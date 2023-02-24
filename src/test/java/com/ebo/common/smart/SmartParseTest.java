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

        //String text = "北京市朝阳区姚家园3楼 13593464918 马云\n";
        //String text = "收货人: 王俊磊 手机号码: 13681992006 所在地区: 上海上海市奉贤区青村镇 详细地址: 金钱公路4801号上海馥臻市场营销策划有限公司\n";
        //UserInfo userInfo = smartParse.parseUserInfo(text);
        //System.out.println(JSONUtil.toJsonStr(userInfo));


        List<String> textList = new ArrayList<>();
        //textList.add("姓名：彭大哥 电话：15521950357 地址：四川南充市营山县城南镇顺安街60号楼1901");
        //textList.add("姓名：彭大哥 电话：15521950357 所在地区: 四川南充市营山县城南镇 详细地址: 顺安街60号楼1901");
        //textList.add("收货人: 彭大哥 手机号码: 15521950357 所在地区: 四川南充市营山县城南镇 详细地址: 顺安街60号楼1901");
        //textList.add("四川南充市营山县城南镇顺安街60号楼1901 15521950357 彭大哥");
        //textList.add("四川省凉山市会理县民族实验中学黎溪镇中厂村9组");
        //textList.add("四川省凉山州市德昌县德州镇角半七社");
        textList.add("萧，13021121177，[黑龙江哈尔滨市南岗区哈西街道]巴黎第九区16-1-1001");
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
        String text = "河南周口西华县长平路东桥";
        AddressInfo addressInfo = smartParse.parseAddressInfo(text, 2);
        System.out.println(JSONUtil.toJsonStr(addressInfo));
    }


}
