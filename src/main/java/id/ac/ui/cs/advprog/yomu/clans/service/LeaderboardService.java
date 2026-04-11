package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import java.util.List;

public interface LeaderboardService {
    void updateClanScore(Clan clan);
    List<Clan> getGlobalLeaderboard();
}
