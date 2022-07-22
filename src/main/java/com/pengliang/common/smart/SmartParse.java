package com.pengliang.common.smart;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.pengliang.common.smart.domain.Address;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SmartParse {

    private AddressDataLoader addressDataLoader;
    public SmartParse(AddressDataLoader addressDataLoader) {
        this.addressDataLoader = addressDataLoader;
    }

    /**
     * 解析用户地址
     * @param text
     */
    public void parseUserInfo(String text) {




    }

    /**
     * 解析地址
     */
    public Address parseAddressInfo(String text) {

        List<AddressDataLoader.Address> addressList = addressDataLoader.loadData();

        Address address = new Address();

        text = filterStr(text);
        List<String> split = StrUtil.split(text, " ");
        for (String str : split) {
            if (StrUtil.isEmpty(str)) {
                continue;
            }
            System.out.println(JSONUtil.toJsonStr(matchAddress(addressList, str)));
        }

        return null;
    }


    /**
     * 匹配地址
     * @param areaDataList
     * @param text
     */
    private List<Address> matchAddress(List<AddressDataLoader.Address> areaDataList, String text) {

        if (StrUtil.isBlank(text)) {
            return Collections.emptyList();
        }

        //省匹配 比如输入北京市朝阳区，会用北  北京  北京市 北京市朝 以此类推在addressList里的province中做匹配，会得到北京市  河北省 天津市等等；
        List<MatchData> matchProvinceList = new ArrayList<>(); //粗略匹配上的省份
        for (AddressDataLoader.Address address : areaDataList) {
            String name = StrUtil.subWithLength(address.getName(), 0, 2);
            int i = text.indexOf(name);
            if (i > -1) {
                MatchData matchData = new MatchData(address.getName(), i + 2);
                matchProvinceList.add(matchData);
            }
        }

        // 匹配市，如果前面匹配到省了，则直接重下一级去匹配
        List<MatchData> matchCityList = new ArrayList<>();
        for (AddressDataLoader.Address province : areaDataList) {
            if (matchProvinceList.isEmpty() || CollUtil.contains(matchProvinceList, a -> a.getName().equals(province.getName()))) {
                List<AddressDataLoader.Address> children = province.getChildren();
                if (CollUtil.isEmpty(children)) {
                    continue;
                }
                for (AddressDataLoader.Address city : children) {
                    String name = StrUtil.subWithLength(province.getName(), 0, 2);
                    int i = text.indexOf(name);
                    if (i>-1) {
                        MatchData matchData = new MatchData(city.getName(), i + 2);
                        matchCityList.add(matchData);
                    }
                }
            }
        }

        // 匹配区
        List<MatchData> matchCountyList = new ArrayList<>();
        for (AddressDataLoader.Address province : areaDataList) { // 省
            if (matchProvinceList.isEmpty() || CollUtil.contains(matchProvinceList, a -> a.getName().equals(province.getName()))) {
                for (AddressDataLoader.Address city : province.getChildren()) {// 市
                    MatchData matchCity = CollUtil.findOne(matchCityList, a -> a.getName().equals(city.getName()));
                    if (matchCityList.isEmpty() || matchCity != null) {
                        int fromIndex = matchCity != null ? matchCity.getIndex() : 0;
                        for (AddressDataLoader.Address county : city.getChildren()) { // 区
                            String name = StrUtil.subWithLength(county.getName(), 0, 2);
                            int i = text.indexOf(name, fromIndex);
                            if (i > -1) {
                                MatchData matchData = new MatchData(county.getName(), i + 2);
                                matchCountyList.add(matchData);
                            }
                        }
                    }
                }
            }
        }

        // 匹配街道
        List<Address> matchResultList = new ArrayList<>();
        for (AddressDataLoader.Address province : areaDataList) { // 省
            if (matchProvinceList.isEmpty() || CollUtil.contains(matchProvinceList, a -> a.getName().equals(province.getName()))) {
                for (AddressDataLoader.Address city : province.getChildren()) {// 市
                    if (matchCityList.isEmpty() || CollUtil.contains(matchCityList, a -> a.getName().equals(city.getName()))) {
                        for (AddressDataLoader.Address county : city.getChildren()) { // 区

                            if (county.getChildren() == null) {
                                continue;
                            }

                            MatchData matchData = CollUtil.findOne(matchCountyList, a -> a.getName().equals(county.getName()));
                            if (matchCountyList.isEmpty() || matchData != null) {
                                for (AddressDataLoader.Address streety : county.getChildren()) { // 街道
                                    String name = StrUtil.subWithLength(streety.getName(), 0, 2);
                                    int fromIndex = matchData != null ? matchData.getIndex() : 0;
                                    int i = text.indexOf(name, fromIndex);
                                    if (i > -1) {
                                        Address address = new Address();
                                        address.setProvince(province.getName());
                                        address.setProvinceCode(province.getCode());
                                        address.setCity(city.getName());
                                        address.setCityCode(city.getCode());
                                        address.setCounty(county.getName());
                                        address.setCountyCode(county.getCode());
                                        address.setStreet(streety.getName());
                                        address.setStreetCode(streety.getCode());
                                        matchResultList.add(address);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
        return matchResultList;
    }




    public String filterStr(String text) {
        text = ReUtil.replaceAll(text, "[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“’。，、？-]", " ");
        return StrUtil.replace(text, "[\\r\\n]", "");
    }


}


/**
 *
 * 匹配上的数据
 */
@Data
class MatchData {

    public MatchData(String name, int index) {
        this.name = name;
        this.index = index;
    }

    String name;
    /**
     * 匹配中的下标，辅助字段
     */
    private int index;
}
