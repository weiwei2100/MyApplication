package com.jason.myapp.utils;

/**
 * Created by mrh on 2016/8/11.
 */
public class Textutil {
    private static Textutil textutil = null;
    public boolean issync = false;

    public static Textutil getTextutil() {
        if (textutil == null) {
            textutil = new Textutil();
        }
        return textutil;
    }

    public boolean issync() {
        return issync;
    }

    public void setIssync(boolean issync) {
        this.issync = issync;
    }
}
