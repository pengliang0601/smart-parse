package com.ebo.common.smart;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.ebo.common.smart.domain.AddressInfo;
import com.ebo.common.smart.domain.UserInfo;
import com.ebo.common.smart.parser.AreaParse;
import com.ebo.common.smart.parser.MobileParser;
import com.ebo.common.smart.parser.NameParser;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SmartParse {

    /**
     * 地名标识常量
     */
    public static final String PLACE_NAME_CHAR = "省市区县州街道镇乡特别行政自治";

    private final MobileParser mobileParser = new MobileParser();
    private final AreaParse areaParse;
    private final NameParser nameParser = new NameParser();


    public SmartParse(AddressDataLoader addressDataLoader) {
        areaParse = new AreaParse(addressDataLoader);
    }

    /**
     * 分割文本：根据空格特殊符出电话、姓名、地址信息
     */
    public List<String> splitText(TextHolder textHolder) {

        String text = textHolder.getText();
        // 去除包含地名标识后面的空格，比如这总地址：广东省 东莞市 珊美社区车站路7号
        for (String str : ReUtil.findAllGroup0("[" + PLACE_NAME_CHAR + "] +", text)) {
            text = text.replace(str, StrUtil.trim(str));
        }

        List<String> textList = StrSplitter.splitByRegex(text, "[\\n\\r]", 0, true, true);

        // 按照一些特定的分隔符来分割，比如空格，逗号等
        List<String> list = textList.stream()
                .flatMap((Function<String, Stream<String>>) str -> {
                    if (ReUtil.contains(":[ ]?", str)) {
                        List<String> split = StrUtil.split(str, ':');
                        if (split.size() == 2 && ReUtil.contains("姓名|地址|收货|人|手机|号码|地区", split.get(0))) {
                            return Stream.of(split.get(1));
                        }
                        return Stream.of(str);
                    } else {
                        List<String> split2 = StrSplitter.splitByRegex(str, "[ ,，]", 0, true, true);
                        return split2.stream();
                    }
                })
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        textHolder.setText(StrUtil.join("", list));
        return list;
    }

    /**
     * 解析用户地址信息
     *
     * @param text 地址信息
     */
    public UserInfo parseUserInfo(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }

        UserInfo userInfo = new UserInfo();
        TextHolder textHolder = new TextHolder(text);
        List<String> textList = splitText(textHolder);
        // 匹配电话号码
        mobileParser.parse(userInfo, textList, textHolder);
        // 解析名字
        nameParser.parse(userInfo, textList, textHolder);
        // 清除特殊字符
        textList.replaceAll(content -> ReUtil.replaceAll(content, RegexConstant.SPECIAL, ""));
        // 解析地区址
        areaParse.parse(userInfo, textList, textHolder);
        return userInfo;
    }

    public AddressInfo parseAddressInfo(String text) {
        UserInfo userInfo = new UserInfo();
        TextHolder textHolder = new TextHolder(text);
        List<String> textList = splitText(textHolder);
        // 解析地区址
        areaParse.parse(userInfo, textList, textHolder);
        return userInfo;
    }
}
