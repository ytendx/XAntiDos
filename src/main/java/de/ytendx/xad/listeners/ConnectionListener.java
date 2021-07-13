package de.ytendx.xad.listeners;

import de.ytendx.xad.XAntiDos;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;

public class ConnectionListener implements Listener {

    @EventHandler
    public void handleConnection(ClientConnectEvent event) {
        XAntiDos.getInstance().getDoSCheck().totalConnections++;
        if (!((InetSocketAddress) event.getSocketAddress()).getAddress().getHostAddress().equalsIgnoreCase("127.0.0.1")) {
            if (!XAntiDos.getInstance().getDoSCheck().handleConnection(event.getSocketAddress())) {
                XAntiDos.getInstance().getDoSCheck().blockedConnections++;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handleLogin(PostLoginEvent event) {
        System.out.println("Country: " + event.getPlayer().getLocale().getCountry());
    }

    @EventHandler
    public void handlePreLogin(PreLoginEvent event){
        XAntiDos.getInstance().getDoSCheck().currentConnectingPlayers++;
        if(XAntiDos.getInstance().getDoSCheck().currentConnectingPlayers > 10){
            event.setCancelReason(new TextComponent("§cToo many players connecting at the same time! Please wait a bit."));
            event.setCancelled(true);
            return;
        }
        if(ProxyServer.getInstance().getConfig().isOnlineMode()){
            if(!event.getConnection().isOnlineMode() || !event.getConnection().isLegacy()){
                event.getConnection().disconnect(new TextComponent("Blocked by XAntiDos"));
                System.out.println("[/" + ((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress() + "] -> Player is suspicious!");
                XAntiDos.getInstance().getDoSCheck().blockIP(((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress());
            }
        }else{
            if (XAntiDos.getInstance().getDoSCheck().isBlockedName(event.getConnection().getName())) {
                event.getConnection().disconnect(new TextComponent("Blocked by XAntiDos"));
                System.out.println("[/" + ((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress() + "] -> Playername contains blocked chars!");
                XAntiDos.getInstance().getDoSCheck().blockIP(((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress());
            }else if(XAntiDos.getInstance().getDoSCheck().containsLetters(event.getConnection().getName())){
                event.getConnection().disconnect(new TextComponent("Blocked by XAntiDos"));
                System.out.println("[/" + ((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress() + "] -> Playername only contains Numbers!");
                XAntiDos.getInstance().getDoSCheck().blockIP(((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress());
            }
        }
    }

    @EventHandler
    public void handleHandshake(PlayerHandshakeEvent event) {
        if (event.getHandshake().getHost().equalsIgnoreCase("localhost") || event.getHandshake().getHost().equalsIgnoreCase("127.0.0.1")) {
            XAntiDos.getInstance().getDoSCheck().blockedConnections++;
            System.out.println("[/" + ((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress() + "] -> Connected with local IP (QueueBot/InstantCrasher)");
            event.getConnection().disconnect(new TextComponent("Blocked by XAntiDos"));
            XAntiDos.getInstance().getDoSCheck().blockIP(((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress());
        }
        if (!(event.getHandshake().getRequestedProtocol() == 1 || event.getHandshake().getRequestedProtocol() == 2)) {
            XAntiDos.getInstance().getDoSCheck().blockedConnections++;
            System.out.println("[/" + ((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress() + "] -> Invalid Request Protocol");
            event.getConnection().disconnect(new TextComponent("Blocked by XAntiDos"));
            XAntiDos.getInstance().getDoSCheck().blockIP(((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress());
        }
        XAntiDos.getInstance().getDoSCheck().totalConnections++;
        if (XAntiDos.getInstance().getDoSCheck().handleConnection(event.getConnection().getSocketAddress())) {
            XAntiDos.getInstance().getDoSCheck().blockedConnections++;
            event.getConnection().disconnect(new TextComponent("Blocked by XAntiDos"));
            XAntiDos.getInstance().getDoSCheck().blockIP(((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress());
        }
    }

}
