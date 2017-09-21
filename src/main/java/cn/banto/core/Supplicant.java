package cn.banto.core;

import cn.banto.event.SupplicantListener;
import cn.banto.exception.SupplicantException;
import cn.banto.model.NetWorkInfo;
import cn.banto.utils.ByteConvert;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by banto on 2017/5/17.
 */
public class Supplicant implements DisconnectListener.OnDisconnect {

    private final static int[] DEFAULT_BLOCK = new int[]{
            0x00, 0x00, 0x00, 0x00
    };

    private final static int[] DEFAULT_SESSION = new int[]{
            0x7b, 0x7b, 0x81, 0x6b, 0x6e, 0x64,
            0x85, 0x71, 0x78, 0x7b, 0x6a, 0x6c,
            0x66, 0x75, 0x87, 0x76, 0x82, 0x6a,
            0x77, 0x87, 0x65, 0x65, 0x81, 0x87,
            0x64, 0x78, 0x72, 0x6d, 0x89, 0x6e,
            0x6e, 0x65, 0x85, 0x72, 0x67
    };

    /**
     * 会话标识
     */
    private byte[] session;

    /**
     * 心跳计数器
     */
    private int index = 0x01000000;

    /**
     * 启用DHCP
     */
    private boolean dhcp = false;

    /**
     * 蝴蝶版本号
     */
    private String version = "3.8.2";

    /**
     * 心跳定时器
     */
    private Timer breatheTimer;

    /**
     * 消息使者
     */
    private Messenger messenger;

    /**
     * 离线消息监听器
     */
    private DisconnectListener disconnectListener;

    /**
     * 事件回调
     */
    private SupplicantListener listener;

    /**
     * 通讯网卡信息
     */
    private NetWorkInfo netWorkInfo;

