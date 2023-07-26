package com.zylpractice.MedicinePanicPurchase.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zylpractice.MedicinePanicPurchase.dto.Result;
import com.zylpractice.MedicinePanicPurchase.entity.Pharmacy;
import com.zylpractice.MedicinePanicPurchase.mapper.PharmacyMapper;
import com.zylpractice.MedicinePanicPurchase.service.IPharmacyService;
import com.zylpractice.MedicinePanicPurchase.utils.CacheClient;
import com.zylpractice.MedicinePanicPurchase.utils.SystemConstants;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.zylpractice.MedicinePanicPurchase.utils.RedisConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZYL
 * @since 2023-06-22
 */
@Service
public class PharmacyServiceImpl extends ServiceImpl<PharmacyMapper, Pharmacy> implements IPharmacyService {


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;

    @Override
    public Result queryById(Long id) {
        // 解决缓存穿透
        Pharmacy pharmacy = cacheClient
                .queryWithPassThrough(CACHE_SHOP_KEY, id, Pharmacy.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);
        //@ZYL：                                                           ↑-为“id2->getById(id2)”简写而来   ★-∝方法引用
        // etc /再    !

        // 互斥锁解决缓存击穿
        // Pharmacy pharmacy = cacheClient
        //         .queryWithMutex(CACHE_SHOP_KEY, id, Pharmacy.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);

        // 逻辑过期解决缓存击穿
        // Pharmacy pharmacy = cacheClient
        //         .queryWithLogicalExpire(CACHE_SHOP_KEY, id, Pharmacy.class, this::getById, 20L, TimeUnit.SECONDS);

        if (pharmacy == null) {
            return Result.fail("药房不存在！");
        }
        // 7.返回
        return Result.ok(pharmacy);
    }

    @Override
    @Transactional
    //@ZYL： ↑ 是处*-事务处理（便于~后续抛异常时统一回滚   ）
    public Result update(Pharmacy pharmacy) {
        Long id = pharmacy.getId();
        if (id == null) {
            return Result.fail("药房id不能为空");
        }
        // 1.更新数据库
        updateById(pharmacy);
        // 2.删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);
        return Result.ok();
    }

    @Override
    public Result queryPharmacyByType(Integer typeId, Integer current, Double x, Double y) {
        // 1.判断是否需要根据坐标查询
        if (x == null || y == null) {
            // 不需要坐标查询，按数据库查询
            Page<Pharmacy> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            // 返回数据
            return Result.ok(page.getRecords());
        }

        // 2.计算分页参数
        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;

        // 3.查询redis、按照距离排序、分页。结果：pharmacyId、distance
        String key = SHOP_GEO_KEY + typeId;
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo() // GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
                .search(
                        key,
                        GeoReference.fromCoordinate(x, y),
                        new Distance(5000),
                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
                );
        // 4.解析出id
        if (results == null) {
            return Result.ok(Collections.emptyList());
        }
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
        if (list.size() <= from) {
            // 没有下一页了，结束
            return Result.ok(Collections.emptyList());
        }
        // 4.1.截取 from ~ end的部分
        List<Long> ids = new ArrayList<>(list.size());
        Map<String, Distance> distanceMap = new HashMap<>(list.size());
        list.stream().skip(from).forEach(result -> {
            // 4.2.获取药房id
            String pharmacyIdStr = result.getContent().getName();
            ids.add(Long.valueOf(pharmacyIdStr));
            // 4.3.获取距离
            Distance distance = result.getDistance();
            distanceMap.put(pharmacyIdStr, distance);
        });
        // 5.根据id查询Pharmacy
        String idStr = StrUtil.join(",", ids);
        List<Pharmacy> pharmacys = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        for (Pharmacy pharmacy : pharmacys) {
            pharmacy.setDistance(distanceMap.get(pharmacy.getId().toString()).getValue());
        }
        // 6.返回
        return Result.ok(pharmacys);
    }
}
