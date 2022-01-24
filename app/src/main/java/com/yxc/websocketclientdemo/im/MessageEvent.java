package com.yxc.websocketclientdemo.im;

public class MessageEvent {

    public MessageEvent(boolean isClose) {
        this.isClose = isClose;
    }

    boolean isClose;
}
