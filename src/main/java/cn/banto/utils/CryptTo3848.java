package cn.banto.utils;

/**
 * Created by banto on 2017/5/17.
 */
public class CryptTo3848 {

    /**
     * 加密
     * @param buffer
     * @return
     */
    public static byte[] encode(byte[] buffer){
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte)((buffer[i] & 0x80) >> 6
                    | (buffer[i] & 0x40) >> 4
                    | (buffer[i] & 0x20) >> 2
                    | (buffer[i] & 0x10) << 2
                    | (buffer[i] & 0x08) << 2
                    | (buffer[i] & 0x04) << 2
                    | (buffer[i] & 0x02) >> 1
                    | (buffer[i] & 0x01) << 7);
        }
        return buffer;
    }

    /**
     * 解密
     * @param buffer
     * @return
     */
    public static byte[] decode(byte[] buffer){
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte)((buffer[i] & 0x80) >> 7
                    | (buffer[i] & 0x40) >> 2
                    | (buffer[i] & 0x20) >> 2
                    | (buffer[i] & 0x10) >> 2
                    | (buffer[i] & 0x08) << 2
                    | (buffer[i] & 0x04) << 4
                    | (buffer[i] & 0x02) << 6
                    | (buffer[i] & 0x01) << 1);
        }
        return buffer;
    }
}
