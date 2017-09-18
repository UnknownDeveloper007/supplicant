package cn.banto.core;

import cn.banto.exception.SupplicantException;
import cn.banto.utils.CryptTo3848;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class DisconnectListener extends BaseSocket {

    private final Logger logger = Logger.getLogger(DisconnectListener.class);

    /**
     * 离线回调接口
     */
    public interface OnDisconnect {
        void onDisconnect(DisconnectType type);
    }

    /**
     * 离线回调
     */
    private OnDisconnect onDisconnect;

    public void setOnDisconnect(OnDisconnect onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

    /**
     * 开始运行
     */
    public void run() throws SupplicantException {
        try {
            initSocket();
            new Receiver().start();
        } catch (SocketException e) {
            throw new SupplicantException("监听4999端口失败,请检查端口是否被占用", e);
        }
        logger.debug("离线消息监听器已启动");
    }

    /**
     * 关闭离线消息监听器
     */
    public void stop(){
        try {
            DatagramPacket packet = new DatagramPacket(new byte[1], 1, InetAddress.getByName("127.0.0.1"), 4999);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("已发送停止离线消息监听请求");
    }

    /**
     * 初始化socket
     * @throws SupplicantException
     */
    protected void initSocket() throws SocketException {
        socket = new DatagramSocket(4999);
    }


    /**
     * 数据接收器
     */
    public class Receiver extends Thread {

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                while (true) {
                    //接收数据
                    socket.receive(packet);
                    int hashCode = packet.hashCode();
                    logger.debug("已接收到离线消息:"+ hashCode);
                    //来自本地的1字节的消息 表示  关闭监听
                    if(isFromLocal(packet) && packet.getLength() == 1){
                        logger.debug("来自本地关闭请求");
                        onDisconnect.onDisconnect(DisconnectType.NORMAL);
                        break;
                    }
                    //如果不是来自服务器的数据,则丢弃
                    if(! isFromServer(packet)){
                        logger.debug(hashCode +"不属于来自服务器的消息,已被丢弃");
                        continue;
                    }
                    byte[] response = new byte[packet.getLength()];
                    System.arraycopy(buffer, 0, response, 0, response.length);
                    //解析数据
                    byte[] data = CryptTo3848.decode(response);
                    Message message = MessageParser.fromByte(data);
                    //检查包是否合法
                    if(message.getAction() == Actions.DISCONNECT) {
                        int reason = message.getData(Keys.REASON)[0];
                        //触发事件
                        DisconnectType type = DisconnectType.findByCode(reason);
                        logger.debug("正在触发离线回调");
                        onDisconnect.onDisconnect(type);
                        break;
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            } finally {
                socket.close();
                socket = null;
                logger.debug("离线消息监听器已关闭");
            }
        }
    }
}
