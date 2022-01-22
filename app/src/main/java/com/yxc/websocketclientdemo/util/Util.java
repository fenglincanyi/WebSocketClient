package com.yxc.websocketclientdemo.util;

import android.content.Context;
import android.widget.Toast;

public class Util {
//    wss连接测试地址: wss://socket.idcd.com:1443
//    ws连接测试地址: ws://49.234.18.41:8866

//    public static final String ws = "ws://echo.websocket.org";//websocket测试地址
    public static final String ws = "ws://49.234.18.41:8866";//websocket测试地址

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}
