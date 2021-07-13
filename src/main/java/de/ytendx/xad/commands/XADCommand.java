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
        if (commandSender instanceof ProxiedPlayer) {
            if (strings.length == 1) {
                if (strings[0].equalsIgnoreCase("notify")) {
                    XAntiDos.getInstance().getDoSCheck().notifiers.add((ProxiedPlayer) commandSender);
                }
            }else{
                if(commandSender.hasPermission("xad.command")){
                    commandSender.sendMessage(new TextComponent("§c§lXAntiDos §7by §eytendx"));
                    commandSender.sendMessage(new TextComponent(" "));
                    commandSender.sendMessage(new TextComponent("§eUse:"));
                    commandSender.sendMessage(new TextComponent("§f/xad notify §7- §eShows notifications"));
                    commandSender.sendMessage(new TextComponent("§f/xad reset §7- §eResets blocked IPs"));
                }
            }
        }
    }
}
