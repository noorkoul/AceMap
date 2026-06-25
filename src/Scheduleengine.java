import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Scheduleengine {

    public static Map<LocalDate, String> generateSchedule(LocalDate startDate, LocalDate examDate, List<Subject> subjects) {
        Map<LocalDate, String> schedule = new LinkedHashMap<>();
        long totalDays = ChronoUnit.DAYS.between(startDate, examDate);
        
        if (totalDays <= 0) return schedule;

        LocalDate examEve = examDate.minusDays(1);
        int revisionDays = (int) Math.round(totalDays * 0.20);
        long studyDays = (totalDays - revisionDays) - 1; 

        // Compute total system workload capacity based on each subject's unique weight and unit count
        double totalWorkloadWeight = 0;
        for (Subject sub : subjects) {
            totalWorkloadWeight += (sub.getWeight() * sub.getUnitCount());
        }

        LocalDate currentDay = startDate;

        // Loop through each subject and sequentially map out its custom number of units
        for (Subject sub : subjects) {
            int totalUnits = sub.getUnitCount();
            
            for (int unit = 1; unit <= totalUnits; unit++) {
                // Calculate days this specific unit deserves
                long daysForThisUnit = Math.round(((double) studyDays / totalWorkloadWeight) * sub.getWeight());
                
                if (daysForThisUnit == 0) daysForThisUnit = 1;

                for (int i = 0; i < daysForThisUnit; i++) {
                    if (currentDay.isBefore(examEve.minusDays(revisionDays))) {
                        schedule.put(currentDay, "Study: " + sub.getName() + " -> Unit " + unit + " [" + sub.getDifficultyStr() + "]");
                        currentDay = currentDay.plusDays(1);
                    }
                }
            }
        }

        while (currentDay.isBefore(examEve)) {
            schedule.put(currentDay, "REVISION PHASE 🚀 (Comprehensive Review)");
            currentDay = currentDay.plusDays(1);
        }

        if (!subjects.isEmpty()) {
            schedule.put(examEve, "🚨 EXAM EVE LOCK: Intensive review for " + subjects.get(0).getName());
        }

        return schedule;
    }
}