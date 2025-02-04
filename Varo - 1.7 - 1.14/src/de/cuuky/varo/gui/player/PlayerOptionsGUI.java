package de.cuuky.varo.gui.player;

import java.net.URL;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import de.cuuky.varo.Main;
import de.cuuky.varo.entity.player.VaroPlayer;
import de.cuuky.varo.entity.player.stats.Stats;
import de.cuuky.varo.entity.player.stats.stat.PlayerState;
import de.cuuky.varo.entity.player.stats.stat.Rank;
import de.cuuky.varo.gui.SuperInventory;
import de.cuuky.varo.gui.player.PlayerListGUI.PlayerGUIType;
import de.cuuky.varo.gui.utils.PageAction;
import de.cuuky.varo.gui.utils.chat.ChatHook;
import de.cuuky.varo.gui.utils.chat.ChatHookListener;
import de.cuuky.varo.item.ItemBuilder;
import de.cuuky.varo.version.types.Materials;

public class PlayerOptionsGUI extends SuperInventory {

	private VaroPlayer target;
	private Stats stats;
	private PlayerGUIType type;

	public PlayerOptionsGUI(Player opener, VaroPlayer target, PlayerGUIType type) {
		super("§2" + target.getName() + " §7(" + target.getId() + ")", opener, 54, false);

		this.target = target;
		this.stats = target.getStats();
		this.type = type;

		open();
	}

	@Override
	public boolean onOpen() {
		if(getPage() == 1) {
			inv.setItem(8, new ItemBuilder().displayname("§aAdd Kill").itemstack(new ItemStack(Material.APPLE)).build());
			inv.setItem(0, new ItemBuilder().displayname("§cRemove Kill").itemstack(Materials.REDSTONE.parseItem()).build());

			inv.setItem(17, new ItemBuilder().displayname("§aSet Rank").itemstack(new ItemStack(Material.APPLE)).build());
			inv.setItem(9, new ItemBuilder().displayname("§cRemove Rank").itemstack(Materials.REDSTONE.parseItem()).build());

			inv.setItem(26, new ItemBuilder().displayname("§aSet Link").itemstack(new ItemStack(Material.APPLE)).build());
			inv.setItem(18, new ItemBuilder().displayname("§cRemove Link").itemstack(Materials.REDSTONE.parseItem()).build());

			inv.setItem(35, new ItemBuilder().displayname("§aAdd Session").itemstack(new ItemStack(Material.APPLE)).build());
			inv.setItem(27, new ItemBuilder().displayname("§cRemove Session").itemstack(Materials.REDSTONE.parseItem()).build());
		} else if(getPage() == 2) {
			inv.setItem(8, new ItemBuilder().displayname("§aAdd EpisodesPlayed").itemstack(new ItemStack(Material.APPLE)).build());
			inv.setItem(0, new ItemBuilder().displayname("§cRemove EpisodesPlayed").itemstack(Materials.REDSTONE.parseItem()).build());

			inv.setItem(17, new ItemBuilder().displayname("§aReset Countdown").itemstack(new ItemStack(Material.APPLE)).build());
			inv.setItem(9, new ItemBuilder().displayname("§cRemove Countdown").itemstack(Materials.REDSTONE.parseItem()).build());
		}

		updateStats();
		return getPage() == 2;
	}

