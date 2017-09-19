package cn.banto.core;

import cn.banto.exception.SocketClosedException;
import cn.banto.utils.CryptTo3848;
import org.apache.log4j.Logger;

import java.io.IOException;
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
     * 关闭socket
     */
    public void stop(){
        try {
            DatagramPacket packet = new DatagramPacket(new byte[1], 1, InetAddress.getByName("127.0.0.1"), socket.getPort());
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化socket
     * @throws SocketException
     */
    protected void initSocket(int port) throws SocketException{
        socket = new DatagramSocket(port);
    }

    /**
     * 发送消息对象
     * @param message
     * @param address
     * @param port
     * @throws IOException
     */
    protected void send(Message message, InetAddress address, int port) throws IOException {
        //编码消息
        byte[] buffer = MessageParser.toByte(message);
        byte[] data   = CryptTo3848.encode(buffer);
        //发送消息
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }

    /**
     * 接收消息
     * @return
     * @throws IOException
     */
    protected Message read() throws IOException {
        return read(new MessageFilter());
    }

    /**
     * 接收消息
     * @param filter
     * @return
     * @throws IOException
     */
    protected Message read(MessageFilter filter) throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        //关闭socket
        if(isStopSocketPacket(packet) && ! socket.isClosed()){
            socket.close();
            throw new SocketClosedException();
        }
        //如果不是来自服务器的消息,则丢弃重新接收，直到接收到或超时为止
        if(! isFromServer(packet)){
            return read(filter);
        }
        //运行前置过滤器
        if(! filter.befor(packet)){
            return read(filter);
        }
        //解析数据
        byte[] response = new byte[packet.getLength()];
        System.arraycopy(buffer, 0, response, 0, response.length);
        //解码
        byte[] decode = CryptTo3848.decode(response);
        Message message = MessageParser.fromByte(decode);
        //运行后置过滤器
        if(! filter.after(message)){
            return read(filter);
        }

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
     * 判断是否为停止socket的包
     * @param packet
     * @return
     */
    protected boolean isStopSocketPacket(DatagramPacket packet){
        return "127.0.0.1".equals(packet.getAddress().getHostAddress()) && packet.getLength() == 1;
    }
}
