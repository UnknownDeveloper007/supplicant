package cn.banto.core;

import cn.banto.utils.CryptTo3848;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class BaseSocket {

    private final Logger logger = Logger.getLogger(BaseSocket.class);

    protected InetAddress server;
    protected DatagramSocket socket;

    /**
     * 设置通讯服务器地址
     * @param server
     */
    public void setServer(InetAddress server) {
        this.server = server;
    }

    /**
     * 获取通讯服务器地址
     * @return
     */
    public InetAddress getServer() {
        return server;
    }

    /**
     * 初始化socket
     * @throws SocketException
     */
    protected void initSocket() throws SocketException{}

    /**
     * 将消息对象转为字节数据
     * @param message
     * @return
     */
    protected byte[] messageToByte(Message message){
        byte[] buffer = MessageParser.toByte(message);
        byte[] data   = CryptTo3848.encode(buffer);
        logger.debug("已成功将消息对象["+ message.hashCode() +"]转为字节数组: "+ StringUtils.join(data, ','));

        return data;
    }

    /**
     * 将字节数组转为消息对象
     * @param data
     * @return
     */
    protected Message byteToMessage(byte[] data){
        byte[] buffer = CryptTo3848.decode(data);
        Message message = MessageParser.fromByte(buffer);
        logger.debug("已成功将字节数组转为消息对象["+ message.hashCode() +"]: "+ StringUtils.join(data, ','));

        return message;
    }

    /**
     * 判断是否是来自认证服务器的消息
     * @param packet
     * @return
     */
    protected boolean isFromServer(DatagramPacket packet){
        if(server != null) {
            String packetAddress = packet.getAddress().getHostAddress();

            return server.getHostAddress().equals(packetAddress) || "1.1.1.8".equals(packetAddress);
        }

        return true;
    }

    /**
     * 判断是否来自本地的消息
     * @param packet
     * @return
     */
    protected boolean isFromLocal(DatagramPacket packet){
        return "127.0.0.1".equals(packet.getAddress().getHostAddress());
    }
}
