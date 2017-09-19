package cn.banto.core;

import java.net.DatagramPacket;

public class MessageFilter {

    public boolean befor(DatagramPacket packet) {
        return true;
    }

    public boolean after(Message message) {
        return true;
    }
}
