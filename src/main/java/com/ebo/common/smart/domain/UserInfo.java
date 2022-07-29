package com.ebo.common.smart.domain;

import lombok.Data;

@Data
public class UserInfo extends AddressInfo {

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;



}
