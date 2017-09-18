package cn.banto.core;

import cn.banto.exception.SupplicantException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Messenger extends  BaseSocket{

    private final Logger logger = Logger.getLogger(Messenger.class);

    /**
     * 数据缓存
     */
    private byte[] buffer = new byte[1024];

    /**
     * 接收超时时间
     */
    private int receiveTimeout;


    public Messenger() throws SupplicantException {
        try {
            initSocket();
        } catch (SocketException e) {
            throw new SupplicantException("监听3848端口失败,请检查端口是否被占用", e);
        }
        logger.debug("消息信使已初始化完成");
    }


    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
        logger.debug("接收消息超时 = "+ receiveTimeout);
    }

    /**
     * 发送消息
     * @param message
     * @throws IOException
     */
    public void send(Message message, InetAddress address, int port) throws IOException {
        byte[] data = messageToByte(message);
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);

        socket.send(packet);
        logger.debug("已发出消息:"+ message.hashCode());
    }

    /**
     * 发送消息
     * @param message
     * @throws IOException
     */
    public void send(Message message) throws IOException {
        send(message, server, 3848);
    }

    /**
     * 接收消息
     * @param appointAction
     * @return
     * @throws IOException
     */
    public Message receive(int appointAction) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(receiveTimeout);
        socket.receive(packet);
        int hashCode = packet.hashCode();
        logger.debug("接收到消息:"+ hashCode);
        //检查是否是来自服务器的消息,如果不是服务器的消息，则重新接收，直到接收到服务器消息为止
        if(! isFromServer(packet)){
            logger.debug(hashCode +"不属于来自服务器的消息,已被丢弃");
            return receive(appointAction);
        }
        //提取数据
        byte[] response = new byte[packet.getLength()];
        System.arraycopy(buffer, 0, response, 0, response.length);
        Message message = byteToMessage(response);
        //检查是否是指定的消息
        if(message.getAction() != appointAction){
            logger.debug(hashCode +"不属于指定消息,已被丢弃");
            return receive(appointAction);
        }

        return message;
    }

    /**
     * 发送并接受消息
     * @param message
     * @param appointAction
     * @return
     * @throws IOException
     */
    public Message sendAndReceive(Message message, int appointAction) throws IOException {
        send(message);
        return receive(appointAction);
    }

    /**
     * 发送并接受消息
     * @param message
     * @param address
     * @param port
     * @param appointAction
     * @return
     * @throws IOException
     */
    public Message sendAndReceive(Message message, InetAddress address, int port, int appointAction) throws IOException {
        send(message, address, port);
        return receive(appointAction);
    }

    /**
     * 释放资源
     */
    public void destroy(){
        socket.close();
        socket = null;
        logger.debug("已释放消息信使");
    }

    protected void initSocket() throws SocketException {
        socket = new DatagramSocket(3848);
        socket.setSoTimeout(receiveTimeout);
    }
}
