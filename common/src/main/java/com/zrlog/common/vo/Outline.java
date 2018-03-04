package com.zrlog.common.vo;

public class Outline {

    private String text;
    private int level;

    private OutlineVO children = new OutlineVO();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OutlineVO getChildren() {
        return children;
    }

    public void setChildren(OutlineVO children) {
        this.children = children;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
