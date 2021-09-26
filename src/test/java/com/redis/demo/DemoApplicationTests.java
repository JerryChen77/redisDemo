package com.redis.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;



    @Test
    public void contextLoads() {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("test03","这是条测试3");
    }

    @Test
    public void contextGetLoads() {
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new StringRedisSerializer());
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String test02 = (String) valueOperations.get("test02");
        System.out.println(test02);
    }


    @Test
    public void pipeline() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        long start = System.currentTimeMillis();
        for (int i = 0;i<10000;i++){
            valueOperations.set("key"+i,"value"+i);
        }
        long end = System.currentTimeMillis();
        System.out.println("消耗时间："+(end-start));
    }

    @Test
    public void pipeline2() {
        long start = System.currentTimeMillis();
        redisTemplate.executePipelined(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                for (int i = 0;i<10000;i++){
                    operations.opsForValue().set("key1"+i,"value3"+i);
                }
                return null;
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("消耗时间2："+(end-start));
    }

    @Test
    public void inject(){
        for (int i =1;i<=10;i++){
            redisTemplate.opsForValue().set("key"+i,"value"+i);
        }
    }
    @Test
    public void queryFromRedis(){
        for (int i =51;i<=60;i++){
            String o = (String) redisTemplate.opsForValue().get("key" + i);
            if (Objects.isNull(o)){
                String s = queryFromMysql(i);
                if (Objects.isNull(s)){
                    s=new String();
                    redisTemplate.opsForValue().set("key"+i,s,300+new Random().nextInt(300),TimeUnit.SECONDS);
                    System.out.println("查询数据库");
                }else {
                    redisTemplate.opsForValue().set("key"+i,s);
                    System.out.println("查询数据库");
                }
                redisTemplate.opsForValue().set("key"+i,s);
                continue;
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
