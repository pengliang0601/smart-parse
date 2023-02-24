package com.ebo.common.smart;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.ebo.common.smart.domain.AddressInfo;
import com.ebo.common.smart.domain.UserInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class SmartParse {

    /**
     * 前缀字符特殊处理，匹配时候会自动处理掉符合正则的文字
     */
    private Pattern pattern = Pattern.compile("^[省市区县州街道镇乡特别行政自治]+");
    private final AddressDataLoader addressDataLoader;


    public SmartParse(AddressDataLoader addressDataLoader) {
        this.addressDataLoader = addressDataLoader;
    }


    public UserInfo parseUserInfo(String text) {
        return parseUserInfo(text, null);
    }

    /**
     * 解析用户地址信息
     *
     * @param text 地址信息
     */
    public UserInfo parseUserInfo(String text, Integer level) {
        if (StrUtil.isBlank(text)) {
            return null;
        }

        text = text.replace(" 详细地址: ", "");
        StringBuilder matchText = new StringBuilder();
        // 先根据空白符分割，如果空白符分割包含：号，则默认取最后那一段
        for (String str : StrSplitter.splitByRegex(text, "[\\n\\r]", 0, true, true)) {
            str = str.replace(": ", ":");
            if (StrUtil.isBlank(str)) {
                continue;
            }
            for (String s : StrSplitter.splitByRegex(str, "，| ", 0, true, true)) {
                List<String> strings = StrSplitter.splitByRegex(s, "[:：]", 0, true, true);
                if (CollUtil.isEmpty(strings)) {
                    continue;
                }
                if (strings.size() == 2) {
                    matchText.append(strings.get(1));
                } else {
                    matchText.append(strings.get(0));
                }
                matchText.append(" ");
            }


        }
        text = matchText.toString();

        UserInfo userInfo = new UserInfo();
        String mobile = matchMobile(text);
        if (StrUtil.isNotEmpty(mobile)) {
            userInfo.setMobile(mobile);
            text = text.replace(mobile, "");
        }

        //text = filterStr(text);
        List<AddressDataLoader.Address> addressList = addressDataLoader.loadData();

        List<String> split = StrUtil.split(text, " ");
        for (String str : split) {
            if (StrUtil.isBlank(str)) {
                continue;
            }
            AddressInfo addressInfo = null;
            // 大于6才能判断是一串地址信息
            if (str.length() > 6) {
                addressInfo = matchAddress(addressList, str, level);
            }
            if (str.length() <= 6) {
                userInfo.setName(str);
            } else {
                BeanUtil.copyProperties(addressInfo, userInfo, CopyOptions.create().ignoreNullValue());
            }

        }
        return userInfo;
    }


    /**
     * 匹配手机号码
     *
     * @param text
     * @return
     */
    public String matchMobile(String text) {
        String mobile = ReUtil.getGroup0(RegexPool.MOBILE, text);
        if (StrUtil.isNotEmpty(mobile)) {
            return mobile;
        }

        mobile = ReUtil.getGroup0(RegexPool.TEL, text);
        if (StrUtil.isNotEmpty(mobile)) {
            return mobile;
        }

        mobile = ReUtil.getGroup0(RegexPool.TEL_400_800, text);
        if (StrUtil.isNotEmpty(mobile)) {
            return mobile;
        }
        mobile = ReUtil.getGroup0(RegexPool.MOBILE_HK, text);
        if (StrUtil.isNotEmpty(mobile)) {
            return mobile;
        }
        mobile = ReUtil.getGroup0(RegexPool.MOBILE_TW, text);
        if (StrUtil.isNotEmpty(mobile)) {
            return mobile;
        }
        return ReUtil.getGroup0(RegexPool.MOBILE_MO, text);
    }

    public AddressInfo parseAddressInfo(String text) {
        return parseAddressInfo(text, null);
    }

    /**
     * 解析地址
     * @param level 匹配级别。从0开始，可以选择只匹配到第几级，为null则忽略
     */
    public AddressInfo parseAddressInfo(String text, Integer level) {

        if (StrUtil.isBlank(text)) {
            return null;
        }

        List<AddressDataLoader.Address> addressList = addressDataLoader.loadData();
        AddressInfo addressInfo = new AddressInfo();

        //text = filterStr(text);
        List<String> split = StrUtil.split(text, " ");
        for (String str : split) {
            AddressInfo info = matchAddress(addressList, str, level);
            if (info != null && !info.isEmpty()) {
                BeanUtil.copyProperties(info, addressInfo, CopyOptions.create().ignoreNullValue());
            }
        }
        return addressInfo;
    }


    /**
     * 匹配地址
     *
     * @param addressList 地址列表
     * @param text        匹配的地址信息
     * @param level 匹配级别。从0开始，可以选择只匹配到第几级，为null则忽略
     */
    private AddressInfo matchAddress(List<AddressDataLoader.Address> addressList, String text, Integer level) {

        if (StrUtil.isBlank(text)) {
            return null;
        }
        AddressInfo info = new AddressInfo();
        // 清除特殊字符
        text = ReUtil.replaceAll(text, "[^\u4e00-\u9fa5A-Za-z0-9-]", "");

        String address = text;

        String matchAddress = "";
        List<MatchData> matchProvince = new ArrayList<>();
        for (int endIndex = 0; endIndex < text.length(); endIndex++) {
            matchAddress = StrUtil.subWithLength(text, 0, endIndex + 2);
            for (AddressDataLoader.Address province : addressList) {
                if (province.getName().contains(matchAddress)) {
                    matchProvince.add(new MatchData(province, null, null, null, matchAddress));
                }
            }
        }

        if (!matchProvince.isEmpty()) {
            MatchData matchData = getTheOptimalMatch(matchProvince);
            setMatchInfo(info, matchData);
            text = text.replaceFirst(matchData.getMatchValue(), "");
            text = ReUtil.replaceFirst(pattern, text, "");
        }
        if (level != null && level == 0) {
            setAddress(matchProvince, address, text, info);
            return info;
        }

        //市查找
        List<MatchData> matchCity = new ArrayList<>(); //粗略匹配上的市
        for (int endIndex = 0; endIndex < text.length(); endIndex++) {
            matchAddress = StrUtil.subWithLength(text, 0, endIndex + 2);
            for (AddressDataLoader.Address province : addressList) {
                if (province.getChildren() == null) {
                    continue;
                }
                if (info.getProvince() == null || province.getName().equals(info.getProvince())) {
                    for (AddressDataLoader.Address city : province.getChildren()) {
                        if (city.getName().contains(matchAddress)) {
                            matchCity.add(new MatchData(province, city, null, null, matchAddress));
                        }
                    }
                }
            }
        }
        if (!matchCity.isEmpty()) {
            MatchData matchData = getTheOptimalMatch(matchCity);
            setMatchInfo(info, matchData);
            text = text.replaceFirst(matchData.getMatchValue(), "");
            // 如果是市开头的，去掉
            text = ReUtil.replaceFirst(pattern, text, "");
        }

        if (level != null && level == 1) {
            setAddress(matchProvince, address, text, info);
            return info;
        }


        //区县查找
        List<MatchData> matchCounty = new ArrayList<>(); //粗略匹配上的区县
        for (int endIndex = 0; endIndex < text.length(); endIndex++) {
            matchAddress = StrUtil.subWithLength(text, 0, endIndex + 2);

            for (AddressDataLoader.Address province : addressList) {
                if (province.getChildren() == null) {
                    continue;
                }
                if (info.getProvince() != null && !info.getProvince().equals(province.getName())) {
                    continue;
                }
                for (AddressDataLoader.Address city : province.getChildren()) {// 市
                    if (CollUtil.isEmpty(city.getChildren())) {
                        continue;
                    }
                    if (info.getCity() != null && !info.getCity().equals(city.getName())) {
                        continue;
                    }
                    for (AddressDataLoader.Address county : city.getChildren()) { // 区
                        if (county.getName().contains(matchAddress)) {
                            matchCounty.add(new MatchData(province, city, county, null, matchAddress));
                        }
                    }
                }
            }
        }
        if (!matchCounty.isEmpty()) {
            MatchData matchData = getTheOptimalMatch(matchCounty);
            setMatchInfo(info, matchData);
            text = text.replaceFirst(matchData.getMatchValue(), "");
            text = ReUtil.replaceFirst(pattern, text, "");
        }

        if (level != null && level == 2) {
            setAddress(matchProvince, address, text, info);
            return info;
        }

        //街道查找
        List<MatchData> matchStreet = new ArrayList<>(); //粗略匹配上的街道查
        for (int endIndex = 0; endIndex < text.length(); endIndex++) {
            matchAddress = StrUtil.subWithLength(text, 0, endIndex + 2);

            for (AddressDataLoader.Address province : addressList) {
                if (province.getChildren() == null) {
                    continue;
                }
                if (info.getProvince() != null && !info.getProvince().equals(province.getName())) {
                    continue;
                }
                for (AddressDataLoader.Address city : province.getChildren()) {// 市
                    if (city.getChildren() == null) {
                        continue;
                    }
                    if (info.getCity() != null && !info.getCity().equals(city.getName())) {
                        continue;
                    }
                    for (AddressDataLoader.Address county : city.getChildren()) { // 区
                        if (county.getChildren() == null) {
                            continue;
                        }
                        if (info.getCounty() != null && !info.getCounty().equals(county.getName())) {
                            continue;
                        }
                        for (AddressDataLoader.Address street : county.getChildren()) { // 街道
                            if (street.getName().contains(matchAddress)) {
                                matchStreet.add(new MatchData(province, city, county, street, matchAddress));
                            }
                        }
                    }
                }
            }
        }
        if (!matchStreet.isEmpty()) {
            MatchData matchData = getTheOptimalMatch(matchStreet);
            setMatchInfo(info, matchData);
            text = text.replaceFirst(matchData.getMatchValue(), "");
            text = ReUtil.replaceFirst(pattern, text, "");
        }
        setAddress(matchStreet, address, text, info);
        return info;
    }


    private void setAddress(List<MatchData> matchList, String address, String text, AddressInfo info) {
        if (matchList.isEmpty() || !address.equals(text)) {
            info.setAddress(text);
        }
    }

    /**
     * 获取最优匹配
     */
    private MatchData getTheOptimalMatch(List<MatchData> matchDataList) {
        return Collections.max(matchDataList, Comparator.comparingInt(o -> o.getMatchValue().length()));
    }

    protected void setMatchInfo(AddressInfo info, MatchData matchData) {
        info.setProvince(matchData.getProvince());
        info.setProvinceCode(matchData.getProvinceCode());
        info.setCity(matchData.getCity());
        info.setCityCode(matchData.getCityCode());
        info.setCounty(matchData.getCounty());
        info.setCountyCode(matchData.getCountyCode());
        info.setStreet(matchData.getStreet());
        info.setStreetCode(matchData.getStreetCode());
        info.setAreaId(matchData.getAreaId());
    }


    public String filterStr(String text) {
        text = ReUtil.replaceAll(text, "[`~!@#$^&*=|{}':;',.<>/?~！@#￥……&*——|‘；：”“’。，、？-]", " ");
        return text.replace("\r", "").replace("\n", "");
    }


    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}


/**
 * 匹配上的数据
 */
@Data
class MatchData {

    public MatchData(AddressDataLoader.Address province,
                     AddressDataLoader.Address city,
                     AddressDataLoader.Address county,
                     AddressDataLoader.Address street,
                     String matchValue) {
        if (province != null) {
            this.province = province.getName();
            this.provinceCode = province.getCode();
            this.areaId = province.getId();
        }
        if (city != null) {
            this.city = city.getName();
            this.cityCode = city.getCode();
            this.areaId = city.getId();
        }
        if (county != null) {
            this.county = county.getName();
            this.countyCode = county.getCode();
            this.areaId = county.getId();
        }
        if (street != null) {
            this.street = street.getName();
            this.streetCode = street.getCode();
            this.areaId = street.getId();
        }
        this.matchValue = matchValue;
    }

    private String province;

    private String provinceCode;

    /**
     * 市
     */
    private String city;

    private String cityCode;

    /**
     * 区
     */
    private String county;

    private String countyCode;

    /**
     * 街道
     */
    private String street;

    private String streetCode;

    private String matchValue;

    private String areaId;

}
