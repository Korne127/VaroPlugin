package de.cuuky.varo.entity.player.event;

import java.util.ArrayList;
import java.util.List;

import de.cuuky.varo.entity.player.VaroPlayer;
import de.cuuky.varo.entity.player.event.events.DeadEvent;
import de.cuuky.varo.entity.player.event.events.JoinEvent;
import de.cuuky.varo.entity.player.event.events.KickEvent;
import de.cuuky.varo.entity.player.event.events.KillEvent;
import de.cuuky.varo.entity.player.event.events.QuitEvent;

public class BukkitEvent {

	private static List<BukkitEvent> events;

	static {
		events = new ArrayList<BukkitEvent>();

		new DeadEvent();
		new KickEvent();
		new JoinEvent();
		new QuitEvent();
		new KillEvent();
	}

	protected BukkitEventType eventType;

	protected BukkitEvent(BukkitEventType eventType) {
		this.eventType = eventType;

		events.add(this);
	}

	public BukkitEvent(VaroPlayer player, BukkitEventType eventType) {
		for(BukkitEvent event : events)
			if(event.getEventType().equals(eventType))
				event.onExec(player);
	}

	public BukkitEventType getEventType() {
		return eventType;
	}

	public void onExec(VaroPlayer player) {}
}