	private void updateStats() {
		if(getPage() == 1) {
			inv.setItem(4, new ItemBuilder().displayname("§cKills").itemstack(new ItemStack(Material.DIAMOND_SWORD)).lore(new String[] { "§7Current: " + stats.getKills() }).build());
			inv.setItem(13, new ItemBuilder().displayname("§6Rank").itemstack(new ItemStack(Materials.SIGN.parseMaterial())).lore(new String[] { "§7Current: " + (target.getRank() == null ? "/" : target.getRank().getDisplay()) }).build());
			inv.setItem(22, new ItemBuilder().displayname("§5YouTube-Link").itemstack(new ItemStack(Materials.PAPER.parseMaterial())).lore(new String[] { "§7Current: " + stats.getYoutubeLink() }).build());
			inv.setItem(31, new ItemBuilder().displayname("§bSessions").itemstack(new ItemStack(Material.DIAMOND)).lore(new String[] { "§7Current: " + stats.getSessions() }).build());
		} else if(getPage() == 2) {
			inv.setItem(4, new ItemBuilder().displayname("§5EpisodesPlayed").itemstack(new ItemStack(Material.BLAZE_POWDER)).lore(new String[] { "§7Current: " + stats.getSessionsPlayed() }).build());
			inv.setItem(13, new ItemBuilder().displayname("§6Countdown").itemstack(new ItemStack(Materials.SIGN.parseMaterial())).lore(new String[] { "§7Current: " + stats.getCountdown() }).build());

			inv.setItem(36, new ItemBuilder().displayname("§7Change §cWill InventoryClear").itemstack(new ItemStack(Material.ARROW)).lore(new String[] { "§7Current: " + stats.isWillClear() }).build());
			inv.setItem(37, new ItemBuilder().displayname("§7Change §6State").itemstack(new ItemStack(Material.GOLDEN_APPLE)).lore(new String[] { "§7Current: " + stats.getState().getName() }).build());
			inv.setItem(38, new ItemBuilder().displayname("§7Remove §cTimeUntilAddSession").itemstack(new ItemStack(Materials.PAPER.parseMaterial())).lore(new String[] { "§7Current: " + (stats.getTimeUntilAddSession()) }).build());
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {}

	@Override
	public void onClick(InventoryClickEvent event) {
		String itemname = event.getCurrentItem().getItemMeta().getDisplayName();

		if(itemname.contains("EpisodesPlayed")) {
			if(itemname.contains("Add"))
				stats.addSessionPlayed();
			else if(itemname.contains("Remove"))
				stats.setSessionsPlayed(stats.getSessionsPlayed() - 1);
		}

		if(itemname.contains("Countdown")) {
			if(itemname.contains("Reset"))
				stats.removeCountdown();
			else if(itemname.contains("Remove"))
				stats.setCountdown(1);
		}

		if(itemname.contains("Will InventoryClear"))
			stats.setWillClear(!stats.isWillClear());

		if(itemname.contains("Kill")) {
			if(itemname.contains("Add"))
				stats.addKill();
			else if(itemname.contains("Remove"))
				stats.setKills(stats.getKills() - 1);
		}

		if(itemname.contains("Session")) {
			if(itemname.contains("Add"))
				stats.setSessions(stats.getSessions() + 1);
			else if(itemname.contains("Remove"))
				stats.setSessions(stats.getSessions() - 1);
		}

		if(itemname.contains("Link")) {
			if(itemname.contains("Set")) {
				close(false);

				new ChatHook(opener, "Enter Youtube-Link:", new ChatHookListener() {

					public void onChat(String input) {
						if(!isURl(input)) {
							opener.sendMessage(Main.getPrefix() + "Das ist kein Link!");
							reopenSoon();
							return;
						}

						stats.setYoutubeLink(input);
						opener.sendMessage(Main.getPrefix() + "Youtubelink gesetzt!");
						reopenSoon();
					}
				});
			} else if(itemname.contains("Remove"))
				stats.setYoutubeLink(null);
		}

		if(itemname.contains("Rank")) {
			if(itemname.contains("Set")) {
				close(false);

				new ChatHook(opener, "§7Enter Rank", new ChatHookListener() {

					@Override
					public void onChat(String message) {
						target.setRank(new Rank(message));
						opener.sendMessage(Main.getPrefix() + "Rang gesetzt!");
						reopenSoon();
					}
				});
			} else if(itemname.contains("Remove"))
				target.setRank(null);
		}

		if(itemname.contains("State")) {
			PlayerState state = null;
			switch(target.getStats().getState()) {
			case DEAD:
				state = PlayerState.SPECTATOR;
				break;
			case ALIVE:
				state = PlayerState.DEAD;
				break;
			case SPECTATOR:
				state = PlayerState.ALIVE;
				break;
			}

			stats.setState(state);
		}

		if(itemname.contains("TimeUntilAddSession")) {
			stats.setTimeUntilAddSession(null);
			stats.setSessions(stats.getSessions() + 1);
		}

		updateStats();
	}

	@Override
	public void onInventoryAction(PageAction action) {}

	@Override
	public boolean onBackClick() {
		new PlayerGUI(opener, target, type);
		return true;
	}

	public VaroPlayer getTarget() {
		return target;
	}

	private boolean isURl(String link) {
		try {
			new URL(link).openConnection();
			return true;
		} catch(Exception e) {
			return false;
		}
	}
}
