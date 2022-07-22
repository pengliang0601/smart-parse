package com.pengliang.common.smart.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地址信息
 */
@NoArgsConstructor
@Data
public class Address {


    /**
     * 省
     */
    private String province;

    private String provinceCode;

    /**
     * 市
     */
    private String city;

    private String cityCode;

    /**
     * 区
     */
    private String county;

    private String countyCode;

    /**
     * 街道
     */
    private String street;

    private String streetCode;

    /**
     * 详细地址
     */
    private String address;
}
