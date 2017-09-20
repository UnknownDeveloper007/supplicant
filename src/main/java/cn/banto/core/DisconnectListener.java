package cn.banto.core;

import cn.banto.exception.SocketClosedException;
import cn.banto.exception.SupplicantException;

import java.io.IOException;
import java.net.SocketException;

/**
 * 离线消息监听器
 */
public class DisconnectListener extends BaseSocket {

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

    /**
     * 设置离线回调
     * @param onDisconnect
     */
    public void setOnDisconnect(OnDisconnect onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

    /**
     * 构造方法
     * @throws SupplicantException
     */
    public DisconnectListener() throws SupplicantException {
        try {
            initSocket(4999);
            new Receiver().start();
        } catch (SocketException e) {
            throw new SupplicantException("监听4999端口失败,请检查端口是否被占用", e);
        }
    }

    /**
     * 数据接收器
     */
    public class Receiver extends Thread {

        @Override
        public void run() {
            DisconnectType type = DisconnectType.UNKNOWN;
            try {
                Message message = read(new MessageFilter() {
                    @Override
                    public boolean after(Message message) {
                        return message.getAction() == Actions.DISCONNECT;
                    }
                });

                int reason = message.getData(Keys.REASON)[0];
                type = DisconnectType.findByCode(reason);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SocketClosedException e){
                type = DisconnectType.NORMAL;
            } finally {
                //触发事件
                onDisconnect.onDisconnect(type);
            }
        }
    }
}
