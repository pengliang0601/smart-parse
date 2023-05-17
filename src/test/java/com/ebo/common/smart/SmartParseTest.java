package com.ebo.common.smart;

import cn.hutool.json.JSONUtil;
import com.ebo.common.smart.domain.AddressInfo;
import com.ebo.common.smart.domain.UserInfo;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class SmartParseTest {

    private final SmartParse smartParse = new SmartParse(new LocalDataAddressDataLoader());

    @Test
    void parseUserInfo() {

        List<String> textList = new ArrayList<>();
        textList.add("四川南充市营山县城南镇顺安街60号楼1901 15521950357 彭大哥");
        textList.add("四川省凉山市会理县民族实验中学黎溪镇中厂村9组");
        textList.add("四川省凉山州市德昌县德州镇角半七社");
        textList.add("萧，13021121177，[黑龙江哈尔滨市南岗区哈西街道]巴黎第九区16-1-1001");
        textList.add("王婷婷 ，18639117616 ， ，河南省 许昌市 长葛市 明珠花园10号楼101室");
        textList.add("王婷婷 ，18639712616 ， ，河南-新乡市-长垣市-恼里镇10号楼101室");
        textList.add("收货人: 唐菊\n" +
                "手机号码: 15508153912\n" +
                "所在地区: 四川省南充市营山县城南街道\n" +
                "详细地址: 宝珍花园顺安街21号楼801");
        textList.add("重庆渝北区回兴街道松鹤路261号奥利给副食超市妈妈驿站代收");
        for (String text : textList) {
            UserInfo userInfo = smartParse.parseUserInfo(text);
            System.out.println(JSONUtil.toJsonStr(userInfo));
        }

    }

    @Test
    void parseAddressInfo() {

        //String text = "新疆阿克苏温宿县博孜墩【柯尔克孜族乡吾斯塘博村一组306号 800-8585222 马云";
        //String text = "新疆阿克苏温宿县博孜墩柯尔克孜族乡吾斯塘博村一组306号";
        //String text = "新疆阿克苏温宿县博孜墩柯尔克孜族乡吾斯塘博村一组306号";
        //String text = "天津市滨海新区北塘街道塘沽区心贻湾2-1402";
        //String text = "13593464918 马云\n";
        String text = "河婆街道";
        AddressInfo addressInfo = smartParse.parseAddressInfo(text);
        System.out.println(JSONUtil.toJsonStr(addressInfo));
    }


}
