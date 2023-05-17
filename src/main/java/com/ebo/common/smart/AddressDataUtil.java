package com.ebo.common.smart;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.List;

public class AddressDataUtil {


    /**
     * 获取制定级别的数据
     *
     * @param data  数据源
     * @param level 制定级别，从0开始
     * @return
     */
    public static List<AddressDataLoader.Address> getAssignLevel(List<AddressDataLoader.Address> data, int level) {
        return getAssignLevel(data, level, 0);
    }

    private static List<AddressDataLoader.Address> getAssignLevel(List<AddressDataLoader.Address> data, int level, int currLevel) {
        if (level == currLevel) {
            return data;
        }
        List<AddressDataLoader.Address> result = new ArrayList<>();
        for (AddressDataLoader.Address datum : data) {
            if (CollUtil.isNotEmpty(datum.getChildren())) {
                result.addAll(getAssignLevel(datum.getChildren(), level, currLevel + 1));
            }
        }
        return result;
    }





}
