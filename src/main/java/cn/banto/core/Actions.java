package cn.banto.core;

/**
 * Created by banto on 2017/5/18.
 */
public class Actions {
    // login
    public final static int LOGIN = 0x01;

    // login result
    public final static int LOGIN_RET = 0x02;

    // breathe
    public final static int BREATHE = 0x03;

    // breathe result
    public final static int BREATHE_RET = 0x04;

    // logout
    public final static int LOGOUT = 0x05;

    // logout result
    public final static int LOGOUT_RET = 0x06;

    // get access point
    public final static int ENTRIES = 0x07;

    // return access point
    public final static int ENTRIES_RET = 0x08;

    // disconnect
    public final static int DISCONNECT = 0x09;

    // confirm login
    public final static int CONFIRM = 0x0A;

    // confirm login result
    public final static int CONFIRM_RET = 0x0B;

    // get server
    public final static int SERVER = 0X0C;

    // return server
    public final static int SERVER_RET = 0x0D;
}
