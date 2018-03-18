package app;

import app.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class ExistingWorkoutController implements Initializable{

    private ObservableList<equipmentExerciseModel> equipmentExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<regularExerciseModel> regularExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<equipmentExerciseInWorkoutModel> selectedEquipmentExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<regularExerciseInWorkoutModel> selectedRegularExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<exerciseInWorkoutModel> exerciseInWorkoutObservableList = FXCollections.observableArrayList();


    @FXML DatePicker datePicker;
    @FXML TextField timeTextField;
    @FXML Spinner<Integer> durationSpinner = new Spinner<>();
    @FXML Spinner<Integer> shapeSpinner = new Spinner<>();
    @FXML Spinner<Integer> performanceSpinner = new Spinner<>();
    @FXML TextArea noteTextArea;
    @FXML Button editButton;
    @FXML Button saveButton;
    @FXML Button discardButton;

    @FXML Label dateLabel;
    @FXML Label timeLabel;
    @FXML Label durationLabel;
    @FXML Label shapeLabel;
    @FXML Label performanceLabel;
    @FXML Label noteLabel;

    @FXML VBox exerciseSelectionBox;
    @FXML ListView<equipmentExerciseModel> equipmentExerciseList = new ListView<>(equipmentExerciseObservableList);
    @FXML ListView<regularExerciseModel> regularExerciseList = new ListView<>(regularExerciseObservableList);

    @FXML VBox addEquipmentSetsBox;
    @FXML Label selectedEquipmentExerciseLabel;
    @FXML Spinner<Integer> weightSpinner = new Spinner<>();
    @FXML Spinner<Integer> repsSpinner = new Spinner<>();
    @FXML Button addEquipmentSetButton;
    @FXML ListView<equipmentExerciseInWorkoutModel> equipmentExerciseSetList = new ListView<>(selectedEquipmentExerciseObservableList);

    @FXML VBox addRegularSetsBox;
    @FXML Label selectedRegularExerciseLabel;
    @FXML TextArea regularSetComment;
    @FXML Button addRegularSetButton;
    @FXML ListView<regularExerciseInWorkoutModel> regularExerciseSetList = new ListView<>(selectedRegularExerciseObservableList);

    @FXML VBox selectedExercisesBox;
    @FXML ListView<exerciseInWorkoutModel> exerciseInWorkoutList = new ListView<>(exerciseInWorkoutObservableList);


    private equipmentExerciseModel selectedEquipmentExercise;
    private regularExerciseModel selectedRegularExercise;
    private exerciseInWorkoutModel selectedExerciseInWorkout;
    private int selectedWorkoutID = 0;
    private int setCount = 0;




    public void discardButtonPressed() {
        datePicker.setVisible(false);
        timeTextField.setVisible(false);
        durationSpinner.setVisible(false);
        shapeSpinner.setVisible(false);
        performanceSpinner.setVisible(false);
        noteTextArea.setVisible(false);
        editButton.setVisible(true);
        saveButton.setVisible(false);
        discardButton.setVisible(false);

    }


    public void saveButtonPressed() {

        dateLabel.setText(datePicker.getValue().toString());
        timeLabel.setText(timeTextField.getText());
        durationLabel.setText(durationSpinner.getValue().toString());
        shapeLabel.setText(shapeSpinner.getValue().toString());
        performanceLabel.setText(performanceSpinner.getValue().toString());
        noteLabel.setText(noteTextArea.getText());
        noteLabel.setPrefHeight(200);
    }


    public void editButtonPressed(){

        //show input UI
        datePicker.setVisible(true);
        timeTextField.setVisible(true);
        durationSpinner.setVisible(true);
        shapeSpinner.setVisible(true);
        performanceSpinner.setVisible(true);
        noteTextArea.setVisible(true);
        editButton.setVisible(false);
        saveButton.setVisible(true);
        discardButton.setVisible(true);

    }

    public boolean loadSelectedEquipmentExerciseFromDB(){
        try {
            equipmentExerciseSetList.getItems().clear();
            Connection con = DBConnector.getConnection();

            PreparedStatement q = con.prepareStatement("SELECT * FROM Equipment_Exercise_In_Workout NATURAL JOIN Exercise WHERE workoutID =(?) AND exerciseID=(?)");
            q.setInt(1, selectedWorkoutID);
            q.setInt(2, selectedEquipmentExercise.getExerciseID());

            ResultSet rs = q.executeQuery();
            while (rs.next()){
                selectedEquipmentExerciseObservableList.add(new equipmentExerciseInWorkoutModel(rs.getInt("workoutID"), rs.getInt("exerciseID"),rs.getInt("set_nr"),rs.getInt("kilos"),rs.getInt("reps"),rs.getString("name")));
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        equipmentExerciseSetList.setCellFactory(param -> new ListCell<equipmentExerciseInWorkoutModel>() {
            @Override
            protected void updateItem(equipmentExerciseInWorkoutModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getClass() == null) {
                    setText(null);
                } else {
                    setText("Set " + item.getSet_nr() +": " + item.getKilos() + " kg x " + item.getReps() + " reps" );
                }
            }
        });

        equipmentExerciseSetList.setItems(selectedEquipmentExerciseObservableList);
        if (selectedEquipmentExerciseObservableList.isEmpty()){
            return false;
        }
        return true;
    }

    public void equipmentExerciseSelected(){
        selectedEquipmentExercise = equipmentExerciseList.getSelectionModel().getSelectedItem();
        selectedEquipmentExerciseLabel.setText(selectedEquipmentExercise.getExercise_name());

        addEquipmentSetsBox.setVisible(true);

        boolean isAlreadyInWorkout = loadSelectedEquipmentExerciseFromDB();

        if (isAlreadyInWorkout){
            setCount = selectedEquipmentExerciseObservableList.size(); // !! will not work if sets are deleted !!
        }
        else{
            setCount = 0;
        }

    }

    public void addEquipmentSetButtonPressed(){
        selectedEquipmentExercise = equipmentExerciseList.getSelectionModel().getSelectedItem();


        try {
            setCount +=1;
            Connection con = DBConnector.getConnection();
            PreparedStatement stm = con.prepareStatement("INSERT INTO Exercise_In_Workout (workoutID,exerciseID,set_nr) VALUES(?,?,?)");
            stm.setInt(1, selectedWorkoutID);
            stm.setInt(2, selectedEquipmentExercise.getExerciseID());
            stm.setInt(3, setCount);
            stm.execute();
            stm.close();
            System.out.println("Inserted: " + selectedEquipmentExercise.getExercise_name() + " set: " + setCount + " into Exercise table");

            PreparedStatement stm2 = con.prepareStatement("INSERT INTO Equipment_Exercise_In_Workout (workoutID,exerciseID,set_nr,kilos,reps) VALUES(?,?,?,?,?)");
            stm2.setInt(1, selectedWorkoutID);
            stm2.setInt(2, selectedEquipmentExercise.getExerciseID());
            stm2.setInt(3, setCount);
            stm2.setInt(4, weightSpinner.getValue());
            stm2.setInt(5, repsSpinner.getValue());
            stm2.execute();
            stm2.close();

            System.out.println("Inserted: " + selectedEquipmentExercise.getExercise_name() + " set:" +  + setCount + " " + weightSpinner.getValue().toString() + "kg x" +repsSpinner.getValue().toString() +" into Equipment_Exercise_In_Workout table");

        } catch (SQLException e) {
            e.printStackTrace();
        }


        loadSelectedEquipmentExerciseFromDB();
        loadExercisesInWorkoutFromDB();
        selectedExercisesBox.setVisible(true);

    }

    public boolean loadSelectedRegularExerciseFromDB(){
        try {
            regularExerciseSetList.getItems().clear();
            Connection con = DBConnector.getConnection();

            PreparedStatement q = con.prepareStatement("SELECT * FROM Regular_Exercise_In_Workout NATURAL JOIN Exercise WHERE workoutID =(?) AND exerciseID=(?)");
            q.setInt(1, selectedWorkoutID);
            q.setInt(2, selectedRegularExercise.getExerciseID());

            ResultSet rs = q.executeQuery();
            while (rs.next()){
                selectedRegularExerciseObservableList.add(new regularExerciseInWorkoutModel(rs.getInt("workoutID"), rs.getInt("exerciseID"),rs.getString("name"),rs.getString("set_comment"),rs.getInt("set_nr")));
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        regularExerciseSetList.setCellFactory(param -> new ListCell<regularExerciseInWorkoutModel>() {
            @Override
            protected void updateItem(regularExerciseInWorkoutModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getClass() == null) {
                    setText(null);
                } else {
                    setText("Set " + item.getSet_nr() +": " + item.getComment());
                }
            }
        });

        regularExerciseSetList.setItems(selectedRegularExerciseObservableList);
        if (selectedRegularExerciseObservableList.isEmpty()){
            return false;
        }
        return true;
    }

    public void regularExerciseSelected(){
        selectedRegularExercise = regularExerciseList.getSelectionModel().getSelectedItem();
        selectedRegularExerciseLabel.setText(selectedRegularExercise.getName());

        addRegularSetsBox.setVisible(true);

        boolean isAlreadyInWorkout = loadSelectedRegularExerciseFromDB();

        if (isAlreadyInWorkout){
            setCount = selectedRegularExerciseObservableList.size(); // !! will not work if sets are deleted !!
        }
        else{
            setCount = 0;
        }

    }

    public void addRegularSetButtonPressed(){
        selectedRegularExercise = regularExerciseList.getSelectionModel().getSelectedItem();

        try {
            setCount +=1;
            Connection con = DBConnector.getConnection();
            PreparedStatement stm = con.prepareStatement("INSERT INTO Exercise_In_Workout (workoutID,exerciseID,set_nr) VALUES(?,?,?)");
            stm.setInt(1, selectedWorkoutID);
            stm.setInt(2, selectedRegularExercise.getExerciseID());
            stm.setInt(3, setCount);
            stm.execute();
            stm.close();
            System.out.println("Inserted: " + selectedRegularExercise.getName() + " set: " + setCount + " into Exercise table");

            PreparedStatement stm2 = con.prepareStatement("INSERT INTO Regular_Exercise_In_Workout (workoutID,exerciseID,set_nr,set_comment) VALUES(?,?,?,?)");
            stm2.setInt(1, selectedWorkoutID);
            stm2.setInt(2, selectedRegularExercise.getExerciseID());
            stm2.setInt(3, setCount);
            stm2.setString(4, regularSetComment.getText());
            stm2.execute();
            stm2.close();

            System.out.println("Inserted: " + selectedRegularExercise.getName() + " set:" +  + setCount + " with comment: " + regularSetComment.getText() +" into Regular_Exercise_In_Workout table");

        } catch (SQLException e) {
            e.printStackTrace();
        }


        loadSelectedRegularExerciseFromDB();
        loadExercisesInWorkoutFromDB();
        selectedExercisesBox.setVisible(true);

    }

    public void selectedExerciseClicked(){
        //convert into selectedEquipmentExercise object so we can use the same method to get data from DB
        selectedExerciseInWorkout = exerciseInWorkoutList.getSelectionModel().getSelectedItem();
        selectedEquipmentExercise = new equipmentExerciseModel(selectedExerciseInWorkout.getExerciseID(),selectedExerciseInWorkout.getExerciseName());
        selectedRegularExercise = new regularExerciseModel(selectedExerciseInWorkout.getExerciseID(),selectedExerciseInWorkout.getExerciseName());


        ListView equipmentSetsList = new ListView<>(selectedEquipmentExerciseObservableList);

        equipmentSetsList.setCellFactory(param -> new ListCell<equipmentExerciseInWorkoutModel>() {
            @Override
            protected void updateItem(equipmentExerciseInWorkoutModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getClass() == null) {
                    setText(null);
                } else {
                    setText("Set " + item.getSet_nr() +": " + item.getKilos() + " kg x " + item.getReps() + " reps" );
                }
            }
        });

        ListView regularSetsList = new ListView<>(selectedRegularExerciseObservableList);

        regularSetsList.setCellFactory(param -> new ListCell<regularExerciseInWorkoutModel>() {
            @Override
            protected void updateItem(regularExerciseInWorkoutModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getClass() == null) {
                    setText(null);
                } else {
                    setText("Set " + item.getSet_nr() +": " + item.getComment());
                }
            }
        });


        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(selectedExerciseInWorkout.getExerciseName());
        alert.setHeaderText(" ");
        alert.setWidth(300);

        if (selectedExerciseInWorkout.getType().equals("regular")){ //check type
            regularSetsList.setPrefWidth(300);
            regularSetsList.setPrefHeight(300);
            alert.setGraphic(regularSetsList);
        }

        if (selectedExerciseInWorkout.getType().equals("equipment")){ //check type
            equipmentSetsList.setPrefWidth(300);
            equipmentSetsList.setPrefHeight(300);
            alert.setGraphic(equipmentSetsList);
        }



        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(closeButton, deleteButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == deleteButton){

            Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
            deleteAlert.setTitle("Confirm Deletion");
            deleteAlert.setHeaderText("Delete exercise " + selectedExerciseInWorkout.getExerciseName());
            deleteAlert.setContentText("Are sure you want to delete the sets in this workout?");

            Optional<ButtonType> deleteResult = deleteAlert.showAndWait();
            if (deleteResult.get() == ButtonType.OK){
                deleteExerciseFromWorkout(selectedExerciseInWorkout.getExerciseID(),selectedWorkoutID);
                System.out.println(selectedExerciseInWorkout.getExerciseName() + " deleted");
                //TODO: find a way to refresh ExerciseInWorkoutList when an exercise is deleted without bugs... Still a minor bug that is hard to trigger.
                exerciseInWorkoutList.getSelectionModel().getSelectedItem().setExerciseName("");
                exerciseInWorkoutList.refresh();
            } else {
                System.out.println(selectedExerciseInWorkout.getExerciseName() + " not deleted");
            }
        }
        else {

        }


    }

    public void loadExercisesInWorkoutFromDB(){
        try {
            exerciseInWorkoutList.getItems().clear();
            Connection con = DBConnector.getConnection();

            PreparedStatement q = con.prepareStatement("select distinct exerciseID, workoutID, name, type from Exercise_In_Workout natural join Exercise where workoutID = (?)");
            q.setInt(1, selectedWorkoutID);

            ResultSet rs = q.executeQuery();
            while (rs.next()){
                exerciseInWorkoutObservableList.add(new exerciseInWorkoutModel(rs.getInt("workoutID"), rs.getInt("exerciseID"),rs.getString("name"), rs.getString("type")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        exerciseInWorkoutList.setCellFactory(param -> new ListCell<exerciseInWorkoutModel>() {
            @Override
            protected void updateItem(exerciseInWorkoutModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getClass() == null) {
                    setText(null);
                } else {
                    setText(item.getExerciseName());
                }
            }
        });

        exerciseInWorkoutList.setItems(exerciseInWorkoutObservableList);

    }

    private void deleteExerciseFromWorkout(int exerciseID, int workoutID){
        try {
            Connection con = DBConnector.getConnection();
            PreparedStatement stm = con.prepareStatement("DELETE FROM Exercise_In_Workout WHERE exerciseID = (?) AND workoutID = (?)");
            stm.setInt(1, exerciseID);
            stm.setInt(2, workoutID);
            stm.execute();
            stm.close();
            System.out.println("Deleted " + exerciseID + " from workout " + workoutID);
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }





    }





    @Override
    public void initialize(URL location, ResourceBundle resources) {


        //intitialize input format for new workout
        SpinnerValueFactory<Integer> shapeValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 5);
        SpinnerValueFactory<Integer> performanceValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 5);
        SpinnerValueFactory<Integer> durationValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 480, 60, 5);
        shapeSpinner.setValueFactory(shapeValueFactory);
        performanceSpinner.setValueFactory(performanceValueFactory);
        durationSpinner.setValueFactory(durationValueFactory);

        datePicker.setValue(LocalDate.now());

        dateLabel.setVisible(true);
        timeLabel.setVisible(true);
        durationLabel.setVisible(true);
        shapeLabel.setVisible(true);
        performanceLabel.setVisible(true);
        noteLabel.setVisible(true);

        SpinnerValueFactory<Integer> weightValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 50, 5);
        SpinnerValueFactory<Integer> repsValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 5,1);
        weightSpinner.setValueFactory(weightValueFactory);
        repsSpinner.setValueFactory(repsValueFactory);

        exerciseSelectionBox.setVisible(true);
        addEquipmentSetsBox.setVisible(false);
        addRegularSetsBox.setVisible(false);
        selectedEquipmentExerciseLabel.setText("");
        selectedRegularExerciseLabel.setText("");

        selectedExercisesBox.setVisible(true);




        //load list of equipment exercises:
        try {
            Connection con = DBConnector.getConnection();

            ResultSet rs = con.createStatement().executeQuery("select t1.exerciseID, t1.name, Equipment.name, Equipment.description from Equipment join (Select * from Equipment_Exercise natural join Exercise) as t1 where Equipment.equipmentID = t1.equipmentID;");
            while (rs.next()){
                equipmentExerciseObservableList.add(new equipmentExerciseModel(rs.getInt("t1.exerciseID"), rs.getString("t1.name"),rs.getString("Equipment.name"),rs.getString("Equipment.description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        equipmentExerciseList.setCellFactory(param -> new ListCell<equipmentExerciseModel>() {
            @Override
            protected void updateItem(equipmentExerciseModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getClass() == null) {
                    setText(null);
                } else {
                    setText(item.getExercise_name());
                }
            }
        });

        equipmentExerciseList.setItems(equipmentExerciseObservableList);


        //load list of regular exercises:
        try {
            Connection con = DBConnector.getConnection();

            ResultSet rs = con.createStatement().executeQuery("SELECT * from Exercise NATURAL JOIN Regular_Exercise;");
            while (rs.next()){
                regularExerciseObservableList.add(new regularExerciseModel(rs.getInt("exerciseID"), rs.getString("name"),rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        regularExerciseList.setCellFactory(param -> new ListCell<regularExerciseModel>() {
            @Override
            protected void updateItem(regularExerciseModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getClass() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        regularExerciseList.setItems(regularExerciseObservableList);

    }
}
