package de.cuuky.varo.command.varo;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.cuuky.varo.Main;
import de.cuuky.varo.command.VaroCommand;
import de.cuuky.varo.config.config.ConfigEntry;
import de.cuuky.varo.entity.player.VaroPlayer;
import de.cuuky.varo.entity.player.stats.stat.PlayerState;
import de.cuuky.varo.game.start.ProtectionTime;
import de.cuuky.varo.listener.helper.cancelable.CancelAbleType;
import de.cuuky.varo.listener.helper.cancelable.VaroCancelAble;
import de.cuuky.varo.logger.logger.EventLogger.LogType;
import de.cuuky.varo.world.border.VaroBorder;

public class FinaleCommand extends VaroCommand {

	private enum FinaleState {
		NO,
		JOIN_PHASE,
		COUNTDOWN_PHASE,
		STARTED;
	}

	private FinaleState Status = FinaleState.NO;
	private int startScheduler;
	private int Countdown;
	private boolean isStarting = false;

	public FinaleCommand() {
		super("finale", "Hauptcommand für das Managen des Finales", "varo.finale");
	}

	@Override
	public void onCommand(CommandSender sender, VaroPlayer vp, Command cmd, String label, String[] args) {
		if(args.length == 0 || (!args[0].equalsIgnoreCase("joinstart") && !args[0].equalsIgnoreCase("hauptstart") && !args[0].equalsIgnoreCase("abort"))) {
			sender.sendMessage(Main.getPrefix() + Main.getProjectName() + " §7Finale Befehle:");
			sender.sendMessage(Main.getPrefix() + Main.getColorCode() + "/varo finale joinStart");
			sender.sendMessage(Main.getPrefix() + Main.getColorCode() + "/varo finale hauptStart [Countdown]");
			sender.sendMessage(Main.getPrefix() + Main.getColorCode() + "/varo finale abort");
			return;
		}

		if(args[0].equalsIgnoreCase("joinstart")) {
			if (Status == FinaleState.JOIN_PHASE) {
				sender.sendMessage(Main.getPrefix() + "Der JoinStart wurde bereits aktiviert.");
				return;
			} else if (Status == FinaleState.COUNTDOWN_PHASE) {
				sender.sendMessage(Main.getPrefix() + "Der Finale-Countdown wurde bereits aktiviert.");
				return;
			} else if (Status == FinaleState.STARTED) {
				sender.sendMessage(Main.getPrefix() + "Das Finale wurde bereits gestartet.");
				return;
			}

			for (VaroPlayer player : VaroPlayer.getAlivePlayer()) {
				Player pl = player.getPlayer();
				if(pl.isOp()) {
					continue;
				}
				
				if(VaroCancelAble.getCancelAble(player, CancelAbleType.FREEZE) == null)
					new VaroCancelAble(CancelAbleType.FREEZE, player);
				
				if (pl.isOnline())
					player.sendMessage(Main.getPrefix() + "Das Finale beginnt bald. Bis zum Finalestart wurden alle gefreezed.");
			}
			
			Main.getGame().setFinaleJoinStart(true);
			Status = FinaleState.JOIN_PHASE;
			ConfigEntry.PLAY_TIME.setValue(-1, true);
			
			sender.sendMessage(Main.getPrefix() + Main.getColorCode() + "Es können nun alle zum Finale joinen.");
			sender.sendMessage(Main.getPrefix() + Main.getColorCode() + "Es wird empfohlen, mindestens 5 Minuten zu warten, bis das Finale gestartet wird.");
			sender.sendMessage(Main.getPrefix() + "§c§lWARNUNG: §cBeim Starten mit §7§l/varo finale hauptStart§7 werden alle Spieler, die nicht online sind, getötet.");
			
			Main.getLoggerMaster().getEventLogger().println(LogType.ALERT, "Man kann nun zum Finale joinen!");
			
			return;
		} else if(args[0].equalsIgnoreCase("hauptstart") || args[0].equalsIgnoreCase("mainstart")) {
			if (Status == FinaleState.NO) {
				sender.sendMessage(Main.getPrefix() + "Der Join-Start wurde noch nicht aktiviert. Dies muss vor dem Hauptstart geschehen.");
				return;
			} else if (Status == FinaleState.COUNTDOWN_PHASE) {
				sender.sendMessage(Main.getPrefix() + "Der Finale-Countdown läuft bereits.");
				return;
			} else if (Status == FinaleState.STARTED) {
				sender.sendMessage(Main.getPrefix() + "Das Finale wurde bereits gestartet.");
				return;
			}

			Countdown = 0;
			if (args.length != 1) {
				try {
					Countdown = Integer.parseInt(args[1]);
				} catch(NumberFormatException e) {
					Countdown = 0;
				}
			}
			if (Countdown != 0) {
				Status = FinaleState.COUNTDOWN_PHASE;
				startScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
					@Override
					public void run() {
						if (Countdown != 0) {
							Bukkit.broadcastMessage(Main.getPrefix() + "Das Finale startet in " + Countdown + " Sekunden!");
						} else {
							finaleStart();
							Bukkit.getScheduler().cancelTask(startScheduler);
						}
						Countdown--;
					}
				}, 0, 20);
			} else {
				finaleStart();
			}

