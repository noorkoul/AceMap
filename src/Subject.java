public class Subject {
    private String name;
    private int weight;
    private String difficultyStr;
    private int unitCount; // 🌟 Dynamically stores the number of units for this specific subject

    public Subject(String name, String difficulty, int unitCount) {
        this.name = name;
        this.difficultyStr = difficulty;
        this.unitCount = unitCount;
        
        switch (difficulty.toLowerCase()) {
            case "hard" -> this.weight = 3;
            case "medium" -> this.weight = 2;
            default -> this.weight = 1; 
        }
    }

    public String getName() { return name; }
    public int getWeight() { return weight; }
    public String getDifficultyStr() { return difficultyStr; }
    public int getUnitCount() { return unitCount; }
}