package app.models;

public class exerciseInWorkoutModel {

    int workoutID, exerciseID, set_nr, kilos, reps;
    String exerciseName;

    public exerciseInWorkoutModel(int workoutID, int exerciseID, String exerciseName) {
        this.workoutID = workoutID;
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
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
}