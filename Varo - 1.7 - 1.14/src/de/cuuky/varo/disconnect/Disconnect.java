package de.cuuky.varo.disconnect;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.cuuky.varo.Main;
import de.cuuky.varo.alert.Alert;
import de.cuuky.varo.alert.AlertType;
import de.cuuky.varo.config.config.ConfigEntry;
import de.cuuky.varo.config.messages.ConfigMessages;
import de.cuuky.varo.entity.player.VaroPlayer;
import de.cuuky.varo.entity.player.stats.stat.PlayerState;
import de.cuuky.varo.entity.player.stats.stat.Strike;
import de.cuuky.varo.game.state.GameState;
import de.cuuky.varo.logger.logger.EventLogger.LogType;

public class Disconnect {

	/*
	 * OLD CODE
	 */

	private static HashMap<String, Integer> scheds = new HashMap<>();
	private static ArrayList<Disconnect> disconnects = new ArrayList<>();

	private int amount = 0;
	private String name;

	public Disconnect(Player p) {
		this.name = p.getName();

		disconnects.add(this);
	}

	public int getDisconnects() {
		return this.amount;
	}

	public void addDisconnect() {
		if(VaroPlayer.getPlayer(name).getNetworkManager().getPing() >= ConfigEntry.NO_DISCONNECT_PING.getValueAsInt() || playerIsDead())
			return;

		amount++;
	}

	public void remove() {
		disconnects.remove(this);
	}

	public String getPlayer() {
		return this.name;
	}

	public boolean check() {
		if(amount <= ConfigEntry.DISCONNECT_PER_SESSION.getValueAsInt())
			return false;

		VaroPlayer vp = VaroPlayer.getPlayer(name);
		vp.getStats().setBan();
		if(vp.getStats().hasTimeLeft())
			vp.getStats().removeCountdown();

		if(ConfigEntry.STRIKE_ON_DISCONNECT.getValueAsBoolean())
			vp.getStats().addStrike(new Strike("Der Server wurde zu oft verlassen.", vp, "CONSOLE"));

		new Alert(AlertType.DISCONNECT, vp.getName() + " hat das Spiel zu oft verlassen! Seine Session wurde entfernt.");
		Main.getLoggerMaster().getEventLogger().println(LogType.ALERT, ConfigMessages.ALERT_DISCONNECT_TOO_OFTEN.getValue(vp));
		Bukkit.broadcastMessage(ConfigMessages.QUIT_TOO_OFTEN.getValue(vp));
		this.remove();
		return true;
	}

	public boolean playerIsDead() {
		Player p = Bukkit.getPlayerExact(name);
		if(p != null)
			if(!p.isDead() && p.getHealth() != 0)
				return false;

		return true;
	}

	public static Disconnect getDisconnect(Player p) {
		for(Disconnect disconnect : disconnects)
			if(disconnect.getPlayer().equals(p.getName()))
				return disconnect;

		return null;
	}

	public static void disconnected(String playerName) {
		if(!ConfigEntry.BAN_AFTER_DISCONNECT_MINUTES.isIntActivated())
			return;

		if(Main.getGame().getGameState() != GameState.STARTED)
			return;

		if(!VaroPlayer.getPlayer(playerName).getStats().isAlive())
			return;

		scheds.put(playerName, Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				if(Bukkit.getPlayerExact(playerName) != null)
					return;

				if(Main.getGame().getGameState() != GameState.STARTED)
					return;

				VaroPlayer vp = VaroPlayer.getPlayer(playerName);
				vp.getStats().removeCountdown();
				vp.getStats().setState(PlayerState.DEAD);
				Bukkit.broadcastMessage(ConfigMessages.QUIT_DISCONNECT_SESSION_END.getValue(vp).replace("%banTime%", String.valueOf(ConfigEntry.BAN_AFTER_DISCONNECT_MINUTES.getValueAsInt())));
			}
		}, (ConfigEntry.BAN_AFTER_DISCONNECT_MINUTES.getValueAsInt() * 60) * 20));
	}

	public static void joinedAgain(String playerName) {
		if(scheds.containsKey(playerName)) {
			Bukkit.getScheduler().cancelTask(scheds.get(playerName));
			scheds.remove(playerName);
		}
	}
}
