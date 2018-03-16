package app;

import app.models.equipmentExerciseInWorkoutModel;
import app.models.equipmentExerciseModel;
import app.models.exerciseInWorkoutModel;
import app.models.regularExerciseModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

public class NewWorkoutController implements Initializable{

    private ObservableList<equipmentExerciseModel> equipmentExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<regularExerciseModel> regularExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<equipmentExerciseInWorkoutModel> selectedEquipmentExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<exerciseInWorkoutModel> exerciseInWorkoutObservableList = FXCollections.observableArrayList();


    @FXML DatePicker datePicker;
    @FXML TextField timeTextField;
    @FXML Spinner<Integer> durationSpinner = new Spinner<>();
    @FXML Spinner<Integer> shapeSpinner = new Spinner<>();
    @FXML Spinner<Integer> performanceSpinner = new Spinner<>();
    @FXML TextArea noteTextArea;
    @FXML Button createButton;
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
    @FXML ListView<equipmentExerciseModel> regularExerciseSetList = new ListView<>(equipmentExerciseObservableList);

    @FXML VBox selectedExercisesBox;
    @FXML ListView<exerciseInWorkoutModel> exerciseInWorkoutList = new ListView<>(exerciseInWorkoutObservableList);


    private equipmentExerciseModel selectedEquipmentExercise;
    private exerciseInWorkoutModel selectedExerciseInWorkout;
    private int selectedWorkoutID = 0;
    private int setCount = 0;








    public void discardButtonPressed() {
        datePicker.setValue(LocalDate.now());
        timeTextField.clear();
        durationSpinner.getValueFactory().setValue(60);
        shapeSpinner.getValueFactory().setValue(5);
        performanceSpinner.getValueFactory().setValue(5);
        noteTextArea.clear();
    }

    public void createButtonPressed(){

        try {
            Connection con = DBConnector.getConnection();
            PreparedStatement stm = con.prepareStatement("INSERT INTO Workout (workout_date, workout_time, duration, shape, performance, note) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, datePicker.getValue().toString());
            stm.setString(2, timeTextField.getText());
            stm.setInt(3, durationSpinner.getValue());
            stm.setInt(4, shapeSpinner.getValue());
            stm.setInt(5, performanceSpinner.getValue());
            stm.setString(6, noteTextArea.getText());
            stm.execute();

            ResultSet rs = stm.getGeneratedKeys();
            rs.next();
            int autoGeneratedID = rs.getInt(1);
            stm.close();
            System.out.println("Workout with key: " + autoGeneratedID + " inserted");
            selectedWorkoutID = autoGeneratedID;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        //hide input UI and show data on new workout created
        datePicker.setVisible(false);
        timeTextField.setVisible(false);
        durationSpinner.setVisible(false);
        shapeSpinner.setVisible(false);
        performanceSpinner.setVisible(false);
        noteTextArea.setVisible(false);
        createButton.setVisible(false);
        discardButton.setVisible(false);

        dateLabel.setText(datePicker.getValue().toString());
        timeLabel.setText(timeTextField.getText());
        durationLabel.setText(durationSpinner.getValue().toString());
        shapeLabel.setText(shapeSpinner.getValue().toString());
        performanceLabel.setText(performanceSpinner.getValue().toString());
        noteLabel.setText(noteTextArea.getText());
        noteLabel.setPrefHeight(200);

        dateLabel.setVisible(true);
        timeLabel.setVisible(true);
        durationLabel.setVisible(true);
        shapeLabel.setVisible(true);
        performanceLabel.setVisible(true);
        noteLabel.setVisible(true);

        exerciseSelectionBox.setVisible(true);


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

    }

    public void selectedExerciseClicked(){
        selectedExerciseInWorkout = exerciseInWorkoutList.getSelectionModel().getSelectedItem();
        System.out.println(selectedExerciseInWorkout.getWorkoutID() + selectedExerciseInWorkout.getExerciseName()+ selectedExerciseInWorkout.getExerciseID());
        //TODO: Open dialog where you can see sets and delete exerciseInWorkout


    }

    public void loadExercisesInWorkoutFromDB(){
        try {
            exerciseInWorkoutList.getItems().clear();
            Connection con = DBConnector.getConnection();

            PreparedStatement q = con.prepareStatement("select distinct exerciseID, workoutID, name from Exercise_In_Workout natural join Exercise where workoutID = (?)");
            q.setInt(1, selectedWorkoutID);

            ResultSet rs = q.executeQuery();
            while (rs.next()){
                exerciseInWorkoutObservableList.add(new exerciseInWorkoutModel(rs.getInt("workoutID"), rs.getInt("exerciseID"),rs.getString("name")));
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

        dateLabel.setVisible(false);
        timeLabel.setVisible(false);
        durationLabel.setVisible(false);
        shapeLabel.setVisible(false);
        performanceLabel.setVisible(false);
        noteLabel.setVisible(false);

        SpinnerValueFactory<Integer> weightValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 50, 5);
        SpinnerValueFactory<Integer> repsValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 5,1);
        weightSpinner.setValueFactory(weightValueFactory);
        repsSpinner.setValueFactory(repsValueFactory);

        exerciseSelectionBox.setVisible(false);
        addEquipmentSetsBox.setVisible(false);
        addRegularSetsBox.setVisible(false);
        selectedEquipmentExerciseLabel.setText("");
        selectedRegularExerciseLabel.setText("");




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

    }
}
