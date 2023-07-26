package com.zylpractice.MedicinePanicPurchase.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.zylpractice.MedicinePanicPurchase.entity.PharmacyType;
import com.zylpractice.MedicinePanicPurchase.mapper.PharmacyTypeMapper;
import com.zylpractice.MedicinePanicPurchase.service.IPharmacyTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.zylpractice.MedicinePanicPurchase.utils.RedisConstants.ZYL_HASH_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZYL
 * @since 2023-06-22
 */

@Service
public class PharmacyTypeServiceImpl extends ServiceImpl<PharmacyTypeMapper, PharmacyType> implements IPharmacyTypeService {

//@ZYL：-----------------------------------------------自己写代码--------------------------------------------

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public List<PharmacyType> queryTypeList() {

////      （1）↓~-用String类型，opsForValue缓存的
//        String key = "Cache:TypeList:String";
//        //@ZYL：先查找缓存
//        String pharmacyTypeJson = stringRedisTemplate.opsForValue().get(key);
//
//        //@ZYL：缓存找到了就直接返回
//        if (StrUtil.isNotBlank(pharmacyTypeJson)) {
//            List<PharmacyType> pharmacyTypeList = JSONUtil.toList(pharmacyTypeJson, PharmacyType.class);
//            return pharmacyTypeList;
//        }
//
//        //@ZYL：缓存找不到(隐含逻辑：if判断失败了，此处相当于else部分 )，再找数据库
//        List<PharmacyType> pharmacyTypeList = query().orderByAsc("sort").list();
//        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(pharmacyTypeList));
//
//        return pharmacyTypeList;

//
////      （2）↓~-用List类型，opsForList缓存的
//        String key = "Cache:TypeList:List";
//        //@ZYL：先查找缓存
//        String pharmacyTypeString = stringRedisTemplate.opsForList().leftPop(key);
//
//        //@ZYL：缓存找到了就直接返回
//        if (StrUtil.isNotBlank(pharmacyTypeString)) {
//            return JSONUtil.toList(pharmacyTypeString,PharmacyType.class);
//            //@ZYL：       ↑万*-JSONUtil.toList     ！★
//        }
//
//        //@ZYL：缓存找不到(隐含逻辑：if判断失败了，此处相当于else部分 )，再找数据库
//        List<PharmacyType> pharmacyTypeList = query().orderByAsc("sort").list();
//        stringRedisTemplate.opsForList().leftPush(key, JSONUtil.toJsonStr(pharmacyTypeList));
//        //@ZYL：                                               ↑万*-JSONUtil.toJsonStr     ！★
//
//        return pharmacyTypeList;


////      （3）↓~-用ZSet类型，opsForzset缓存的
//        String key = "Cache:TypeList:ZSet";
//        //@ZYL：先查找缓存
//        String pharmacyTypeString = stringRedisTemplate.opsForZSet().randomMember(key);
//
//        //@ZYL：缓存找到了就直接返回
//        if (StrUtil.isNotBlank(pharmacyTypeString)) {
//            return JSONUtil.toList(pharmacyTypeString,PharmacyType.class);
//            //@ZYL：       ↑万*-JSONUtil.toList     ！★
//        }
//
//        //@ZYL：缓存找不到(隐含逻辑：if判断失败了，此处相当于else部分 )，再找数据库
//        List<PharmacyType> pharmacyTypeList = query().orderByAsc("sort").list();
//        stringRedisTemplate.opsForZSet().add(key, JSONUtil.toJsonStr(pharmacyTypeList),0);
//        //@ZYL：                                               ↑万*-JSONUtil.toJsonStr     ！★
//
//        return pharmacyTypeList;


//      （4）↓~-用hash类型，opsForHash缓存的  //@ZYL：Hash的用Map转String的方法-试了一下，最终没成功，用String类型（JSON字符串）接收
//      Map类型，最终也不知道怎么把String类型再转为Map类型了（上网搜了几个方法API试了一下，没试成功）；而这里的为用Object类型接收的
//                                                                            ↑将来/再  ，也有可能将来就算API能用了也无法”List
//                                                                            类型转String类型转Map类型     “
        String key = "Cache:TypeList:Hash";
        //@ZYL：先查找缓存
        Object pharmacyTypeObject = stringRedisTemplate.opsForHash().get(key,ZYL_HASH_KEY);
        if(pharmacyTypeObject!=null) {
            String pharmacyTypeString = pharmacyTypeObject.toString();
            //@ZYL：缓存找到了就直接返回
            if (StrUtil.isNotBlank(pharmacyTypeString)) {
                return JSONUtil.toList(pharmacyTypeString, PharmacyType.class);
                //@ZYL：       ↑万*-JSONUtil.toList     ！★
            }
        }

        //@ZYL：缓存找不到(隐含逻辑：if判断失败了，此处相当于else部分 )，再找数据库
        List<PharmacyType> pharmacyTypeList = query().orderByAsc("sort").list();
        stringRedisTemplate.opsForHash().put(key,ZYL_HASH_KEY,JSONUtil.toJsonStr(pharmacyTypeList));
        //@ZYL：                                               ↑万*-JSONUtil.toJsonStr     ！★

        return pharmacyTypeList;

    }
}
