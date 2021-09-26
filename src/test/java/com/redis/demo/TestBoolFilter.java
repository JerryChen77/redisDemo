package com.redis.demo;

import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Cjl
 * @date 2021/8/11 16:12
 */
public class TestBoolFilter {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void bloomFilter(){
        Config config = new Config();
        //config.useSingleServer().setAddress("redis://192.168.140.129:7001");
        config.useClusterServers().addNodeAddress("redis://192.168.140.129:7001");
        //config.useSingleServer().setPassword("qfjava");
        //构造Redisson
        RedissonClient redisson = Redisson.create(config);

        RBloomFilter<String> bloomFilter = redisson.getBloomFilter("nameList");
        //初始化布隆过滤器：预计元素为100000000L,误差率为3%,根据这两个参数会计算出底层的bit数组大小
        bloomFilter.tryInit(100000000L,0.03);
        //将xiaoming插入到布隆过滤器中
        bloomFilter.add("xiaoming");

        //判断下面号码是否在布隆过滤器中
        System.out.println(bloomFilter.contains("xiaoli"));//false
        System.out.println(bloomFilter.contains("xiaowang"));//false
        System.out.println(bloomFilter.contains("xiaoming"));//true



    }


}