    /**
     * 构造方法
     */
    public Supplicant(NetWorkInfo netWorkInfo) throws SupplicantException {
        if(netWorkInfo == null){
            throw new SupplicantException("请设置正确的网卡信息");
        }
        this.netWorkInfo = netWorkInfo;
        //初始化信使
        messenger = new Messenger();
        //初始化
        init();
        //自动搜搜认证服务器
        try {
            InetAddress server = InetAddress.getByName(search());
            messenger.setServer(server);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置监听回调
     * @param listener
     */
    public void setListener(SupplicantListener listener) {
        this.listener = listener;
    }

    /**
     * 设置通讯服务器
     * @param server
     */
    public void setServer(InetAddress server) {
        messenger.setServer(server);
    }

    /**
     * 设置超时时间
     * @param timeout
     */
    public void setTimeout(int timeout) {
        messenger.setReceiveTimeout(timeout);
    }

    /**
     * 设置蝴蝶版本号
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 设置是否启用dhcp
     * @param dhcp
     */
    public void setDhcp(boolean dhcp) {
        this.dhcp = dhcp;
    }

    /**
     * 初始化
     */
    public void init() throws SupplicantException {
        try {
            InetAddress address = InetAddress.getByName("1.1.1.8");
            messenger.send("info sock ini".getBytes(), address, 3850);
        } catch (UnknownHostException e) {
            throw new SupplicantException("无法找到1.1.1.8主机", e);
        } catch (IOException e) {
            throw new SupplicantException("与主机[1.1.1.8]通讯出错", e);
        }
    }

    /**
     * 搜索服务器
     * @return
     */
    public String search() throws SupplicantException {
        Message message = new Message();
        message.setAction(Actions.SERVER);
        message.putData(Keys.SESSION, ByteConvert.intArrayToBytes(DEFAULT_SESSION));
        message.putData(Keys.IP, netWorkInfo.getIp());
        message.putData(Keys.MAC, getMAC());
        try {
            InetAddress address = InetAddress.getByName("1.1.1.8");
            messenger.send(message, address, 3850);
            Message response = messenger.read(Actions.SERVER_RET);
            //组装服务器IP
            byte[] ip = response.getData(Keys.SERVER);
            StringBuffer server = new StringBuffer();
            for (int i = 0; i < ip.length; i++) {
                server.append(ip[i] & 0xFF).append(".");
            }
            server.deleteCharAt(server.lastIndexOf("."));

            return server.toString();
        } catch (UnknownHostException e) {
            throw new SupplicantException("无法找到1.1.1.8主机", e);
        } catch (IOException e) {
            throw new SupplicantException("与主机[1.1.1.8]通讯出错", e);
        }
    }

    /**
     * 获取可用认证方式
     * @return
     */
    public List<String> getEntries() throws SupplicantException {
        Message message = new Message();
        message.setAction(Actions.ENTRIES);
        message.putData(Keys.SESSION, ByteConvert.intArrayToBytes(DEFAULT_SESSION));
        message.putData(Keys.MAC, getMAC());
        try {
            messenger.send(message);
            Message response  = messenger.read(Actions.ENTRIES_RET);
            //组装可用认证方式
            List<String> entries = new ArrayList<String>();
            for (byte[] buffer : response.getDatas(Keys.ENTRY)) {
                entries.add(new String(buffer));
            }

            return entries;
        } catch (IOException e) {
            throw new SupplicantException("与认证服务器通讯出错", e);
        }
    }


    /**
     * 上线
     * @param username 用户名
     * @param password 密码
     * @param entry    认证方式
     */
    public void connect(String username, String password, String entry) throws SupplicantException{
        Message message = new Message();
        message.setAction(Actions.LOGIN);
        message.putData(Keys.MAC, getMAC());
        message.putData(Keys.USERNAME, username);
        message.putData(Keys.PASSWORD, password);
        message.putData(Keys.IP, netWorkInfo.getIp());
        message.putData(Keys.ENTRY, entry);
        message.putData(Keys.DHCP, (dhcp ? new byte[]{0x01} : new byte[]{0x00}));
        message.putData(Keys.VERSION, version);
        try {
            messenger.send(message);
            Message response = messenger.read(Actions.LOGIN_RET);
            //认证状态码
            boolean isSuccess = response.getData(Keys.SUCCESS)[0] == 1;
            //认证消息
            String msg = new String(response.getData(Keys.MESSAGE), "gb2312");

            if (isSuccess) {
                //初始化session和计数器
                session = response.getData(Keys.SESSION);
                index = 0x01000000;
                //启用心跳
                breatheTimer = new Timer(true);
                breatheTimer.schedule(new BreatheTask(), 0, 30000);
                //设置断开监听器
                disconnectListener = new DisconnectListener();
                disconnectListener.setServer(messenger.getServer());
                disconnectListener.setOnDisconnect(this);
            }
            //登录回调
            listener.onConnect(isSuccess, msg);
        } catch (IOException e) {
            throw new SupplicantException("与认证服务器通讯出错", e);
        }

    }

    /**
     * 心跳
     */
    private void breathe() throws SupplicantException {
        Message message = new Message();
        message.setAction(Actions.BREATHE);
        message.putData(Keys.SESSION, session);
        message.putData(Keys.IP, netWorkInfo.getIp());
        message.putData(Keys.MAC, getMAC());
        message.putData(Keys.INDEX, ByteConvert.intToBytes(index));
        message.putData(Keys.BLOCK2A, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        message.putData(Keys.BLOCK2B, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        message.putData(Keys.BLOCK2C, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        message.putData(Keys.BLOCK2D, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        message.putData(Keys.BLOCK2E, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        message.putData(Keys.BLOCK2F, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        try {
            messenger.send(message);
            Message response= messenger.read(Actions.BREATHE_RET);
            boolean isSuccess = response.getData(Keys.SUCCESS)[0] == 1;

            if (isSuccess) {
                //更新session和计数器
                session = response.getData(Keys.SESSION);
                index += 3;
            }
            //心跳回调
            listener.onBreathe(isSuccess);
        } catch (IOException e) {
            throw new SupplicantException("与认证服务器通讯出错", e);
        }
    }

    /**
     * 离线
     */
    public void disconnect() throws SupplicantException {
        Message message = new Message();
        message.setAction(Actions.LOGOUT);
        message.putData(Keys.SESSION, session);
        message.putData(Keys.IP, netWorkInfo.getIp());
        message.putData(Keys.MAC, getMAC());
        message.putData(Keys.INDEX, ByteConvert.intToBytes(index));
        message.putData(Keys.BLOCK2A, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        message.putData(Keys.BLOCK2B, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        message.putData(Keys.BLOCK2C, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        message.putData(Keys.BLOCK2D, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        message.putData(Keys.BLOCK2E, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        message.putData(Keys.BLOCK2F, ByteConvert.intArrayToBytes(DEFAULT_BLOCK));
        try {
            messenger.send(message);
            Message response = messenger.read(Actions.LOGOUT_RET);
            boolean isSuccess = response.getData(Keys.SUCCESS)[0] == 1;

            if (isSuccess) {
                disconnectListener.stop();
            }
        } catch (IOException e) {
            throw new SupplicantException("与认证服务器通讯出错", e);
        }
    }

    /**
     * 释放资源
     */
    public void destroy(){
        messenger.stop();
    }

    @Override
    public void onDisconnect(DisconnectType type) {
        //取消心跳任务
        breatheTimer.cancel();
        //触发回调
        listener.onDisconnect(type);
    }

    /**
     * 获取处理后的MAC地址
     * @return
     */
    private byte[] getMAC(){
        String[] mac = netWorkInfo.getMac().split(":");
        byte[] tmp = new byte[6];
        for (int i = 0; i < mac.length; i++){
            tmp[i] = (byte) Integer.parseInt(mac[i], 16);
        }

        return tmp;
    }

    /**
     * 心跳任务
     */
    private class BreatheTask extends TimerTask {
        @Override
        public void run() {
            try {
                breathe();
            } catch (SupplicantException e) {
                e.printStackTrace();
            }
        }
    }
}
