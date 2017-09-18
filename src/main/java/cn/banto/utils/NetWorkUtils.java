package cn.banto.utils;

import cn.banto.model.NetWorkInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by banto on 2017/5/17.
 */
public class NetWorkUtils {

    /**
     * 获取所有有效网卡信息
     * @return
     */
    public static ArrayList<NetWorkInfo> getNetworkInterfaces(){
        ArrayList<NetWorkInfo> result = new ArrayList<NetWorkInfo>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()){
                NetworkInterface networkInterface = interfaces.nextElement();
                //获取IPv4
                String ip = null;
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()){
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if(isIpv4(inetAddress.getHostAddress())){
                        ip = inetAddress.getHostAddress();
                        break;
                    }
                }
                //获取mac
                String mac = null;
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if(hardwareAddress != null && hardwareAddress.length == 6){
                    StringBuffer sb = new StringBuffer();
                    for(byte b : hardwareAddress){
                        //与11110000作按位与运算以便读取当前字节高4位
                        sb.append(Integer.toHexString((b & 240) >> 4));
                        //与00001111作按位与运算以便读取当前字节低4位
                        sb.append(Integer.toHexString(b & 15));
                        sb.append(":");
                    }
                    sb.deleteCharAt(sb.length()-1);
                    mac = sb.toString().toUpperCase();
                }
                //检查是否合格
                if(ip != null && mac != null){
                    NetWorkInfo info = new NetWorkInfo();
                    info.setName(networkInterface.getDisplayName());
                    info.setIp(ip);
                    info.setMac(mac);
                    info.setUp(networkInterface.isUp());
                    result.add(info);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取处理活跃状态的第一张网卡信息
     * @return
     */
    public static NetWorkInfo getUpNetWorkInfo(){
        ArrayList<NetWorkInfo> list = NetWorkUtils.getNetworkInterfaces();
        for (NetWorkInfo info : list) {
            if(info.isUp()){
                return info;
            }
        }

        return null;
    }

    /**
     * 获取指定网卡的信息
     * @param name
     * @return
     */
    public static NetWorkInfo getNetWorkInfo(String name){
        List<NetWorkInfo> list = NetWorkUtils.getNetworkInterfaces();
        for (NetWorkInfo info : list){
            if(info.getName().equals(name)){
                return info;
            }
        }

        return null;
    }

    /**
     * 判断是否为合法P
     * @return true or false
     */
    public static boolean isIpv4(String ipAddress) {
        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(ip);
        return pattern.matcher(ipAddress).find();
    }

    /**
     * 判断是否是合法mac
     * @param mac
     * @return
     */
    public static boolean isMAC(String mac){
        Pattern pattern = Pattern.compile("([a-fA-F0-9]{2}:){5}[a-fA-F0-9]{2}");
        return pattern.matcher(mac).find();
    }
}
