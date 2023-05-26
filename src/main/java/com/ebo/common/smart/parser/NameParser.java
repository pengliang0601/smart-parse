package com.ebo.common.smart.parser;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.ReUtil;
import com.ebo.common.smart.RegexConstant;
import com.ebo.common.smart.TextHolder;
import com.ebo.common.smart.domain.UserInfo;

import java.util.List;

/**
 * 姓名解析器
 */
public class NameParser {


    public void parse(UserInfo userInfo, List<String> textList, TextHolder textHolder) {
        String name = parseName(textList);
        if (name == null) {
            return;
        }
        userInfo.setName(name);
        textHolder.removeText(name);
    }

    private String parseName(List<String> textList) {
        for (String text : textList) {
            for (String name : StrSplitter.splitByRegex(text, RegexConstant.SPECIAL, 0, true, true)) {
                if (ReUtil.isMatch(RegexConstant.SURNAME_LIST, name)) {
                    return name;
                }
            }
        }
        return null;
    }


}
