package cn.banto.event;

import cn.banto.core.DisconnectType;

public class SupplicantListener {
    /**
     * 登录回调
     * @param isSuccess
     * @param msg
     */
    public void onLogin(boolean isSuccess, String msg){}

    /**
     * 离线回调
     * @param type
     */
    public void onDisconnect(DisconnectType type){}
}
