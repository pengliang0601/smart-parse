package com.pengliang.common.smart;

import org.junit.jupiter.api.Test;

class SmartParseTest {

    private SmartParse smartParse = new SmartParse(new LocalDataAddressDataLoader());

    @Test
    void parseUserInfo() {



    }

    @Test
    void parseAddressInfo() {

        //String text = "新疆阿克苏温宿县博孜墩【柯尔克孜族乡吾斯塘博村一组306号 800-8585222 马云";
        String text = "新疆阿克苏温宿县博孜墩柯尔克孜族乡吾斯塘博村一组306号";
        smartParse.parseAddressInfo(text);




    }
}
