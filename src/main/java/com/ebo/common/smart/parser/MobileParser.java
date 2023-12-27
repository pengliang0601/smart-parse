package com.ebo.common.smart.parser;

import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.ebo.common.smart.TextHolder;
import com.ebo.common.smart.domain.UserInfo;

import java.util.List;

/**
 * 电话解析器
 */
public class MobileParser {

    /**
     * 匹配手机号正则、包含虚拟虚拟号
     */
    String MOBILE = "(?:0|86|\\+86)?1[3-9]\\d{9}([-,]+\\d{0,5}#?){0,1}";

    public void parse(UserInfo userInfo, List<String> textList, TextHolder textHolder) {
        for (int i = 0; i < textList.size(); i++) {
            String text = textList.get(i);
            String mobile = match(text);
            if (StrUtil.isEmpty(mobile)) {
                continue;
            }
            textList.set(i, textList.get(i).replace(mobile, ""));
            userInfo.setMobile(mobile);
            textHolder.removeText(mobile);
            return;
        }
    }

    /**
     * 匹配手机号码
     */
    public String match(String text) {
        String mobile = ReUtil.getGroup0(MOBILE, text);
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

}

