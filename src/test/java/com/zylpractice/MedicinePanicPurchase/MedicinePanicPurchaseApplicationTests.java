package com.zylpractice.MedicinePanicPurchase;

import com.zylpractice.MedicinePanicPurchase.entity.Pharmacy;
import com.zylpractice.MedicinePanicPurchase.service.impl.PharmacyServiceImpl;
import com.zylpractice.MedicinePanicPurchase.utils.CacheClient;
import com.zylpractice.MedicinePanicPurchase.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zylpractice.MedicinePanicPurchase.utils.RedisConstants.CACHE_SHOP_KEY;
import static com.zylpractice.MedicinePanicPurchase.utils.RedisConstants.SHOP_GEO_KEY;

@SpringBootTest
class MedicinePanicPurchaseApplicationTests {

    @Resource
    private CacheClient cacheClient;

    @Resource
    private PharmacyServiceImpl pharmacyService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private ExecutorService es = Executors.newFixedThreadPool(500);

    @Test
    void testIdWorker() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(300);

        Runnable task = () -> {
            //@ZYL：是处线程相关知识-/再 ./记
            for (int i = 0; i < 100; i++) {
                long id = redisIdWorker.nextId("order");
                System.out.println("id = " + id);
            }
            latch.countDown();
        };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            es.submit(task);
            //@ZYL：是处线程相关知识-/再
        }
        latch.await();
        long end = System.currentTimeMillis();
        System.out.println("time = " + (end - begin));
    }

    @Test
    void testSavePharmacy() throws InterruptedException {
        Pharmacy pharmacy = pharmacyService.getById(1L);
        cacheClient.setWithLogicalExpire(CACHE_SHOP_KEY + 1L, pharmacy, 10L, TimeUnit.SECONDS);
    }

    @Test
    void loadPharmacyData() {
        // 1.查询药房信息
        List<Pharmacy> list = pharmacyService.list();
        // 2.把药房分组，按照typeId分组，typeId一致的放到一个集合
        Map<Long, List<Pharmacy>> map = list.stream().collect(Collectors.groupingBy(Pharmacy::getTypeId));
        //@ZYL：↑是处stream相关的 -*学~     ！
        // 3.分批完成写入Redis
        for (Map.Entry<Long, List<Pharmacy>> entry : map.entrySet()) {
            // 3.1.获取类型id
            Long typeId = entry.getKey();
            String key = SHOP_GEO_KEY + typeId;
            // 3.2.获取同类型的药房的集合
            List<Pharmacy> value = entry.getValue();
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(value.size());
            // 3.3.写入redis GEOADD key 经度 纬度 member
            for (Pharmacy pharmacy : value) {
                // stringRedisTemplate.opsForGeo().add(key, new Point(pharmacy.getX(), pharmacy.getY()), pharmacy.getId().toString());
                locations.add(new RedisGeoCommands.GeoLocation<>(
                        pharmacy.getId().toString(),
                        new Point(pharmacy.getX(), pharmacy.getY())
                ));
            }
            stringRedisTemplate.opsForGeo().add(key, locations);
        }
    }

}
