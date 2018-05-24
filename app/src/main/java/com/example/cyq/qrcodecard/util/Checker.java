package com.example.cyq.qrcodecard.util;

import java.util.regex.Pattern;

/**
 * Created by as on 2018/5/22.
 */

public class Checker {
    public static final Pattern NAME=Pattern.compile("^([\\u4e00-\\u9fa5A-Za-z]|[ ]+){2,20}$");
    public static final Pattern PHONE=Pattern.compile("^(((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7]))\\d{8}|0\\d{2,3}-\\d{7,8}$)$");
    public static final Pattern EMAIL=Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    public static final Pattern QQ=Pattern.compile("^[0-9]{5,12}$");
    public static final Pattern WE_CHAT=Pattern.compile("^([A-Za-z0-9]|-|_){1,40}$");
    public static final Pattern ADDRESS=Pattern.compile("^([A-Za-z0-9\\u4e00-\\u9fa5]|-|_){1,50}$");
}
