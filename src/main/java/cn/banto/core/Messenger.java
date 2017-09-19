package cn.banto.core;

import cn.banto.exception.SupplicantException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

public class Messenger extends  BaseSocket{

    private final Logger logger = Logger.getLogger(Messenger.class);

    /**
     * 构造方法
     * @throws SupplicantException
     */
    public Messenger() throws SupplicantException {
        try {
            initSocket(3848);
        } catch (SocketException e) {
            throw new SupplicantException("监听3848端口失败,请检查端口是否被占用", e);
        }
        logger.debug("消息信使已初始化完成");
    }

    /**
     * 设置超时时间
     * @param receiveTimeout
     */
    public void setReceiveTimeout(int receiveTimeout) {
        try {
            socket.setSoTimeout(receiveTimeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        logger.debug("接收消息超时 = "+ receiveTimeout);
    }

    /**
     * 发送消息
     * @param message 消息
     * @throws IOException
     */
    public void send(Message message) throws IOException {
        send(message, server, 3848);
    }

    /**
     * 接收消息
     * @param appointAction 指定消息动作
     * @return
     * @throws IOException
     */
    public Message receive(final int appointAction) throws IOException {
        return read(new MessageFilter() {
            @Override
            public boolean after(Message message) {
                return message.getAction() == appointAction;
            }
        });
    }

    /**
     * 发送并接受消息
     * @param message 发送的消息
     * @param appointAction 指定消息动作
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
}
