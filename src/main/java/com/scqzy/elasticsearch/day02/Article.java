package com.scqzy.elasticsearch.day02;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Description:
 * @Author 盛春强
 * @Date 2021/7/5 17:20
 */
@Getter
@Setter
@ToString
public class Article implements Serializable {
    private long id;
    private String title;
    private String content;
}
