package com.pengliang.common.smart;

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
        private Serializable id;
        private String name;
        private String fullName;
        private String code;
        private Serializable parentId;
        private List<Address> children;
    }
}
