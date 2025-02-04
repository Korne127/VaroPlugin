package de.cuuky.varo.listener;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.cuuky.varo.Main;
import de.cuuky.varo.config.config.ConfigEntry;
import de.cuuky.varo.config.messages.ConfigMessages;
import net.labymod.serverapi.Permission;
import net.labymod.serverapi.bukkit.event.LabyModPlayerJoinEvent;
import net.labymod.serverapi.bukkit.event.PermissionsSendEvent;

public class PermissionSendListener implements Listener {

	public static ArrayList<Player> labyJoined = new ArrayList<>();

	@EventHandler
	public void labyModJoin(LabyModPlayerJoinEvent event) {
		if(ConfigEntry.ONLY_LABYMOD_PLAYER.getValueAsBoolean())
			labyJoined.add(event.getPlayer());

		if(ConfigEntry.KICK_LABYMOD_PLAYER.getValueAsBoolean())
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {

				@Override
				public void run() {
					event.getPlayer().kickPlayer(ConfigMessages.KICK_LABY_MOD.getValue());
				}
			}, 1);
	}

	@EventHandler
	public void onPermissionSend(PermissionsSendEvent event) {
		if(ConfigEntry.DISABLE_LABYMOD_FUNCTIONS.getValueAsBoolean())
			for(Entry<Permission, Boolean> permissionEntry : event.getPermissions().entrySet())
				permissionEntry.setValue(false);
	}
}
