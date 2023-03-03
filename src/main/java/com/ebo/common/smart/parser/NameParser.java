package com.ebo.common.smart.parser;

import cn.hutool.core.util.NumberUtil;
import com.ebo.common.smart.TextHolder;
import com.ebo.common.smart.domain.UserInfo;

import java.util.List;

/**
 * 姓名解析器
 */
public class NameParser {


    public void parse(UserInfo userInfo, List<String> textList, TextHolder textHolder) {

        for (int i = 0; i < textList.size(); i++) {
            String text = textList.get(i);
            if (NumberUtil.isNumber(text)) {
                continue;
            }
            if (text.length() <= 6) {
                userInfo.setName(text);
                textHolder.removeText(text);
                textList.remove(i);
                return;
            }
        }
    }
}
