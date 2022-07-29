package com.ebo.common.smart;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class LocalDataAddressDataLoader implements AddressDataLoader {

    private final List<Address> addressList;

    public LocalDataAddressDataLoader() {
        URL url = ResourceUtil.getResource("areaData.json");
        String jsonData = FileUtil.readString(url, Charset.defaultCharset());
        this.addressList = JSONUtil.parseArray(jsonData).toList(Address.class);
    }

    @Override
    public List<Address> loadData() {
        return addressList;
    }

}
