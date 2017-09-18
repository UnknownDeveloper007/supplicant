package cn.banto.core;

/**
 * Created by banto on 2017/5/18.
 */
public class Keys {

    // username
    public final static int USERNAME = 0x01;

    // password
    public final static int PASSWORD = 0x02;

    // whether it is success
    public final static int SUCCESS = 0x03;

    // unknown, appears while login successfully
    public final static int UNKNOWN05 = 0x05;
    public final static int UNKNOWN06 = 0x06;

    // mac address
    public final static int MAC = 0x07;

    // session (NOTE: wrong in return packet)
    public final static int SESSION = 0x08;

    // ip address
    public final static int IP = 0x09;

    // access point
    public final static int ENTRY = 0x0A;

    // message (NOTE: wrong in return packet)
    public final static int MESSAGE = 0x0B;

    // server ip address
    public final static int SERVER = 0x0C;

    // unknown, appears while received server ip address
    public final static int UNKNOWN0D = 0x0D;

    // is dhcp enabled
    public final static int DHCP = 0x0E;

    // self-services website link
    public final static int WEBSITE = 0x13;

    // serial no
    public final static int INDEX = 0x14;

    // version
    public final static int VERSION = 0x1F;

    // unknown, appears while login successfully
    public final static int UNKNOWN20 = 0x20;
    public final static int UNKNOWN23 = 0x23;

    // disconnect reason
    public final static int REASON = 0x24;

    // 4 bytes blocks, send in breathe and logout
    public final static int BLOCK2A = 0x2A;
    public final static int BLOCK2B = 0x2B;
    public final static int BLOCK2C = 0x2C;
    public final static int BLOCK2D = 0x2D;
    public final static int BLOCK2E = 0x2E;
    public final static int BLOCK2F = 0x2F;

    // unknown 4 bytes blocks, appears while confirmed
    public final static int BLOCK30 = 0x30;
    public final static int BLOCK31 = 0x31;

    // unknown
    public final static int UNKOWN32 = 0x32;

    // 4 bytes blocks, appears while login successfully
    public final static int BLOCK34 = 0x34;
    public final static int BLOCK35 = 0x35;
    public final static int BLOCK36 = 0x36;
    public final static int BLOCK37 = 0x37;
    public final static int BLOCK38 = 0x38;
}
