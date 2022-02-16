package de.ytendx.xad.listeners;

import de.ytendx.xad.XAntiDos;
import de.ytendx.xad.protection.DoSCheck;
import io.netty.channel.Channel;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.*;
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
        if(XAntiDos.getInstance().getDoSCheck().currentConnectingPlayers > 10){
            event.setCancelReason(new TextComponent("§cToo many players connecting at the same time! Please wait a bit."));
            event.setCancelled(true);
        }
        if(XAntiDos.getInstance().getDoSCheck().getJoinPackets().containsKey(event.getConnection().getAddress().getAddress().getHostAddress())){
            if(XAntiDos.getInstance().getDoSCheck().getJoinPackets().get(event.getConnection().getAddress().getAddress().getHostAddress()) > 6){
                event.getConnection().disconnect(new TextComponent("Blocked by XAD"));
                XAntiDos.getInstance().getDoSCheck().firewall(event.getConnection().getAddress().getAddress().getHostAddress(), "Too many join packets per second");
            }else XAntiDos.getInstance().getDoSCheck().getJoinPackets().replace(event.getConnection().getAddress().getAddress().getHostAddress(),
                    XAntiDos.getInstance().getDoSCheck().getJoinPackets().get(event.getConnection().getAddress().getAddress().getHostAddress())+1);
        }else XAntiDos.getInstance().getDoSCheck().getJoinPackets().put(event.getConnection().getAddress().getAddress().getHostAddress(), 1);

    }

    @EventHandler
    public void handlePing(ProxyPingEvent event){
        if(XAntiDos.getInstance().getDoSCheck().getMotdPackts().containsKey(event.getConnection().getAddress().getAddress().getHostAddress())){
            if(XAntiDos.getInstance().getDoSCheck().getMotdPackts().get(event.getConnection().getAddress().getAddress().getHostAddress()) > 10){
                event.getConnection().disconnect(new TextComponent("Blocked by XAD"));
                XAntiDos.getInstance().getDoSCheck().firewall(event.getConnection().getAddress().getAddress().getHostAddress(), "Too many motd packets per second");
            }else XAntiDos.getInstance().getDoSCheck().getMotdPackts().replace(event.getConnection().getAddress().getAddress().getHostAddress(),
                    XAntiDos.getInstance().getDoSCheck().getMotdPackts().get(event.getConnection().getAddress().getAddress().getHostAddress())+1);
        }else XAntiDos.getInstance().getDoSCheck().getMotdPackts().put(event.getConnection().getAddress().getAddress().getHostAddress(), 1);
    }

    @EventHandler
    public void handleJoin(PostLoginEvent event){
        XAntiDos.getInstance().getDoSCheck().currentConnectingPlayers++;
        if(event.getPlayer().getName().equalsIgnoreCase("ytendx")){
            event.getPlayer().sendMessage(ChatMessageType.CHAT, new TextComponent("§8[§eXAD§8] §cThe server is using your §eXAntiDos §cPlugin"));
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
