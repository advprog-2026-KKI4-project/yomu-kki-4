package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.*;
import id.ac.ui.cs.advprog.yomu.clans.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;
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

    @Override
    @Transactional
    public void endCurrentSeason() {
        List<Clan> allClans = clanRepository.findAllByOrderByTotalScoreDesc();

        Map<Division, List<Clan>> clansByDivision = allClans.stream()
                .collect(Collectors.groupingBy(Clan::getDivision));

        for (Division division : Division.values()) {
            List<Clan> divisionClans = clansByDivision.getOrDefault(division, List.of());

            if (divisionClans.isEmpty())
                continue;

            int totalClans = divisionClans.size();

            int promoteLimit = Math.max(1, (int) Math.ceil(totalClans * 0.20));
            int relegateLimit = Math.max(1, (int) Math.ceil(totalClans * 0.20));

            for (int i = 0; i < totalClans; i++) {
                Clan clan = divisionClans.get(i);
                int currentRank = i + 1;

                clan.setPreviousRank(currentRank);

                if (currentRank <= promoteLimit) {
                    clan.setDivision(clan.getDivision().next());
                } else if (currentRank > (totalClans - relegateLimit) && totalClans >= 3) {
                    clan.setDivision(clan.getDivision().previous());
                }

                clan.setTotalScore(0L);
                clan.getMembers().forEach(m -> m.setLocalScore(0));
            }
        }

        clanRepository.saveAll(allClans);
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
