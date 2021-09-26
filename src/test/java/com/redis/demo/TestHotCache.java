
package com.redis.demo;
import com.redis.demo.entity.Site;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 热点缓存重建优化
 */
@SpringBootTest
public class TestHotCache {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;


    @Test
    public void testBreak(){

        //创建多线程。模拟并发，让100条线程一起执行getSiteFromDB()这个方法。
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            //被线程池执行的任务
            Runnable runnable = new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    getSiteFromDB();
                }
            };
            threadPool.submit(runnable);
        }

        try {
            Thread.sleep(100000000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getSiteFromDB() throws InterruptedException {

        //1.先从缓存中拿数据
        List<Site> siteList = (List<Site>) redisTemplate.opsForValue().get("site6:list");
        if(Objects.isNull(siteList)){
            //String uuid = UUID.randomUUID().toString();
            //Boolean lock = redisTemplate.opsForValue().setIfAbsent("site:lock", uuid, 60, TimeUnit.SECONDS);
            RLock lock = redissonClient.getLock("site:redisson:lock");
            //if (lock){
                lock.lock();
                try {
                    //2.缓存中没有，去数据库中拿
                    //模拟数据库中
                    siteList = new ArrayList<>();
                    Site site1 = new Site();
                    Site site2 = new Site();
                    site1.setId(1001L);
                    site1.setName("人脸识别");
                    site2.setId(1002L);
                    site2.setName("测温闸机");
                    siteList.add(site1);
                    siteList.add(site2);
                    System.out.println("从数据库获取数据");
                    //3.存入到缓存里面
                    redisTemplate.opsForValue().set("site6:list",siteList,60, TimeUnit.SECONDS);
                } finally {
                   lock.unlock();
                    //if (uuid.equals(redisTemplate.opsForValue().get("site:lock"))){
                    //    redisTemplate.delete("site:lock");
                    //}
                }
            //}{
            //    Thread.sleep(10);
            //    getSiteFromDB();
            //}
        }else {
            System.out.println("从缓存中获取");
        }
    }



}
