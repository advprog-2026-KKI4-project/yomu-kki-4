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
        long rawSum = clan.getMembers().stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()))
                .mapToLong(ClanMember::getLocalScore)
                .sum();

        long newTotal = Math.round(rawSum * clan.getActiveMultiplier());

        clan.setTotalScore(newTotal);
        clanRepository.save(clan);
    }

    @Transactional
    public void endCurrentSeason() {
        List<Clan> allClans = clanRepository.findAllByOrderByTotalScoreDesc();

        if (allClans.isEmpty()) return;

        int totalClans = allClans.size();
        int promotionLimit = (int) Math.ceil(totalClans * 0.10);

        for (int i = 0; i < totalClans; i++) {
            Clan clan = allClans.get(i);
            int currentRank = i + 1;
            clan.setPreviousRank(currentRank);

            if (currentRank <= promotionLimit && clan.getDivision() != Division.DIAMOND) {
                promoteClan(clan);
            }

            clan.setTotalScore(0L);

            for (ClanMember member : clan.getMembers()) {
                member.setLocalScore(0);
            }
        }
        clanRepository.saveAll(allClans);
    }

    private void promoteClan(Clan clan) {
        Division current = clan.getDivision();
        Division next = switch (current) {
            case BRONZE -> Division.SILVER;
            case SILVER -> Division.GOLD;
            case GOLD -> Division.DIAMOND;
            default -> current;
        };
        clan.setDivision(next);
    }

    @Override
    public List<Clan> getGlobalLeaderboard() {
        return clanRepository.findAllByOrderByTotalScoreDesc();
    }

    @Override
    public List<Clan> getDivisionLeaderboard(String division) {
        try {
            Division divisionEnum = Division.valueOf(division.toUpperCase());

            return clanRepository.findAllByDivisionOrderByTotalScoreDesc(divisionEnum);
        } catch (IllegalArgumentException | NullPointerException e) {
            return List.of();
        }
    }
}
