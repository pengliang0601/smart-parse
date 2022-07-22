package com.pengliang.common.smart;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class LocalDataAddressDataLoader implements AddressDataLoader {

    @Override
    public List<Address> loadData() {
        URL url = ResourceUtil.getResource("areaData.json");
        String jsonData = FileUtil.readString(url, Charset.defaultCharset());
        return JSONUtil.parseArray(jsonData).toList(Address.class);
    }

}
