package com.learning.bliss.demo.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 用户模块接口实现类
 *
 * @author gblfy
 * @date 2022-03-15
 */
@Slf4j
@Component
@CacheConfig(cacheManager = "caffeineCacheManager", cacheNames = "caffeineBean")
public class CaffeineCacheHandle {

    /**
     * 模拟数据库存储数据
     */
    private HashMap<String, ICaffeineCacheBean> userInfoMap = new HashMap<>();

    @CachePut(key = "#bean.id")
    public ICaffeineCacheBean addUserInfo(ICaffeineCacheBean bean) {
        log.info("create");
        userInfoMap.put(bean.getId(), bean);
        return bean;
    }

    @Cacheable(key = "#id")
    public ICaffeineCacheBean getByName(String id) {
        log.info("get");
        return userInfoMap.get(id);
    }

    @CachePut(key = "#bean.id")
    public ICaffeineCacheBean updateUserInfo(ICaffeineCacheBean bean) {
        log.info("update");
        if (!userInfoMap.containsKey(bean.getId())) {
            return null;
        }
        // 取旧的值
        ICaffeineCacheBean oldBean = userInfoMap.get(bean.getId());
        // 替换内容
        BeanUtils.copyProperties(oldBean, bean);
        // 将新的对象存储，更新旧对象信息
        userInfoMap.put(bean.getId(), bean);
        // 返回新对象信息
        return bean;
    }

    @CacheEvict(key = "#id")
    public void deleteById(String id) {
        log.info("delete");
        userInfoMap.remove(id);
    }

}