package com.redis.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Cjl
 * @date 2021/8/11 19:41
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Site implements Serializable {
    private Long id;
    private String name;
}
