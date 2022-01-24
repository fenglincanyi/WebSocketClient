package com.yxc.websocketclientdemo.im;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.yxc.websocketclientdemo.App;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class JWebSocketClient extends WebSocketClient {
    public JWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("JWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        Log.e("JWebSocketClient", "onMessage()");
    }

    @Override
    public void onClose(int code, final String reason, boolean remote) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getInstance(), "连接关闭： "+reason, Toast.LENGTH_LONG).show();
            }
        });
        Log.e("JWebSocketClient", "onClose()");
    }

    @Override
    public void onError(final Exception ex) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getInstance(), "连接错误："+ex.toString(), Toast.LENGTH_LONG).show();
            }
        });
        Log.e("JWebSocketClient", "onError()");
    }
}
