package de.cuuky.varo.scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.cuuky.varo.entity.player.VaroPlayer;
import de.cuuky.varo.entity.team.Team;

public class TopScoreList {

	private ArrayList<VaroPlayer> topPlayer;
	private ArrayList<Team> topTeams;

	private Comparator<VaroPlayer> playerSort;
	private Comparator<Team> teamSort;

	public TopScoreList() {
		this.topPlayer = new ArrayList<>();
		this.topTeams = new ArrayList<>();

		playerSort = new Comparator<VaroPlayer>() {

			@Override
			public int compare(VaroPlayer o1, VaroPlayer o2) {
				if(o1.getStats().getKills() == o2.getStats().getKills())
					return 0;

				return o1.getStats().getKills() > o2.getStats().getKills() ? -1 : 1;
			}
		};

		teamSort = new Comparator<Team>() {

			@Override
			public int compare(Team o1, Team o2) {
				if(o1.getKills() == o2.getKills())
					return 0;

				return o1.getKills() > o2.getKills() ? -1 : 1;
			}
		};

		update();
	}

	public void update() {
		topPlayer.clear();
		topTeams.clear();

		for(VaroPlayer player : VaroPlayer.getVaroPlayer()) {
			int kills = player.getStats().getKills();

			if(kills > 0)
				topPlayer.add(player);
		}

		for(Team team : Team.getTeams()) {
			int kills = team.getKills();

			if(kills > 0)
				topTeams.add(team);
		}

		Collections.sort(topPlayer, playerSort);
		Collections.sort(topTeams, teamSort);
	}

	public VaroPlayer getPlayer(int rank) {
		if(rank - 1 < topPlayer.size())
			return (VaroPlayer) topPlayer.toArray()[rank - 1];
		else
			return null;
	}

	public Team getTeam(int rank) {
		if(rank - 1 < topTeams.size())
			return (Team) topTeams.toArray()[rank - 1];
		else
			return null;
	}
}
