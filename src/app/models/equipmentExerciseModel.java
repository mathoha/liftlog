package app.models;

public class equipmentExerciseModel {

    int exerciseID;
    String exercise_name, equipment_name, equipment_description;

    public equipmentExerciseModel(int exerciseID,String exercise_name, String equipment_name,  String equipment_description) {
        this.exerciseID = exerciseID;
        this.exercise_name = exercise_name;
        this.equipment_name = equipment_name;
        this.equipment_description = equipment_description;

    }

    public int getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(int exerciseID) {
        this.exerciseID = exerciseID;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public String getEquipment_name() {
        return equipment_name;
    }

    public void setEquipment_name(String equipment_name) {
        this.equipment_name = equipment_name;
    }

    public String getEquipment_description() {
        return equipment_description;
    }

    public void setEquipment_description(String equipment_description) {
        this.equipment_description = equipment_description;
    }
}