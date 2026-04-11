package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.*;
import id.ac.ui.cs.advprog.yomu.clans.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    @Autowired
    private ClanRepository clanRepository;

    @Override
    @Transactional
    public void updateClanScore(Clan clan) {
        long newTotal = clan.getMembers().stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()))
                .mapToLong(ClanMember::getLocalScore)
                .sum();

        clan.setTotalScore(newTotal);
        clanRepository.save(clan);
    }

    @Override
    public List<Clan> getGlobalLeaderboard() {
        return clanRepository.findAllByOrderByTotalScoreDescIdAsc();
    }
}
