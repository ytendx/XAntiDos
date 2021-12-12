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
    public void handlePreLogin(PreLoginEvent event){
        XAntiDos.getInstance().getDoSCheck().currentConnectingPlayers++;
        if(XAntiDos.getInstance().getDoSCheck().currentConnectingPlayers > 10){
            event.setCancelReason(new TextComponent("§cToo many players connecting at the same time! Please wait a bit."));
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void handleHandshake(PlayerHandshakeEvent event) {
        XAntiDos.getInstance().getDoSCheck().adapt(event);
        if (event.getHandshake().getHost().equalsIgnoreCase("localhost") || event.getHandshake().getHost().equalsIgnoreCase("127.0.0.1")) {
            XAntiDos.getInstance().getDoSCheck().blockedConnections++;
            event.getConnection().disconnect(new TextComponent("Blocked by XAntiDos"));
        }
        if (!(event.getHandshake().getRequestedProtocol() == 1 || event.getHandshake().getRequestedProtocol() == 2)) {
            XAntiDos.getInstance().getDoSCheck().blockedConnections++;
            event.getConnection().disconnect(new TextComponent("Blocked by XAntiDos"));
            XAntiDos.getInstance().getDoSCheck().firewall(((InetSocketAddress) event.getConnection().getSocketAddress()).getAddress().getHostAddress(), "Invalid Request Protocol");
        }
    }

}
