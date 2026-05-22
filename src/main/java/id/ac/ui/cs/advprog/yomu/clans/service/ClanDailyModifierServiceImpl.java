package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.achievement.service.MissionTrackingService;
import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clans.model.Division;
import id.ac.ui.cs.advprog.yomu.clans.repository.ClanRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClanDailyModifierServiceImpl implements ClanDailyModifierService {

    @Autowired
    private ClanRepository clanRepository;

    @Autowired 
    private MissionTrackingService missionTrackingService;

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void evaluateDailyClanModifiers() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        for (Division division : Division.values()) {
            List<Clan> divisionClans = clanRepository.findAllByDivisionOrderByTotalScoreDesc(division);
            int totalInDiv = divisionClans.size();

            for (int i = 0; i < totalInDiv; i++) {
                Clan clan = divisionClans.get(i);
                double newMultiplier = 1.0;

                // Buff for last 3 clans in a division leaderboard (must have at least 4 clans)
                if (totalInDiv >= 4 && i >= totalInDiv - 3) {
                    newMultiplier += 0.1;
                }

                List<Long> activeMemberIds = clan.getMembers().stream()
                        .filter(m -> "ACCEPTED".equals(m.getStatus()))
                        .map(ClanMember::getStudentId)
                        .collect(Collectors.toList());

                if (!activeMemberIds.isEmpty()) {
                    long membersWithMissions = missionTrackingService.getCompletedMissionCountForUsers(activeMemberIds,
                            yesterday);

                    // Buff if > 50% members finished daily mission. Debuff if 0% did.
                    if (membersWithMissions > (activeMemberIds.size() / 2.0)) {
                        newMultiplier += 0.2;
                    } else if (membersWithMissions == 0) {
                        newMultiplier -= 0.2;
                    }
                }

                clan.setActiveMultiplier(Math.max(0.5, newMultiplier));
            }

            clanRepository.saveAll(divisionClans);
        }
    }
}