package com.ebo.common.smart;

public class TextHolder {

    private String text;

    public TextHolder(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void removeText(String text) {
        if (text == null) {
            return;
        }
        this.text = this.text.replace(text, "");
    }
}
