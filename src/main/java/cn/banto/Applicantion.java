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
        final Supplicant supplicant = new Supplicant();
        //读取配置
        File file = new File("./config.json");
        if (!file.exists()) {
            this.buildConfig(supplicant, file);
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
        supplicant.setTimeout(5000);
        supplicant.setListener(this);
        supplicant.setNetWorkInfo(netWorkInfo);
        supplicant.setServer(InetAddress.getByName(config.getServer()));
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
        supplicant.connect(config.getUsername(), config.getPassword(), config.getEntry(), config.isDhcp(), config.getVersion());
    }

    /**
     * 创建配置文件
     * @param supplicant
     * @param file
     * @throws
     */
    public void buildConfig(Supplicant supplicant, File file) throws SupplicantException, UnknownHostException {
        Config config = new Config();
        Scanner scanner = new Scanner(System.in);
        //设置网卡信息
        System.out.print("是否启动NAT穿透(y/n)?");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {

            System.out.print("请输入路由器IP:");
            config.setNatIp(scanner.nextLine().trim());
            System.out.print("请输入路由器MAC:");
            config.setNatMac(scanner.nextLine().trim());

            NetWorkInfo info = new NetWorkInfo();
            info.setIp(config.getNatIp());
            info.setMac(config.getNatMac());
            supplicant.setNetWorkInfo(info);

        } else {

            System.out.println("请选择网卡:");
            List<NetWorkInfo> list = NetWorkUtils.getNetworkInterfaces();
            for(int i = 0; i < list.size(); ++i) {
                NetWorkInfo info = list.get(i);
                System.out.println(i + 1 + " : " + info.getName() + "(IP:" + info.getIp() + ")");
            }

            System.out.print("请输入序号:");
            NetWorkInfo info = list.get(scanner.nextInt() - 1);
            config.setNetwork(info.getName());
            //解决直接跳过的bug
            scanner.nextLine();

            supplicant.setNetWorkInfo(info);
        }

        //认证服务器
        System.out.print("请输入认证服务器IP(留空则自动搜索): ");
        String server = scanner.nextLine().trim();
        if (server.length() == 0) {
            server = supplicant.search();
            System.out.println();
            System.out.println("已搜索到服务器:" + server);
        }
        config.setServer(server);
        supplicant.setServer(InetAddress.getByName(config.getServer()));

        //认证方式
        List<String> entries = supplicant.getEntries();
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
