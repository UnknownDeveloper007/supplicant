package cn.banto.core;

import cn.banto.exception.SupplicantException;

import java.io.IOException;
import java.net.SocketException;

public class Messenger extends  BaseSocket{

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
    public Message read(final int appointAction) throws IOException {
        return read(new MessageFilter() {
            @Override
            public boolean after(Message message) {
                return message.getAction() == appointAction;
            }
        });
    }


    /**
     * 关闭socket
     */
    @Override
    public void stop() {
        socket.close();
        socket = null;
    }
}
