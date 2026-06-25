import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Scheduleengine {

    public static Map<LocalDate, String> generateSchedule(LocalDate startDate, LocalDate examDate, List<Subject> subjects) {
        Map<LocalDate, String> schedule = new LinkedHashMap<>();
        long totalDays = ChronoUnit.DAYS.between(startDate, examDate);
        
        if (totalDays <= 0 || subjects.isEmpty()) return schedule;

        // 🌟 Fix 1: Explicitly establish our boundaries
        LocalDate examEve = examDate.minusDays(1);
        int revisionDays = 4; // Locked revision block
        
        // The study cutoff date is exactly 4 days before the actual exam start
        LocalDate studyCutoffDate = examDate.minusDays(revisionDays);
        long studyDays = ChronoUnit.DAYS.between(startDate, studyCutoffDate);
        
        // Fallback safety layer for ultra-short countdown crunches
        if (studyDays <= 0) {
            studyDays = 1;
            studyCutoffDate = startDate.plusDays(1);
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
        LocalDate currentDay = startDate;

        // 2. Distribute tasks up until the study cutoff date boundary line
        for (int i = 0; i < totalTasks; i++) {
            String task = masterTaskQueue.get(i);
            
            if (currentDay.isBefore(studyCutoffDate)) {
                if (schedule.containsKey(currentDay)) {
                    String existingTasks = schedule.get(currentDay);
                    schedule.put(currentDay, existingTasks + " & " + task);
                } else {
                    schedule.put(currentDay, "Study: " + task);
                }
                
                // Perfectly spreads out the tasks across the core study window days
                double tasksPerDay = (double) totalTasks / studyDays;
                if (tasksPerDay <= 1.0 || (i + 1) % Math.ceil(tasksPerDay) == 0) {
                    currentDay = currentDay.plusDays(1);
                }
            } else {
                // Out of study days? Bundle any left-over tasks onto the final study day
                LocalDate finalStudyDay = studyCutoffDate.minusDays(1);
                if (finalStudyDay.isBefore(startDate)) finalStudyDay = startDate;
                
                String existing = schedule.getOrDefault(finalStudyDay, "Study: ");
                schedule.put(finalStudyDay, existing + " & " + task);
            }
        }

        // 🌟 Fix 2: Reset the pointer to the cutoff date to ensure exactly 4 days of review rows are printed
        currentDay = studyCutoffDate;

        // 3. Fill the exact 4-day block remaining up to the exam date
        while (currentDay.isBefore(examDate)) {
            if (currentDay.equals(examEve)) {
                schedule.put(currentDay, "🚨 EXAM EVE LOCK: Intensive review for " + subjects.get(0).getName());
            } else {
                schedule.put(currentDay, "REVISION PHASE 🚀 (Comprehensive Review)");
            }
            currentDay = currentDay.plusDays(1);
        }

        return schedule;
    }
}