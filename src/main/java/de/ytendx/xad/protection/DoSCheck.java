package de.ytendx.xad.protection;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class DoSCheck {

    public int totalConnections;
    public int blockedConnections;
    public final ArrayList<ProxiedPlayer> notifiers;
    private int cps;
    private int cpsPeak;
    private CopyOnWriteArrayList<String> blockedIps;
    private final HashMap<String, Long> lastConnections;
    private ConcurrentHashMap<String, Integer> packetCount;
    private CopyOnWriteArrayList<String> whitelisted;
    public int currentConnectingPlayers;
    private boolean notified = false;
    public final Thread cpsThread;

    public DoSCheck() {
        this.cps = 0;
        this.cpsPeak = 0;
        this.blockedConnections = 0;
        this.blockedIps = new CopyOnWriteArrayList<>();
        this.lastConnections = new HashMap<>();
        this.notifiers = new ArrayList<>();
        this.packetCount = new ConcurrentHashMap<>();
        this.whitelisted = new CopyOnWriteArrayList<>();
        AtomicInteger connsBefore = new AtomicInteger();
        this.cpsThread = new Thread(() -> {
            while (true) {
                this.cps = totalConnections - connsBefore.get();
                connsBefore.set(totalConnections);
                this.currentConnectingPlayers = 0;
                packetCount = new ConcurrentHashMap<>();
                if(cps > 100){
                    if(!notified){
                        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
                            if(!player.hasPermission("xad.info")){
                                continue;
                            }
                            notified = true;
                            player.sendMessage(new TextComponent("§8[§eXAD§8] §7The server is §cunder attack§7. §7(§bSTRENGTH: " + (this.cps > 10000 ? "§4Strong" : this.cps > 5000 ? "§cNormal" : "§eLightweight") + "§7)"));
                        }
                    }
                }else{
                    if(notified){
                        for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
                            if(!player.hasPermission("xad.info")){
                                continue;
                            }
                            notified = false;
                            player.sendMessage(new TextComponent("§8[§eXAD§8] §7The attack §astopped§7! (§c<100CPS)"));
                        }
                    }
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException exception) {
                }
            }
        }, "CPS-Showing Thread (XAntiDos)");
        this.cpsThread.start();
        new Thread(() -> {
            while (true) {
                if(this.blockedIps.size() > 0){
                    for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()){
                        if(!player.hasPermission("xad.info")) {
                            continue;
                        }
                        player.sendMessage(new TextComponent("§8[§eXAD§8] §7The firewall was reseted! §7(§bSIZE: §f" + this.blockedIps.size() + "§7)"));
                    }
                    this.blockedIps = new CopyOnWriteArrayList<>();
                }
                try {
                    Thread.sleep(60000L);
                } catch (InterruptedException exception) {
                }
            }
        }, "Firewall Reset").start();
    }

    public boolean handleConnection(final SocketAddress address) {
        if (blockedIps.contains(((InetSocketAddress) address).getAddress().getHostAddress())) return false;
        final InetAddress ad = ((InetSocketAddress) address).getAddress();
        if (!lastConnections.containsKey(ad.getHostAddress())) {
            lastConnections.put(ad.getHostAddress(), System.currentTimeMillis());
        }
        if(!this.packetCount.containsKey(ad.getHostAddress())){
            this.packetCount.put(ad.getHostAddress(), 1);
        }else{
            if(this.packetCount.get(ad.getHostAddress()) > 20){
                firewall(ad.getHostAddress(), "Too many connections in last second >20");
                return false;
            }else this.packetCount.replace(ad.getHostAddress(), this.packetCount.get(ad.getHostAddress())+1);
        }
        /*if (System.currentTimeMillis() - lastConnections.get(ad.getHostAddress()) < 1) {
            firewall(ad.getHostAddress(), "Next connection in lower than 1ms");
            return false;
        }*/

        if (System.currentTimeMillis() - lastConnections.get(ad.getHostAddress()) > 1500 && !this.whitelisted.contains(ad.getHostAddress())) {
            firewall(ad.getHostAddress(), "Sent no minecraft packets");
            return false;
        }

        return true;
    }

    public void firewall(String ip, String cause){
        System.out.println("[/" + ip + "] -> Got firewalled! (Cause: " + cause + ")");
        this.blockedIps.add(ip);
    }

    public void adapt(PlayerHandshakeEvent event){
        whitelisted.add(event.getConnection().getAddress().getAddress().getHostAddress());
    }

    public boolean containsLetters(String s) {
        for(char c : s.toCharArray()) {
            String string = String.valueOf(c);
            if(string.matches("[a-zA-Z]")) {
                return true;
            }
        }
        return false;
    }

    public boolean isBlockedName(final String name) {
        return name.toLowerCase(Locale.ROOT).contains("cipher") || name.toLowerCase(Locale.ROOT).contains("bot") || name.toLowerCase().contains("lauren") ;
    }
}
