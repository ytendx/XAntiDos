package de.ytendx.xad;

import de.ytendx.xad.commands.XADCommand;
import de.ytendx.xad.listeners.ConnectionListener;
import de.ytendx.xad.protection.DoSCheck;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;

public class XAntiDos extends Plugin {

    private static XAntiDos instance;
    private DoSCheck doSCheck;

    public static final String VERSION = "v3.0-PUBLIC";

    public static XAntiDos getInstance() {
        return instance;
    }

    public DoSCheck getDoSCheck() {
        return doSCheck;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.getProxy().getLogger().info("[XAD] Starting up XAntiDos...");
        this.doSCheck = new DoSCheck();
        this.getProxy().getLogger().info("[XAD] Initialize dos-check succesfully!");
        new XADCommand();
        this.getProxy().getPluginManager().registerListener(this, new ConnectionListener());
        super.onEnable();
        this.getProxy().getLogger().info("[XAD] Did adaption for BungeeCord.");
        this.getProxy().getLogger().info("[XAD] Succesfully started up!");
    }

    @Override
    public void onDisable() {
        this.doSCheck.cpsThread.stop();
        System.out.println("[XAD] Successfully deactivated and disabled!");
        super.onDisable();
    }
}
