package cn.banto;


public class Config {

    private String network;
    private String natIp;
    private String natMac;

    private String entry;
    private boolean dhcp = false;
    private String username;
    private String password;
    private String version = "3.8.2";


    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getNatIp() {
        return natIp;
    }

    public void setNatIp(String natIp) {
        this.natIp = natIp;
    }

    public String getNatMac() {
        return natMac;
    }

    public void setNatMac(String natMac) {
        this.natMac = natMac;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public boolean isDhcp() {
        return dhcp;
    }

    public void setDhcp(boolean dhcp) {
        this.dhcp = dhcp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
