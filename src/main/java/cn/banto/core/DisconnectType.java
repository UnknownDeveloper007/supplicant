package cn.banto.core;

/**
 * Created by banto on 2017/5/18.
 */
public enum DisconnectType {
    UNKNOWN(-2, "未知原因"),
    NORMAL(-1, "正常下线"),
    DROPPED(0, "心跳掉线"),
    FORCE_DISCONNECT(1, "强制下线"),
    //TIMEOUT(2, "时长已用完")
    NO_TIME_AVAILABLE(2, "时长已用完");

    private int code;
    private String remark;

    DisconnectType(int code, String remark){
        this.code = code;
        this.remark = remark;
    }

    public int getCode() {
        return code;
    }

    public String getRemark() {
        return remark;
    }

    public static DisconnectType findByCode(int code){
        for (DisconnectType type : values()) {
            if(code == type.getCode()){
                return type;
            }
        }

        return UNKNOWN;
    }
}
