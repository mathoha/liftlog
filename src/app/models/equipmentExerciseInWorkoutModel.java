package app.models;

public class equipmentExerciseInWorkoutModel {

    int workoutID, exerciseID, set_nr, kilos, reps;
    String exerciseName;

    public equipmentExerciseInWorkoutModel(int workoutID, int exerciseID, int set_nr, int kilos, int reps, String exerciseName) {
        this.workoutID = workoutID;
        this.exerciseID = exerciseID;
        this.set_nr = set_nr;
        this.kilos = kilos;
        this.reps = reps;
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

    public int getKilos() {
        return kilos;
    }

    public void setKilos(int kilos) {
        this.kilos = kilos;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
}