			return;
		} else if (args[0].equalsIgnoreCase("abort")) {
			if (Status == FinaleState.NO || Status == FinaleState.JOIN_PHASE) {
				sender.sendMessage(Main.getPrefix() + "Es gibt keinen Countdown zum Abbrechen.");
				return;
			} else if (Status == FinaleState.STARTED) {
				sender.sendMessage(Main.getPrefix() + "Das Finale wurde bereits gestartet.");
				return;
			}

			Bukkit.getScheduler().cancelTask(startScheduler);
			Status = FinaleState.JOIN_PHASE;
			Bukkit.broadcastMessage("§7Der Finale-Start wurde §cabgebrochen§7!");
		}
	}

	private void finaleStart() {
		Status = FinaleState.STARTED;

		Bukkit.broadcastMessage(Main.getPrefix() + "§cDAS FINALE STARTET!");
		if (ConfigEntry.FINALE_PROTECTION_TIME.getValueAsInt() > 0) {
			Bukkit.broadcastMessage(Main.getPrefix() + "§7Es gibt " + ConfigEntry.FINALE_PROTECTION_TIME.getValueAsInt() + " Sekunden Schutzzeit.");
			Main.getGame().setProtection(new ProtectionTime(ConfigEntry.FINALE_PROTECTION_TIME.getValueAsInt()));
		} else {
			Bukkit.broadcastMessage(Main.getPrefix() + "§7Es gibt keine Schutzzeit");
		}

		for (VaroPlayer player : VaroPlayer.getVaroPlayer()) {
			if (player.getPlayer().isOnline()) {
				player.getPlayer().teleport(Main.getDataManager().getWorldHandler().getWorld().getSpawnLocation());
			} else {
				if (ConfigEntry.PLAYER_SPECTATE_IN_FINALE.getValueAsBoolean()) {
					player.getStats().setState(PlayerState.SPECTATOR);
				} else {
					player.getStats().setState(PlayerState.DEAD);
				}
			}
			if (VaroCancelAble.getCancelAble(player, CancelAbleType.FREEZE) != null) {
				VaroCancelAble.getCancelAble(player, CancelAbleType.FREEZE).remove();
			}
		}

		VaroBorder border = Main.getDataManager().getWorldHandler().getBorder();
		border.setSize(ConfigEntry.BORDER_SIZE_IN_FINALE.getValueAsInt());

		int playerNumber = VaroPlayer.getOnlinePlayer().size();
		Main.getLoggerMaster().getEventLogger().println(LogType.ALERT, "DAS FINALE STARTET!\nEs nehmen " + playerNumber + "Spieler teil.");
	}
}