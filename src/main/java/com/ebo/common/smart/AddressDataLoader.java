package com.ebo.common.smart;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 地址数据加载器
 */
public interface AddressDataLoader {

    List<Address> loadData();

    @Data
    class Address {
        private String id;
        private String name;
        private String code;
        private Serializable parentId;
        private List<Address> children;
    }
}
