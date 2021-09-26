package com.redis.demo;

import com.redis.demo.entity.Site;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Cjl
 * @date 2021/8/11 16:43
 */
public class Test03 {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void testBreak(){
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0;i<10;i++){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    getSiteFromDB();
                }
            };
            executorService.submit(runnable);
        }
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void getSiteFromDB(){
        List<Site> siteList = (List<Site>) redisTemplate.opsForValue().get("site2:list");
        if (Objects.isNull(siteList)){
            siteList = new ArrayList<>();
            Site site1 = new Site(1001L,"北京");
            Site site2 = new Site(1002L,"上海");
            siteList.add(site1);
            siteList.add(site2);
            System.out.println("从数据库获取");
            redisTemplate.opsForValue().set("site2:list",siteList);
        }else {
            System.out.println("从缓存获取");
        }

    }



}
