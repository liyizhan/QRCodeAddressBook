package com.example.cyq.qrcodecard.util;

/**
 * Created by as on 2018/5/17.
 */

public class StringUtils {
    public static boolean isEmpty(String str){
        if(str==null){
            return true;
        }
        str=str.trim();
        if(str.equals("")){
            return true;
        }
        return false;
    }
}
