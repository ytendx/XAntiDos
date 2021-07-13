package de.ytendx.xad.protection;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public class DoSCheck {

    public int totalConnections;
    public int blockedConnections;
    public final ArrayList<ProxiedPlayer> notifiers;
    private int cps;
    private int cpsPeak;
    private final CopyOnWriteArrayList<String> blockedIps;
    private final HashMap<String, Long> lastConnections;
    public int currentConnectingPlayers;
    public final Thread cpsThread;

    public DoSCheck() {
        this.cps = 0;
        this.cpsPeak = 0;
        this.blockedConnections = 0;
        this.blockedIps = new CopyOnWriteArrayList<>();
        this.lastConnections = new HashMap<>();
        this.notifiers = new ArrayList<>();
        AtomicInteger connsBefore = new AtomicInteger();
        this.cpsThread = new Thread(() -> {
            while (true) {
                this.cps = totalConnections - connsBefore.get();
                connsBefore.set(totalConnections);
                if (this.cps > this.cpsPeak) this.cpsPeak = this.cps;
                for (ProxiedPlayer player : notifiers) {
                    player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cCPS: §f" + this.cps + " §8| §cBlocked: §f" + this.blockedConnections + " §8| §cBlockedIPs: §f" + this.blockedIps.size()));
                }
                this.currentConnectingPlayers = 0;
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException exception) {
                }
            }
        }, "CPS-Showing Thread (XAntiDos)");
        this.cpsThread.start();
    }

    public void blockIP(final String ip) {
        this.blockedIps.add(ip);
    }

    public boolean handleConnection(final SocketAddress address) {
        if (blockedIps.contains(((InetSocketAddress) address).getAddress().getHostAddress())) return false;
        final InetAddress ad = ((InetSocketAddress) address).getAddress();
        if (!lastConnections.containsKey(ad.getHostAddress())) {
            lastConnections.put(ad.getHostAddress(), System.currentTimeMillis());
            return true;
        }
        if (System.currentTimeMillis() - lastConnections.get(ad.getHostAddress()) < 32) {
            System.out.println("[/" + ad.getHostAddress() + "] -> Got blocked from the AntiDoS(Next connection in lower than 32ms)");
            this.blockedIps.add(ad.getHostAddress());
            return false;
        }
        return true;
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
        return name.toLowerCase(Locale.ROOT).contains("cipher") || name.toLowerCase(Locale.ROOT).contains("bot") || name.toLowerCase().contains("lauren");
    }
}
