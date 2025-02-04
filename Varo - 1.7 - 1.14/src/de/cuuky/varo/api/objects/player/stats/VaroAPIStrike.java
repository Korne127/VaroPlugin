package de.cuuky.varo.api.objects.player.stats;

import java.util.Date;

import de.cuuky.varo.entity.player.VaroPlayer;
import de.cuuky.varo.entity.player.stats.stat.Strike;

public class VaroAPIStrike {

	private Strike strike;

	public VaroAPIStrike(Strike strike) {
		this.strike = strike;
	}

	public String getReason() {
		return strike.getReason();
	}

	public Date getAcquiredDate() {
		return strike.getAcquiredDate();
	}

	public String getStriker() {
		return strike.getStriker();
	}

	public VaroPlayer getStrikeOwner() {
		return strike.getStriked();
	}
}
