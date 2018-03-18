package app.models;

public class exerciseGroupModel {

    private int exerciseGroupID;
    private String exerciseGroupName;

    public exerciseGroupModel( int exerciseGroupID, String exerciseGroupName) {
        this.exerciseGroupID = exerciseGroupID;
        this.exerciseGroupName = exerciseGroupName;
    }


    public int getExerciseGroupID() {
        return exerciseGroupID;
    }

    public void setExerciseGroupID(int exerciseGroupID) {
        this.exerciseGroupID = exerciseGroupID;
    }


    public String getExerciseGroupName() {
        return exerciseGroupName;
    }

    public void setExerciseGroupName(String exerciseGroupName) {
        this.exerciseGroupName = exerciseGroupName;
    }
}