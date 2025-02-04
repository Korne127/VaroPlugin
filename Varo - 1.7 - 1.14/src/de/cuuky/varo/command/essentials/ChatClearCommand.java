package de.cuuky.varo.command.essentials;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.cuuky.varo.Main;
import de.cuuky.varo.command.VaroCommand;

public class ChatClearCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("varo.chatclear")) {
			sender.sendMessage(VaroCommand.getNoPermission("varo.chatclear"));
			return false;
		}
		
		for (int i=0; i<100; i++) {
			Bukkit.broadcastMessage("");
		}
		
		Bukkit.broadcastMessage(Main.getPrefix() + "§7Der Chat wurde §7gecleart§7!");
		return false;
	}

}