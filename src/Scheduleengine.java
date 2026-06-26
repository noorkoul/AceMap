import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Scheduleengine {

    public static Map<LocalDate, String> generateSchedule(LocalDate startDate, LocalDate examDate, List<Subject> subjects) {
        Map<LocalDate, String> schedule = new LinkedHashMap<>();
        long totalDays = ChronoUnit.DAYS.between(startDate, examDate);
        
        if (totalDays <= 0 || subjects.isEmpty()) return schedule;

        LocalDate examEve = examDate.minusDays(1);
        int revisionDays = 4; 
        LocalDate studyCutoffDate = examDate.minusDays(revisionDays);
        
        if (studyCutoffDate.isBefore(startDate)) {
            studyCutoffDate = examEve;
        }

        // 1. Flatten all units into our master task queue
        List<String> masterTaskQueue = new ArrayList<>();
        for (Subject sub : subjects) {
            for (int unit = 1; unit <= sub.getUnitCount(); unit++) {
                masterTaskQueue.add(sub.getName() + " -> Unit " + unit + " [" + sub.getDifficultyStr() + "]");
                
                if (sub.getDifficultyStr().equalsIgnoreCase("Hard")) {
                    masterTaskQueue.add(sub.getName() + " -> Unit " + unit + " (Deep Dive) [" + sub.getDifficultyStr() + "]");
                }
            }
        }

        int totalTasks = masterTaskQueue.size();
        long totalStudyDaysAvailable = ChronoUnit.DAYS.between(startDate, studyCutoffDate);
        if (totalStudyDaysAvailable <= 0) totalStudyDaysAvailable = 1;

        LocalDate currentDay = startDate;

        // 2. Distribute tasks sequentially across the timeline
        for (int i = 0; i < totalTasks; i++) {
            String task = masterTaskQueue.get(i);
            
            if (currentDay.isBefore(studyCutoffDate)) {
                if (schedule.containsKey(currentDay)) {
                    String existingTasks = schedule.get(currentDay);
                    schedule.put(currentDay, existingTasks + " & " + task);
                } else {
                    schedule.put(currentDay, "Study: " + task);
                }
                
                double tasksPerDay = (double) totalTasks / totalStudyDaysAvailable;
                if (tasksPerDay <= 1.0 || (i + 1) % Math.ceil(tasksPerDay) == 0) {
                    currentDay = currentDay.plusDays(1);
                }
            } else {
                // Compression safety fallback
                LocalDate finalStudyDay = studyCutoffDate.minusDays(1);
                if (finalStudyDay.isBefore(startDate)) finalStudyDay = startDate;
                
                String existing = schedule.getOrDefault(finalStudyDay, "Study: ");
                schedule.put(finalStudyDay, existing + " & " + task);
            }
        }

        // 🌟 THE FIX: Seamlessly fill ANY unassigned intermediate dates up until the exam day
        while (currentDay.isBefore(examDate)) {
            if (currentDay.equals(examEve)) {
                schedule.put(currentDay, "🚨 EXAM EVE LOCK: Intensive review for " + subjects.get(0).getName());
            } else {
                schedule.put(currentDay, "REVISION PHASE 🚀 (Comprehensive Review)");
            }
            currentDay = currentDay.plusDays(1); // Safely advances day-by-day without skipping!
        }

        return schedule;
    }
}