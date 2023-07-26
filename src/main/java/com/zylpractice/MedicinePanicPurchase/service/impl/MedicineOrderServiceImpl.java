package com.zylpractice.MedicinePanicPurchase.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.entity.MedicineOrder;
import com.zylpractice.MedicinePanicPurchase.mapper.MedicineOrderMapper;
import com.zylpractice.MedicinePanicPurchase.service.IPanicPurchaseMedicineService;
import com.zylpractice.MedicinePanicPurchase.service.IMedicineOrderService;
import com.zylpractice.MedicinePanicPurchase.utils.RedisIdWorker;
import com.zylpractice.MedicinePanicPurchase.utils.PurchaserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ZYL
 * @since 2023-06-22
 */
@Slf4j
@Service
public class MedicineOrderServiceImpl extends ServiceImpl<MedicineOrderMapper, MedicineOrder> implements IMedicineOrderService {

    @Resource
    private IPanicPurchaseMedicineService panicpurchaseMedicineService;

    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("panicpurchase.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }


    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    @PostConstruct
    //@ZYL：↑-标记下方方法在Spring刚编译完时即执行
    private void init() {
//        SECKILL_ORDER_EXECUTOR.submit(new MedicineOrderHandler());//@ZYL：∝线程池etc-先记./再
    }

    private class MedicineOrderHandler implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    // 1.获取消息队列中的订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS s1 >
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create("stream.orders", ReadOffset.lastConsumed())
                    );
                    // 2.判断订单信息是否为空
                    if (list == null || list.isEmpty()) {
                        // 如果为null，说明没有消息，继续下一次循环
                        continue;
                    }
                    // 解析数据
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    MedicineOrder medicineOrder = BeanUtil.fillBeanWithMap(value, new MedicineOrder(), true);
                    // 3.创建订单
                    createMedicineOrder(medicineOrder);
                    // 4.确认消息 XACK
                    stringRedisTemplate.opsForStream().acknowledge("s1", "g1", record.getId());
                } catch (Exception e) {
                    log.error("处理订单异常", e);
                    handlePendingList();
                }
            }
        }

        private void handlePendingList() {
            while (true) {
                try {
                    // 1.获取pending-list中的订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS s1 0
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create("stream.orders", ReadOffset.from("0"))
                    );
                    // 2.判断订单信息是否为空
                    if (list == null || list.isEmpty()) {
                        // 如果为null，说明没有异常消息，结束循环
                        break;
                    }
                    // 解析数据
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> value = record.getValue();
                    MedicineOrder medicineOrder = BeanUtil.fillBeanWithMap(value, new MedicineOrder(), true);
                    // 3.创建订单
                    createMedicineOrder(medicineOrder);
                    // 4.确认消息 XACK
                    stringRedisTemplate.opsForStream().acknowledge("s1", "g1", record.getId());
                } catch (Exception e) {
                    log.error("处理订单异常", e);
                }
            }
        }
    }

    /*private BlockingQueue<MedicineOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);
    //@ZYL：是处BlockingQueue-阻塞队列 → 队列中没有元素的时候再从队列中获取元素则线程会发生阻塞  ，是则刚好适合用于存储限量药品 限量药品抢完
    的时候就阻塞不让你抢了 增益夫防止超卖之功能
    private class MedicineOrderHandler implements Runnable{

        @Override
        public void run() {
            while (true){
                try {
                    // 1.获取队列中的订单信息
                    MedicineOrder medicineOrder = orderTasks.take();
                    // 2.创建订单
                    createMedicineOrder(medicineOrder);
                } catch (Exception e) {
                    log.error("处理订单异常", e);
                }
            }
        }
    }*/

    private void createMedicineOrder(MedicineOrder medicineOrder) {
        Long purchaserId = medicineOrder.getPurchaserId();
        Long medicineId = medicineOrder.getMedicineId();
        // 创建锁对象
        RLock redisLock = redissonClient.getLock("lock:order:" + purchaserId);
        // 尝试获取锁
        boolean isLock = redisLock.tryLock();
        // 判断
        if (!isLock) {
            // 获取锁失败，直接返回失败或者重试
            log.error("不允许重复下单！");
            return;
        }

        try {
            // 5.1.查询订单
            int count = query().eq("purchaser_id", purchaserId).eq("medicine_id", medicineId).count();
            // 5.2.判断是否存在
            if (count > 0) {
                // 购药者已经购买过了
                log.error("不允许重复下单！");
                return;
            }

            // 6.扣减库存
            boolean success = panicpurchaseMedicineService.update()
                    .setSql("stock = stock - 1") // set stock = stock - 1
                    .eq("medicine_id", medicineId).gt("stock", 0) // where id = ? and stock > 0
                    .update();
            if (!success) {
                // 扣减失败
                log.error("库存不足！");
                return;
            }

            // 7.创建订单
            save(medicineOrder);
        } finally {
            // 释放锁
            redisLock.unlock();
        }
    }

    @Override
    public Result panicpurchaseMedicine(Long medicineId) {
        Long purchaserId = PurchaserHolder.getPurchaser().getId();
        long orderId = redisIdWorker.nextId("order");
        // 1.执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),//@ZYL：是处-空集合之表示方法~    （勿用null  ）
                medicineId.toString(), purchaserId.toString(), String.valueOf(orderId)
        );
        int r = result.intValue();//@ZYL：intValue-Long转换为int类型
        // 2.判断结果是否为0
        if (r != 0) {
            // 2.1.不为0 ，代表没有购买资格
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }
        // 3.返回订单id
        return Result.ok(orderId);
    }

    /*@Override
    public Result panicpurchaseMedicine(Long medicineId) {
        Long purchaserId = PurchaserHolder.getPurchaser().getId();
        // 1.执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                medicineId.toString(), purchaserId.toString()
        );
        int r = result.intValue();
        // 2.判断结果是否为0
        if (r != 0) {
            // 2.1.不为0 ，代表没有购买资格
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }
        // 2.2.为0 ，有购买资格，把下单信息保存到阻塞队列
        MedicineOrder medicineOrder = new MedicineOrder();
        // 2.3.订单id
        long orderId = redisIdWorker.nextId("order");
        medicineOrder.setId(orderId);
        // 2.4.购药者id
        medicineOrder.setPurchaserId(purchaserId);
        // 2.5.药品id
        medicineOrder.setMedicineId(medicineId);
        // 2.6.放入阻塞队列
        orderTasks.add(medicineOrder);

        // 3.返回订单id
        return Result.ok(orderId);
    }*/
    /*@Override
    public Result panicpurchaseMedicine(Long medicineId) {
        // 1.查询限量药品
        PanicPurchaseMedicine Medicine = panicpurchaseMedicineService.getById(medicineId);
        // 2.判断抢购是否开始
        if (Medicine.getBeginTime().isAfter(LocalDateTime.now())) {
            // 尚未开始
            return Result.fail("抢购尚未开始！");
        }
        // 3.判断抢购是否已经结束
        if (Medicine.getEndTime().isBefore(LocalDateTime.now())) {
            // 尚未开始
            return Result.fail("抢购已经结束！");
        }
        // 4.判断库存是否充足
        if (Medicine.getStock() < 1) {
            // 库存不足
            return Result.fail("库存不足！");
        }

        return createMedicineOrder(medicineId);
    }



    @Transactional
    public Result createMedicineOrder(Long medicineId) {
        // 5.一人一单
        Long purchaserId = PurchaserHolder.getPurchaser().getId();

        // 创建锁对象
        RLock redisLock = redissonClient.getLock("lock:order:" + purchaserId);
        // 尝试获取锁
        boolean isLock = redisLock.tryLock();
        // 判断
        if(!isLock){
            // 获取锁失败，直接返回失败或者重试
            return Result.fail("不允许重复下单！");
        }

        try {
            // 5.1.查询订单
            int count = query().eq("purchaser_id", purchaserId).eq("medicine_id", medicineId).count();
            // 5.2.判断是否存在
            if (count > 0) {
                // 购药者已经购买过了
                return Result.fail("购药者已经购买过一次！");
            }

            // 6.扣减库存
            boolean success = panicpurchaseMedicineService.update()
                    .setSql("stock = stock - 1") // set stock = stock - 1
                    .eq("medicine_id", medicineId).gt("stock", 0) // where id = ? and stock > 0
                    .update();
            if (!success) {
                // 扣减失败
                return Result.fail("库存不足！");
            }

            // 7.创建订单
            MedicineOrder medicineOrder = new MedicineOrder();
            // 7.1.订单id
            long orderId = redisIdWorker.nextId("order");
            medicineOrder.setId(orderId);
            // 7.2.购药者id
            medicineOrder.setPurchaserId(purchaserId);
            // 7.3.药品id
            medicineOrder.setMedicineId(medicineId);
            save(medicineOrder);

            // 7.返回订单id
            return Result.ok(orderId);
        } finally {
            // 释放锁
            redisLock.unlock();
        }

    }*/
    /*@Transactional
    public Result createMedicineOrder(Long medicineId) {
        // 5.一人一单
        Long purchaserId = PurchaserHolder.getPurchaser().getId();

        // 创建锁对象
        SimpleRedisLock redisLock = new SimpleRedisLock("order:" + purchaserId, stringRedisTemplate);
        // 尝试获取锁
        boolean isLock = redisLock.tryLock(1200);
        // 判断
        if(!isLock){
            // 获取锁失败，直接返回失败或者重试
            return Result.fail("不允许重复下单！");
        }

        try {
            // 5.1.查询订单
            int count = query().eq("purchaser_id", purchaserId).eq("medicine_id", medicineId).count();
            // 5.2.判断是否存在
            if (count > 0) {
                // 购药者已经购买过了
                return Result.fail("购药者已经购买过一次！");
            }

            // 6.扣减库存
            boolean success = panicpurchaseMedicineService.update()
                    .setSql("stock = stock - 1") // set stock = stock - 1
                    .eq("medicine_id", medicineId).gt("stock", 0) // where id = ? and stock > 0
                    .update();
            if (!success) {
                // 扣减失败
                return Result.fail("库存不足！");
            }

            // 7.创建订单
            MedicineOrder medicineOrder = new MedicineOrder();
            // 7.1.订单id
            long orderId = redisIdWorker.nextId("order");
            medicineOrder.setId(orderId);
            // 7.2.购药者id
            medicineOrder.setPurchaserId(purchaserId);
            // 7.3.药品id
            medicineOrder.setMedicineId(medicineId);
            save(medicineOrder);

            // 7.返回订单id
            return Result.ok(orderId);
        } finally {
            // 释放锁
            redisLock.unlock();
        }

    }*/

    /*@Transactional
    public Result createMedicineOrder(Long medicineId) {
        // 5.一人一单
        Long purchaserId = PurchaserHolder.getPurchaser().getId();

        synchronized (purchaserId.toString().intern()) {//@ZYL：是处*-但锁purchaserId（而不对整个方法加锁 ），且通过.intern确保此purchaserId.
        toString为同一对象而非每次new一个新对象  。是处保证了“一人一单，防止黄牛把限量药品全都抢走抢光  z   ”
            // 5.1.查询订单
            int count = query().eq("purchaser_id", purchaserId).eq("medicine_id", medicineId).count();
            // 5.2.判断是否存在
            if (count > 0) {
                // 购药者已经购买过了
                return Result.fail("购药者已经购买过一次！");
            }

            // 6.扣减库存
            boolean success = panicpurchaseMedicineService.update()
                    .setSql("stock = stock - 1") // set stock = stock - 1
                    .eq("medicine_id", medicineId).gt("stock", 0) // where id = ? and stock > 0
                    .update();
            if (!success) {
                // 扣减失败
                return Result.fail("库存不足！");
            }

            // 7.创建订单
            MedicineOrder medicineOrder = new MedicineOrder();
            // 7.1.订单id
            long orderId = redisIdWorker.nextId("order");
            medicineOrder.setId(orderId);
            // 7.2.购药者id
            medicineOrder.setPurchaserId(purchaserId);
            // 7.3.药品id
            medicineOrder.setMedicineId(medicineId);
            save(medicineOrder);

            // 7.返回订单id
            return Result.ok(orderId);
        }
    }*/
}
