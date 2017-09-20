package cn.banto.event;

import cn.banto.core.DisconnectType;

public class SupplicantListener {

    /**
     * 认证回调
     * @param isSuccess
     * @param msg
     */
    public void onConnect(boolean isSuccess, String msg){}

    /**
     * 心跳回调
     * @param isSuccess
     */
    public void onBreathe(boolean isSuccess){}

    /**
     * 离线回调
     * @param type
     */
    public void onDisconnect(DisconnectType type){}
}
