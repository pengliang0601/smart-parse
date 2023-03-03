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


    public void parse(UserInfo userInfo, List<String> textList, TextHolder textHolder) {
        for (int i = 0; i < textList.size(); i++) {
            String text = textList.get(i);
            String mobile = match(text);
            if (StrUtil.isEmpty(mobile)) {
                continue;
            }
            userInfo.setMobile(mobile);
            textHolder.removeText(text);
            textList.remove(i);
            return;
        }
    }

    /**
     * 匹配手机号码
     *
     * @param text
     * @return
     */
    public String match(String text) {
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


}

