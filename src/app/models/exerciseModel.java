package app.models;

public class exerciseModel {

    private int exerciseID;
    private String name, type;

    public exerciseModel(int exerciseID, String name, String type){
        this.exerciseID = exerciseID;
        this.name = name;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
