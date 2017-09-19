package cn.banto;


import cn.banto.core.DisconnectType;
import cn.banto.core.Supplicant;
import cn.banto.event.SupplicantListener;
import cn.banto.exception.SupplicantException;
import cn.banto.model.NetWorkInfo;
import cn.banto.utils.NetWorkUtils;
import com.alibaba.fastjson.JSON;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Applicantion extends SupplicantListener {

    public static void main(String[] args) throws Exception {
        new Applicantion().run();
    }

    /**
     * 启动入口
     * @throws Exception
     */
    public void run() throws Exception {
        //读取配置
        File file = new File("./config.json");
        if (!file.exists()) {
            buildConfig(file);
        }

        Config config =  JSON.parseObject(new FileInputStream(file), Config.class);

        //设置网卡信息
        NetWorkInfo netWorkInfo;
        if (config.getNetwork() == null) {
            netWorkInfo = new NetWorkInfo();
            netWorkInfo.setIp(config.getNatIp());
            netWorkInfo.setMac(config.getNatMac());
        } else {
            netWorkInfo = NetWorkUtils.getNetWorkInfo(config.getNetwork());
        }

        if (netWorkInfo == null) {
            System.out.println("网卡信息设置错误");
            return;
        }

        //启动supplicant
        final Supplicant supplicant = new Supplicant(netWorkInfo);
        supplicant.setTimeout(5000);
        supplicant.setListener(this);
        supplicant.setDhcp(config.isDhcp());
        supplicant.setVersion(config.getVersion());
        //添加ctrl + c的监听
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    supplicant.disconnect();
                } catch (SupplicantException e) {
                    e.printStackTrace();
                } finally {
                    supplicant.destroy();
                }
            }
        });

        //开始认证
        supplicant.connect(config.getUsername(), config.getPassword(), config.getEntry());
    }

    /**
     * 创建配置文件
     * @param file
     * @throws
     */
    public void buildConfig(File file) throws SupplicantException {
        Config config = new Config();
        Scanner scanner = new Scanner(System.in);

        //设置网卡信息
        NetWorkInfo netWorkInfo = null;
        System.out.print("是否启动NAT穿透(y/n)?");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {

            System.out.print("请输入路由器IP:");
            config.setNatIp(scanner.nextLine().trim());
            System.out.print("请输入路由器MAC:");
            config.setNatMac(scanner.nextLine().trim());

            netWorkInfo.setIp(config.getNatIp());
            netWorkInfo.setMac(config.getNatMac());
        } else {

            System.out.println("请选择网卡:");
            List<NetWorkInfo> list = NetWorkUtils.getNetworkInterfaces();
            for(int i = 0; i < list.size(); ++i) {
                NetWorkInfo info = list.get(i);
                System.out.println(i + 1 + " : " + info.getName() + "(IP:" + info.getIp() + ")");
            }

            System.out.print("请输入序号:");
            netWorkInfo = list.get(scanner.nextInt() - 1);
            config.setNetwork(netWorkInfo.getName());
            //解决直接跳过的bug
            scanner.nextLine();
        }


        //认证方式
        Supplicant supplicant = null;
        List<String> entries = new ArrayList<String>();
        try {
            supplicant = new Supplicant(netWorkInfo);
            entries.addAll(supplicant.getEntries());
        } finally {
            if(supplicant != null){
                supplicant.destroy();
            }
        }
        System.out.println("请选择认证方式:");
        for(int i = 0; i < entries.size(); ++i) {
            System.out.println(i + 1 + " : " + entries.get(i));
        }
        System.out.print("请输入序号:");
        config.setEntry(entries.get(scanner.nextInt() - 1));
        //解决直接跳过的bug
        scanner.nextLine();
        //设置用户名和密码
        System.out.print("请输入用户名:");
        String username = scanner.nextLine().trim();
        config.setUsername(username);
        System.out.print("请输入密码:");
        String password = scanner.nextLine().trim();
        config.setPassword(password);

        //保存配置文件
        String json = JSON.toJSONString(config);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(json.getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onConnect(boolean isSuccess, String msg) {
        System.out.println((isSuccess ? "认证成功" : "认证失败") + "，系统消息：" + msg);
    }

    public void onDisconnect(DisconnectType type) {
        System.out.print("你已与认证服务器断开, 断开原因: "+ type.getRemark());
    }
}
