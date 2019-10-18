package de.cuuky.varo.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.cuuky.varo.Main;

public class PlayerHungerListener implements Listener {

	@EventHandler
	public void on(FoodLevelChangeEvent e) {
		if(!Main.getGame().hasStarted())
			e.setFoodLevel(40);
	}
}
