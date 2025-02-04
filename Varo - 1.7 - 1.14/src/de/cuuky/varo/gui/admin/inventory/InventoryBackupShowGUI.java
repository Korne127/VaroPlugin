package de.cuuky.varo.gui.admin.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import de.cuuky.varo.entity.player.stats.stat.inventory.InventoryBackup;
import de.cuuky.varo.gui.SuperInventory;
import de.cuuky.varo.gui.utils.PageAction;

public class InventoryBackupShowGUI extends SuperInventory {

	private InventoryBackup backup;

	public InventoryBackupShowGUI(Player opener, InventoryBackup backup) {
		super("§7Inventory: §c" + backup.getVaroPlayer().getName(), opener, 45, false);

		this.backup = backup;

		open();
	}

	@Override
	public boolean onOpen() {
		for(int i = 0; i < backup.getInventory().getInventory().getContents().length; i++)
			inv.setItem(i, backup.getInventory().getInventory().getContents()[i]);

		for(int i = 0; i < backup.getArmor().size(); i++)
			inv.setItem(41 + i, backup.getArmor().get(i));
		return true;
	}

	@Override
	public void onClick(InventoryClickEvent event) {}

	@Override
	public void onInventoryAction(PageAction action) {}

	@Override
	public boolean onBackClick() {
		new InventoryBackupGUI(opener, backup);
		return true;
	}

	@Override
	public void onClose(InventoryCloseEvent event) {}
}
