package de.cuuky.varo.listener.lists;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.cuuky.varo.Main;
import de.cuuky.varo.config.messages.ConfigMessages;

public class BlockedItemsListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getItem() == null)
			return;

		if(!Main.getDataManager().getItemHandler().getBlockedItems().isBlocked(event.getItem()))
			return;

		event.setCancelled(true);
		event.getPlayer().sendMessage(Main.getPrefix() + ConfigMessages.OTHER_NOT_ALLOWED_CRAFT.getValue());
	}

	@EventHandler
	public void onCraft(CraftItemEvent event) {
		if(event.getCurrentItem() == null)
			return;

		if(!Main.getDataManager().getItemHandler().getBlockedItems().isBlocked(event.getCurrentItem()) && !Main.getDataManager().getItemHandler().getBlockedRecipes().isBlocked(event.getCurrentItem()))
			return;

		event.setCancelled(true);
		((Player) event.getWhoClicked()).sendMessage(Main.getPrefix() + ConfigMessages.OTHER_NOT_ALLOWED_CRAFT.getValue());
	}
}
