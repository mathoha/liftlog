package app.models;

public class regularExerciseInWorkoutModel {

    int workoutID, exerciseID, set_nr;
    String exerciseName, comment;

    public regularExerciseInWorkoutModel(int workoutID, int exerciseID, String exerciseName, String comment, int set_nr) {
        this.workoutID = workoutID;
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.comment = comment;
        this.set_nr = set_nr;
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

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String description) {
        this.comment = description;
    }

    public int getSet_nr() {
        return set_nr;
    }

    public void setSet_nr(int set_nr) {
        this.set_nr = set_nr;
    }
}