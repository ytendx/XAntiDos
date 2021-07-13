package de.ytendx.xad;

import de.ytendx.xad.commands.XADCommand;
import de.ytendx.xad.listeners.ConnectionListener;
import de.ytendx.xad.protection.DoSCheck;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;

public class XAntiDos extends Plugin {

    private static XAntiDos instance;
    private DoSCheck doSCheck;

    public static XAntiDos getInstance() {
        return instance;
    }

    public DoSCheck getDoSCheck() {
        return doSCheck;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.getProxy().getLogger().info(
                "\n              ___    __   __   __  \n" +
                    "\\_/  /\\  |\\ |  |  | |  \\ /  \\ /__` \n" +
                    "/ \\ /~~\\ | \\|  |  | |__/ \\__/ .__/ \n" +
                    "                                   ");
        this.doSCheck = new DoSCheck();
        new XADCommand();
        this.getProxy().getPluginManager().registerListener(this, new ConnectionListener());
        super.onEnable();
        try {
            this.toggleIPTables(25565, true);
            this.toggleIPTables(25577, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        this.doSCheck.cpsThread.stop();
        try {
            this.toggleIPTables(25565, false);
            this.toggleIPTables(25577, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[XAD] Successfully deactivated and disabled!");
        super.onDisable();
    }

    public void toggleIPTables(int port, boolean enable) throws IOException {
        if(enable){
            // SIMPLY LIMITS THE CONNECTION AMOUNT
            Runtime.getRuntime().exec("sudo iptables -A INPUT -p tcp --syn --dport " + port + " -m connlimit --connlimit-above 10 --connlimit-mask 20 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A INPUT -p udp --dport " + port + " -m connlimit --connlimit-above 2 --connlimit-mask 10 -j DROP");
            // BLOCK ALL KNOWN INVALID PACKETS BY THE SERVER AND PACKETS WITH NO CONTENT
            Runtime.getRuntime().exec("sudo iptables -A INPUT -p tcp --tcp-flags ALL ALL -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A INPUT -p tcp --tcp-flags ALL NONE -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A INPUT -m state --state INVALID -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A FORWARD -m state --state INVALID -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A OUTPUT -m state --state INVALID -j DROP");
            // BLOCKS SPOOFED PACKETS
            Runtime.getRuntime().exec("sudo iptables -t mangle -A PREROUTING -s 224.0.0.0/3 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -A PREROUTING -s 169.254.0.0/16 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -A PREROUTING -s 172.16.0.0/12 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -A PREROUTING -s 192.0.2.0/24 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -A PREROUTING -s 192.168.0.0/16 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -A PREROUTING -s 10.0.0.0/8 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -A PREROUTING -s 0.0.0.0/8 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -A PREROUTING -s 240.0.0.0/5 -j DROP");
            // BLOCKS SPECIFIC TCP PACKETS PER FLAGS
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags FIN,SYN,RST,PSH,ACK,URG NONE -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags FIN,SYN FIN,SYN -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags SYN,RST SYN,RST -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags FIN,RST FIN,RST -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags FIN,ACK FIN -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags ACK,URG URG -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags ACK,FIN FIN -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags ACK,PSH PSH -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags ALL ALL -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags ALL NONE -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags ALL FIN,PSH,URG -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags ALL SYN,FIN,PSH,URG -j DROP");
            Runtime.getRuntime().exec("sudo iptables -A PREROUTING -p tcp --tcp-flags ALL SYN,RST,ACK,FIN,URG -j DROP");
        }else{
            Runtime.getRuntime().exec("sudo iptables -D INPUT -p tcp --syn --dport " + port + " -m connlimit --connlimit-above 10 --connlimit-mask 20 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D INPUT -p udp --dport " + port + " -m connlimit --connlimit-above 2 --connlimit-mask 10 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D INPUT -p tcp --tcp-flags ALL ALL -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D INPUT -p tcp --tcp-flags ALL NONE -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D INPUT -m state --state INVALID -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D FORWARD -m state --state INVALID -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D OUTPUT -m state --state INVALID -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -D PREROUTING -s 224.0.0.0/3 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -D PREROUTING -s 169.254.0.0/16 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -D PREROUTING -s 172.16.0.0/12 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -D PREROUTING -s 192.0.2.0/24 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -D PREROUTING -s 192.168.0.0/16 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -D PREROUTING -s 10.0.0.0/8 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -D PREROUTING -s 0.0.0.0/8 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -t mangle -D PREROUTING -s 240.0.0.0/5 -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags FIN,SYN,RST,PSH,ACK,URG NONE -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags FIN,SYN FIN,SYN -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags SYN,RST SYN,RST -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags FIN,RST FIN,RST -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags FIN,ACK FIN -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags ACK,URG URG -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags ACK,FIN FIN -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags ACK,PSH PSH -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags ALL ALL -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags ALL NONE -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags ALL FIN,PSH,URG -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags ALL SYN,FIN,PSH,URG -j DROP");
            Runtime.getRuntime().exec("sudo iptables -D PREROUTING -p tcp --tcp-flags ALL SYN,RST,ACK,FIN,URG -j DROP");
        }
        Runtime.getRuntime().exec("service iptables save");
    }
}
