package com.example.cyq.qrcodecard.util;

import java.util.UUID;

/**
 * Created by as on 2018/5/22.
 */

public class UUIDUtils {
    public static String shortUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
