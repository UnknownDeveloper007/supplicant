package cn.banto.utils;

public class ByteConvert {

    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        
        return b;
    }

    /**
     * 将int数组转为byte数组
     * @param data
     * @return
     */
    public static byte[] intArrayToBytes(int[] data){
        byte[] buffer = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            buffer[i] = (byte) data[i];
        }

        return buffer;
    }

}