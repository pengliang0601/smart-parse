package com.ebo.common.smart.domain;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地址信息
 */
@NoArgsConstructor
@Data
public class AddressInfo {


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

    private String areaId;

    private int level;

    public boolean isEmpty() {
        return StrUtil.isAllEmpty(province, provinceCode, city, cityCode, county, countyCode, street, streetCode, address) && areaId == null;
    }
}
