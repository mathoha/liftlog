package app.models;

public class exerciseInWorkoutModel {

    int workoutID, exerciseID, set_nr;
    String exerciseName, type;

    public exerciseInWorkoutModel(int workoutID, int exerciseID, String exerciseName, String type) {
        this.workoutID = workoutID;
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.type = type;
    }

    public int getWorkoutID() {
        return workoutID;
    }

    public void setWorkoutID(int workoutID) {
        this.workoutID = workoutID;
    }

    public int getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(int exerciseID) {
        this.exerciseID = exerciseID;
    }

    public int getSet_nr() {
        return set_nr;
    }

    public void setSet_nr(int set_nr) {
        this.set_nr = set_nr;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}