package com.pengliang.common.smart.domain;

import lombok.Data;

@Data
public class UserInfo extends Address{

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;



}
