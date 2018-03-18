package app.models;

public class exerciseInGroupModel {

    private int  exerciseID, exerciseGroupID;
    private String exerciseName, exerciseGroupName;

    public exerciseInGroupModel(int exerciseID, int exerciseGroupID, String exerciseName, String exerciseGroupName) {
        this.exerciseID = exerciseID;
        this.exerciseGroupID = exerciseGroupID;
        this.exerciseName = exerciseName;
        this.exerciseGroupName = exerciseGroupName;
    }

    public int getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(int exerciseID) {
        this.exerciseID = exerciseID;
    }

    public int getExerciseGroupID() {
        return exerciseGroupID;
    }

    public void setExerciseGroupID(int exerciseGroupID) {
        this.exerciseGroupID = exerciseGroupID;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getExerciseGroupName() {
        return exerciseGroupName;
    }

    public void setExerciseGroupName(String exerciseGroupName) {
        this.exerciseGroupName = exerciseGroupName;
    }
}