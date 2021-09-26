package com.redis.demo;

import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Cjl
 * @date 2021/8/11 12:35
 */
public class Test01 {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void inject(){
        for (int i =1;i<=10;i++){
            redisTemplate.opsForValue().set("key"+i,"value"+i);
        }
    }
    @Test
    public void queryFromRedis(){
        for (int i =11;i<=20;i++){
            String o = (String) redisTemplate.opsForValue().get("key" + i);
            if (Objects.isNull(o)){
                String s = queryFromMysql(i);
                if (Objects.isNull(s)){
                    s=new String();
                    System.out.println("s = " + s);
                    redisTemplate.opsForValue().set("key"+i,s,300, TimeUnit.SECONDS);
                    System.out.println("查询数据库");
                }else {
                    redisTemplate.opsForValue().set("key"+i,s);
                    System.out.println("查询数据库");
                }
                redisTemplate.opsForValue().set("key"+i,s);
            }
            System.out.println("查询缓存");
        }
    }

    public String queryFromMysql(int id){
        if (id>10){
            return null;
        }
        return "value"+id;
    }

}
