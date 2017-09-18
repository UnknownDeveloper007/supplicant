package cn.banto.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by banto on 2017/5/18.
 */
public class MessageParser {

    /**
     * 将通讯包转为字节数组
     * @param message
     * @return
     */
    public static byte[] toByte(Message message) {
        //将数据转为字节数组
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (Map.Entry<Integer, byte[]> entry : message.getData().entrySet()){
            try {
                out.write(entry.getKey());
                out.write(entry.getValue().length + 2);
                out.write(entry.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] data = out.toByteArray();
        //封包
        byte[] buffer = new byte[data.length + 18];
        //设置包动作
        buffer[0] = (byte) message.getAction();
        //设置包长
        buffer[1] = (byte) buffer.length;
        //预填充签名数据
        System.arraycopy(new byte[16], 0, buffer, 2, 16);
        //复制数据
        System.arraycopy(data, 0, buffer, 18, data.length);
        //签名
        byte[] sign = sign(buffer);
        System.arraycopy(sign, 0, buffer, 2, 16);

        return buffer;
    }


    public static Message fromByte(byte[] data){
//        for (int i = 0; i < data.length; i++) {
//            System.out.print(data[i] +",");
//        }
        //检查数据完好性
//        System.out.println("包长："+ data[1] +"," +(data[1] & 0xff));
//        System.out.println("实际长度："+ data.length);
        //未知bug, 登录数据包中返回的包长远小于实际长度
        //|| (data[1] & 0xff)  != data.length
        if(data == null || data.length < 18  || ! checkSign(data)) return null;
        //解析数据
        Message packet = new Message();
        //设置动作
        packet.setAction(data[0]);

        int index = 18;
        boolean flag = false;
        while (true){
            int key = data[index];
            //修正长度无法正确获取的问题
            int dataLen = (data[++index] & 0xff) - 2;
            //修正数据错误
            if(key == Keys.SESSION || key == Keys.MESSAGE) {
                dataLen += 2;
            }
//            System.out.println("key下标： "+ index +"，key="+ key);
//            System.out.println("数据长度："+ dataLen);
            byte[] value  = new byte[dataLen];
            System.arraycopy(data, ++index, value, 0, value.length);
            index += dataLen;
            //写入数据
            if(packet.getData(key) == null && flag == false){
                packet.putData(key, value);
            } else {
                ArrayList<byte[]> list = new ArrayList<byte[]>();
                if(packet.getData(key) != null && flag == false) {
                    list.add(packet.getData(key));
                    packet.removeData(key);
                }
                list.add(value);
                packet.putDatas(key, list);
            }
            //检查数据是否已经解析完成
            if(index == data.length){
                break;
            }
        }
        return packet;
    }

    /**
     * 签名
     * @param buffer
     * @return
     */
    private static byte[] sign(byte[] buffer){
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(buffer);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return new byte[16];
    }

    /**
     * 检查签名
     * @param buffer
     * @return
     */
    private static boolean checkSign(byte[] buffer){
        //获取原签名
        byte[] md5 = new byte[16];
        System.arraycopy(buffer, 2, md5, 0, 16);
        //重新签名
        System.arraycopy(new byte[16], 0, buffer, 2, 16);
        byte[] newMd5 = sign(buffer);

        return new String(md5).equals(new String(newMd5));
    }
}
