package com.ebo.common.smart.parser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.ebo.common.smart.AddressDataLoader;
import com.ebo.common.smart.AddressDataUtil;
import com.ebo.common.smart.RegexConstant;
import com.ebo.common.smart.TextHolder;
import com.ebo.common.smart.domain.UserInfo;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 解析地区
 */
public class AreaParse {

    private final AddressDataLoader addressDataLoader;

    /**
     * 地名标识常量
     */
    public static final String PLACE_NAME_CHAR = "省市区县州街道镇乡特别行政自治";

    /**
     * 前缀字符特殊处理，匹配时候会自动处理掉符合正则的文字
     */
    private Pattern keyCharPattern = Pattern.compile("^[" + PLACE_NAME_CHAR + "]+");

    private final Map<Serializable, AddressDataLoader.Address> areaMap = new HashMap<>();
    private boolean initAreaMap = false;

    public AreaParse(AddressDataLoader addressDataLoader) {
        this.addressDataLoader = addressDataLoader;
    }


    public void parse(UserInfo userInfo, List<String> textList, TextHolder textHolder) {

        textHolder.removeText(" ");
        parseArea(userInfo, textHolder);

        // 获取详细地址
        userInfo.setAddress(textHolder.getText());

    }


    public void parseArea(UserInfo userInfo, TextHolder textHolder) {

        String text = textHolder.getText();
        List<AddressDataLoader.Address> addresses = addressDataLoader.loadData();

        // 匹配省
        List<AlternativeData> provinceList = match(text, 0, addresses, null);

        // 匹配市
        List<AlternativeData> cityList = bestMatch(text, 1, provinceList);
        // 如果到市级都还没匹配到合适的，则不符合地址规范，直接退出
        if (cityList.isEmpty()) {
            return;
        }
        // 匹配区
        List<AlternativeData> areaList = bestMatch(text, 2, cityList);

        // 匹配街道
        List<AlternativeData> streetList = bestMatch(text, 3, areaList);

        AlternativeData result;
        if (streetList.isEmpty()) {
            result = getResult(areaList);
        } else if (!areaList.isEmpty()){
            result = getResult(streetList);
        } else if (!cityList.isEmpty()){
            result = getResult(cityList);;
        }else{
            result = getResult(streetList);;
        }

        // 填充省市区
        if (result != null) {
            fillUserInfo(userInfo, result.getData(), result.getLevel());
            String removeBef = textHolder.getText();
            textHolder.removeText(result.getFullMatchValue());

            if (removeBef.equals(textHolder.getText())) {
                // 如果删除前和删除后还一样，则根据省市区来删除
                textHolder.removeText(userInfo.getProvince());
                textHolder.removeText(userInfo.getCity());
                textHolder.removeText(userInfo.getCounty());
                textHolder.removeText(userInfo.getStreet());
            }
        }


    }

    void fillUserInfo(UserInfo userInfo, AddressDataLoader.Address result, int level) {
        if (result == null || level < 0) {
            return;
        }
        if (level == 0) {
            userInfo.setProvince(result.getName());
            userInfo.setProvinceCode(result.getCode());
        } else if (level == 1) {
            userInfo.setCity(result.getName());
            userInfo.setCityCode(result.getCode());
        } else if (level == 2) {
            userInfo.setCounty(result.getName());
            userInfo.setCountyCode(result.getCode());
        } else if (level == 3) {
            userInfo.setStreet(result.getName());
            userInfo.setStreetCode(result.getCode());
        }
        if (userInfo.getLevel() < level) {
            userInfo.setLevel(level);
            userInfo.setAreaId(result.getId());
        }
        if (result.getParentId()!=null) {
            fillUserInfo(userInfo, getAddressByMap(result.getParentId().toString()), level - 1);
        }
    }

    public AddressDataLoader.Address getAddressByMap(String id) {
        if (!initAreaMap) {
            initAreaMap();
        }
        return areaMap.get(id);
    }


    private synchronized void initAreaMap() {
        if (initAreaMap) {
          return;
        }
        initAreaMap(addressDataLoader.loadData());
        initAreaMap = true;
    }

    private void initAreaMap(List<AddressDataLoader.Address> addresses) {
        if (CollUtil.isEmpty(addresses)) {
            return;
        }
        for (AddressDataLoader.Address loadDatum : addresses) {
            areaMap.put(loadDatum.getId(), loadDatum);
            initAreaMap(loadDatum.getChildren());
        }
    }


    /**
     * 获取结果
     * @return
     */

    AlternativeData getResult(List<AlternativeData> dataList) {

        if (CollUtil.isEmpty(dataList) ) {
            return null;
        }
        AlternativeData data = dataList.get(0);
        for (AlternativeData alternativeData : dataList) {
            if (alternativeData.getMatchValue().length() > data.getMatchValue().length()) {
                data = alternativeData;
            }
        }
        return data;
    }


    /**
     * 解析符合条件的结果
     *
     * @param text
     * @param provinceList
     * @return
     */
    public List<AlternativeData> bestMatch(String text, int level, List<AlternativeData> provinceList) {

        List<AddressDataLoader.Address> addressList = AddressDataUtil.getAssignLevel(addressDataLoader.loadData(), level);
        if (CollUtil.isEmpty(provinceList)) {
            return match(text, level, addressList, null);
        }

        Map<Serializable, List<AddressDataLoader.Address>> groupList = addressList.stream()
                .collect(Collectors.groupingBy(AddressDataLoader.Address::getParentId));
        List<AlternativeData> alternativeList = new ArrayList<>();
        for (AlternativeData alternativeData : provinceList) {
            groupList.forEach((parentId, addresses) -> {
                if (!alternativeData.getAreaId().equals(parentId.toString())) {
                    return;
                }
                alternativeList.addAll(match(text, level, addresses, alternativeData));
            });
        }
        return alternativeList;
    }


    public List<AlternativeData> match(String text, int level, List<AddressDataLoader.Address> addressList,
                                       AlternativeData parentData) {

        if (CollUtil.isEmpty(addressList)) {
            return Collections.emptyList();
        }

        if (parentData != null) {
            text = text.replace(parentData.getFullMatchValue(), "");
        }

        text = ReUtil.replaceFirst(keyCharPattern, text, "");

        Map<String, AlternativeData> matchProvince = new HashMap<>();
        for (int endIndex = 0; endIndex < text.length(); endIndex++) {
            String keyword = StrUtil.subWithLength(text, 0, endIndex + 2);

            // 清楚特殊字符后
            String clearSpecialLat = ReUtil.replaceAll(keyword, RegexConstant.SPECIAL, "");
            for (AddressDataLoader.Address data : addressList) {
                // 如果名称与地址一致，则为最有匹配
                if (clearSpecialLat.equals(data.getName())) {
                    return ListUtil.of(new AlternativeData(data, parentData, level, keyword));
                }
                if (data.getName().contains(clearSpecialLat)) {
                    matchProvince.put(data.getId(), new AlternativeData(data, parentData, level, keyword));
                }
            }
        }

        if (matchProvince.isEmpty()) {
            // 重庆有重庆城区和县区，所以这里size<=2
            if (addressList.size() <= 2 ) {
                // 取前两个字
                List<AlternativeData> data = new ArrayList<>();
                for (AddressDataLoader.Address address : addressList) {
                    data.add(new AlternativeData(address, parentData, level, ""));
                }
                return data;
            }
        }
        return new ArrayList<>(matchProvince.values());
    }

}
