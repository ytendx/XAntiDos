package de.ytendx.xad.commands;

import de.ytendx.xad.XAntiDos;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class XADCommand extends Command {
    public XADCommand() {
        super("xad");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        commandSender.sendMessage(new TextComponent("§8[§eXAD§8] §6§lXAntiDos §7v3 by §eytendx"));
        commandSender.sendMessage(new TextComponent("§8[§eXAD§8] §7Blocks large payload attacks and blocks invalid stuff."));
    }
}
