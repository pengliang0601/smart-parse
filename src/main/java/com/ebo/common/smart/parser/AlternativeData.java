package com.ebo.common.smart.parser;

import com.ebo.common.smart.AddressDataLoader;
import lombok.Data;

@Data
public class AlternativeData {

    public AlternativeData(AddressDataLoader.Address data,
                           AlternativeData parent, int level, String matchValue) {
        this.data = data;
        this.matchValue = matchValue;
        this.parent = parent;
        this.level = level;
        this.areaId = data.getId();

        if (parent != null) {
            this.fullMatchValue = parent.getFullMatchValue() + matchValue;
        } else {
            this.fullMatchValue = matchValue;
        }
    }

    /**
     * 当前级别
     */
    private int level;
    private AddressDataLoader.Address data;
    private AlternativeData parent;
    private String matchValue;

    /**
     * 完整的匹配值
     */
    private String fullMatchValue = "";

    private String areaId;


}
