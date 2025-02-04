package de.cuuky.varo.listener.lists;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import de.cuuky.varo.Main;
import de.cuuky.varo.config.messages.ConfigMessages;

public class BlockedEnchantmentsListener implements Listener {

	@EventHandler
	public void onEnchant(EnchantItemEvent event) {
		if(event.getItem() == null)
			return;

		for(Enchantment enc : event.getEnchantsToAdd().keySet())
			if(Main.getDataManager().getItemHandler().getBlockedEnchantments().isBlocked(enc, event.getEnchantsToAdd().get(enc))) {
				event.setCancelled(true);
				event.getEnchanter().sendMessage(Main.getPrefix() + ConfigMessages.OTHER_NOT_ALLOWED_CRAFT.getValue());
				return;
			}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.isCancelled())
			return;

		Inventory inv = event.getInventory();

		if(!(inv instanceof AnvilInventory))
			return;

		InventoryView view = event.getView();
		int rawSlot = event.getRawSlot();

		if(rawSlot != view.convertSlot(rawSlot) || rawSlot != 2)
			return;

		ItemStack item = event.getCurrentItem();
		if(item == null)
			return;

		for(Enchantment enc : item.getEnchantments().keySet())
			if(Main.getDataManager().getItemHandler().getBlockedEnchantments().isBlocked(enc, item.getEnchantments().get(enc))) {
				event.setCancelled(true);
				((Player) event.getWhoClicked()).sendMessage(Main.getPrefix() + ConfigMessages.OTHER_NOT_ALLOWED_CRAFT.getValue());
				return;
			}
	}
}
