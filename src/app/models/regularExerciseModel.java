package app.models;

public class regularExerciseModel {

    private int exerciseID;
    private String name,description;

    public regularExerciseModel(int exerciseID, String name, String description){
        this.exerciseID = exerciseID;
        this.name = name;
        this.description = description;

    }

    public regularExerciseModel(int exerciseID,String name) {
        this.exerciseID = exerciseID;
        this.name = name;
    }

    public int getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(int exerciseID) {
        this.exerciseID = exerciseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
