package app;


import app.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

public class PersonalRecordsController implements Initializable{


    @FXML private HBox selectNWorkoutsBox;
    @FXML Button selectDateInterval;
    @FXML DatePicker fromDate;
    @FXML DatePicker toDate;

    private ObservableList<exerciseModel> exerciseObservableList = FXCollections.observableArrayList();

    @FXML ComboBox<exerciseModel> exerciseComboBox = new ComboBox<>();
    @FXML Label exerciseName;
    @FXML Label oneRMLabel;
    @FXML Label oneRMDateLabel;


    @FXML private TableView PRTableView;
    @FXML private TableColumn<exercisePRModel,Date> col_date;
    @FXML private TableColumn<exercisePRModel,Time> col_time;
    @FXML private TableColumn<exercisePRModel,Integer> col_kilos;
    @FXML private TableColumn<exercisePRModel,Integer> col_reps;
    @FXML private TableColumn<exercisePRModel,Integer> col_1RM;

    @FXML private LineChart<String,Number> oneRMChart;
    @FXML private CategoryAxis x;
    @FXML private NumberAxis y;

    ObservableList<exercisePRModel> PRObservableList = FXCollections.observableArrayList();
    private ObservableList<String> timeObservableList = FXCollections.observableArrayList();






    //-----------------------SelectedWorkoutPane------------------------


    private ObservableList<equipmentExerciseModel> equipmentExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<regularExerciseModel> regularExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<equipmentExerciseInWorkoutModel> selectedEquipmentExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<regularExerciseInWorkoutModel> selectedRegularExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<exerciseInWorkoutModel> exerciseInWorkoutObservableList = FXCollections.observableArrayList();

    @FXML BorderPane selectedWorkoutPane;

    @FXML DatePicker datePicker;
    @FXML ComboBox<String> timeComboBox;
    @FXML Spinner<Integer> durationSpinner = new Spinner<>();
    @FXML Spinner<Integer> shapeSpinner = new Spinner<>();
    @FXML Spinner<Integer> performanceSpinner = new Spinner<>();
    @FXML TextArea noteTextArea;
    @FXML Button editButton;
    @FXML Button saveButton;
    @FXML Button discardButton;
    @FXML Button deleteButton;

    @FXML Label dateLabel;
    @FXML Label timeLabel;
    @FXML Label durationLabel;
    @FXML Label shapeLabel;
    @FXML Label performanceLabel;
    @FXML Label noteLabel;

    @FXML
    VBox exerciseSelectionBox;
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
    private int regSetCount = 0;
    private int eqSetCount = 0;

    private workoutModel selectedWorkout;

    public workoutModel getExistingWorkoutFromDB(int workoutID){
        try {
            selectedWorkout = null;
            Connection con = DBConnector.getConnection();

            PreparedStatement q = con.prepareStatement("SELECT * FROM Workout WHERE workoutID =(?)");
            q.setInt(1, workoutID);

            ResultSet rs = q.executeQuery();
            while (rs.next()){
                selectedWorkout = new workoutModel(rs.getInt("workoutID"), rs.getDate("workout_date"),rs.getTime("workout_time"),rs.getInt("duration"),rs.getInt("shape"),rs.getInt("performance"),rs.getString("note"));
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return selectedWorkout;



    }




    public void enterExistingWorkout(int workoutID) {
        PRTableView.setVisible(false);
        selectedWorkoutPane.setVisible(true);
        selectNWorkoutsBox.setVisible(false);
        exerciseName.setVisible(false);
        oneRMLabel.setVisible(false);
        oneRMDateLabel.setVisible(false);
        oneRMChart.setVisible(false);
        selectedWorkout = getExistingWorkoutFromDB(workoutID);
        selectedWorkoutID = workoutID;
        loadExercisesInWorkoutFromDB();

        dateLabel.setText(selectedWorkout.getDate().toString());
        timeLabel.setText(selectedWorkout.getTime().toString());
        durationLabel.setText((Integer.toString(selectedWorkout.getDuration())));
        shapeLabel.setText((Integer.toString(selectedWorkout.getShape())));
        performanceLabel.setText((Integer.toString(selectedWorkout.getPerformance())));
        noteLabel.setText(selectedWorkout.getNote());

    }

    public void selectDateIntervalButtonPressed(){
        System.out.println("from " + fromDate.getValue() + " To " +toDate.getValue());
        if (fromDate.getValue() != null && toDate.getValue() != null){
            loadPersonalRecordsFromDB();
            PRTableView.setItems(PRObservableList);
            PRTableView.refresh();
        }


    }

    //-----------------------SelectedWorkout------------------------


    public void fillTimeComboBox(){
        for(int i = 4; i <10; i++){
            timeObservableList.add("0"+i+":00");
        }
        for(int i = 10; i <25; i++){
            timeObservableList.add(+i+":00");
        }
        timeComboBox.setItems(timeObservableList);
        timeComboBox.getSelectionModel().select(10);
    }


    public void discardButtonPressed() {
        datePicker.setVisible(false);
        timeComboBox.setVisible(false);
        durationSpinner.setVisible(false);
        shapeSpinner.setVisible(false);
        performanceSpinner.setVisible(false);
        noteTextArea.setVisible(false);
        editButton.setVisible(true);
        saveButton.setVisible(false);
        discardButton.setVisible(false);
        deleteButton.setVisible(true);

    }


    public void saveButtonPressed() {

        dateLabel.setText(datePicker.getValue().toString());
        timeLabel.setText(timeComboBox.getValue());
        durationLabel.setText(durationSpinner.getValue().toString());
        shapeLabel.setText(shapeSpinner.getValue().toString());
        performanceLabel.setText(performanceSpinner.getValue().toString());
        noteLabel.setText(noteTextArea.getText());
        noteLabel.setPrefHeight(200);
        saveButton.setVisible(false);
        discardButton.setVisible(false);
        editButton.setVisible(true);
        deleteButton.setVisible(true);

        datePicker.setVisible(false);
        timeComboBox.setVisible(false);
        durationSpinner.setVisible(false);
        shapeSpinner.setVisible(false);
        performanceSpinner.setVisible(false);
        noteTextArea.setVisible(false);
        editButton.setVisible(true);
        saveButton.setVisible(false);
        discardButton.setVisible(false);
        deleteButton.setVisible(true);

        try {
            Connection con = DBConnector.getConnection();
            PreparedStatement stm = con.prepareStatement("UPDATE Workout SET workout_date = ?, workout_time = ?," +
                    " duration = ?, shape = ?, performance = ?, note = ? WHERE workoutID = ?");
            stm.setString(1, datePicker.getValue().toString());
            stm.setString(2, timeComboBox.getValue());
            stm.setInt(3, durationSpinner.getValue());
            stm.setInt(4, shapeSpinner.getValue());
            stm.setInt(5, performanceSpinner.getValue());
            stm.setString(6, noteTextArea.getText());
            stm.setInt(7, selectedWorkoutID);
            stm.execute();
            stm.close();
            System.out.println("Successfully updated workout: " + selectedWorkoutID );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void editButtonPressed(){
        System.out.println(selectedWorkout.getWorkoutID());

        //load data into input fields
        datePicker.setValue(selectedWorkout.getDate().toLocalDate());
        timeComboBox.setValue(selectedWorkout.getTime().toString());
        durationSpinner.getValueFactory().setValue(selectedWorkout.getDuration());
        shapeSpinner.getValueFactory().setValue(selectedWorkout.getShape());
        performanceSpinner.getValueFactory().setValue(selectedWorkout.getPerformance());
        noteTextArea.setText(selectedWorkout.getNote());
        editButton.setVisible(false);
        deleteButton.setVisible(false);
        saveButton.setVisible(true);
        discardButton.setVisible(true);

        //show input UI
        datePicker.setVisible(true);
        timeComboBox.setVisible(true);
        durationSpinner.setVisible(true);
        shapeSpinner.setVisible(true);
        performanceSpinner.setVisible(true);
        noteTextArea.setVisible(true);
        editButton.setVisible(false);
        deleteButton.setVisible(false);
        saveButton.setVisible(true);
        discardButton.setVisible(true);

    }

    public void deleteButtonPressed(){
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Confirm Deletion");
        deleteAlert.setHeaderText("Delete workout on " + selectedWorkout.getDate().toString());
        deleteAlert.setContentText("Delete this workout?");

        Optional<ButtonType> deleteResult = deleteAlert.showAndWait();
        if (deleteResult.get() == ButtonType.OK){
            try {
                Connection con = DBConnector.getConnection();
                PreparedStatement stm = con.prepareStatement("DELETE FROM Workout WHERE workoutID = ?");
                stm.setInt(1, selectedWorkoutID);
                stm.execute();
                stm.close();
                System.out.println("Deleted workout: " + selectedWorkoutID );

            } catch (SQLException e) {
                e.printStackTrace();
            }
            selectedWorkoutPane.setVisible(false);
            PRTableView.setVisible(true);
            selectNWorkoutsBox.setVisible(true);
            PRObservableList.remove(selectedWorkout);
            PRTableView.setItems(PRObservableList);
            PRTableView.refresh();
        } else {
            System.out.println("workout not deleted");
        }


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
            eqSetCount = selectedEquipmentExerciseObservableList.size(); // !! will not work if sets are deleted !!
        }
        else{
            eqSetCount = 0;
        }

    }

    public void addEquipmentSetButtonPressed(){

        selectedEquipmentExercise = equipmentExerciseList.getSelectionModel().getSelectedItem();
        equipmentExerciseSelected();


        try {
            eqSetCount +=1;
            Connection con = DBConnector.getConnection();
            PreparedStatement stm = con.prepareStatement("INSERT INTO Exercise_In_Workout (workoutID,exerciseID,set_nr) VALUES(?,?,?)");
            stm.setInt(1, selectedWorkoutID);
            stm.setInt(2, selectedEquipmentExercise.getExerciseID());
            stm.setInt(3, eqSetCount);
            stm.execute();
            stm.close();
            System.out.println("Inserted: " + selectedEquipmentExercise.getExercise_name() + " set: " + eqSetCount + " into Exercise table");

            PreparedStatement stm2 = con.prepareStatement("INSERT INTO Equipment_Exercise_In_Workout (workoutID,exerciseID,set_nr,kilos,reps) VALUES(?,?,?,?,?)");
            stm2.setInt(1, selectedWorkoutID);
            stm2.setInt(2, selectedEquipmentExercise.getExerciseID());
            stm2.setInt(3, eqSetCount);
            stm2.setInt(4, weightSpinner.getValue());
            stm2.setInt(5, repsSpinner.getValue());
            stm2.execute();
            stm2.close();

            System.out.println("Inserted: " + selectedEquipmentExercise.getExercise_name() + " set:" +  + eqSetCount + " " + weightSpinner.getValue().toString() + "kg x" +repsSpinner.getValue().toString() +" into Equipment_Exercise_In_Workout table");

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
            regSetCount = selectedRegularExerciseObservableList.size(); // !! will not work if sets are deleted !!
        }
        else{
            regSetCount = 0;
        }

    }

    public void addRegularSetButtonPressed(){
        selectedRegularExercise = regularExerciseList.getSelectionModel().getSelectedItem();
        loadSelectedRegularExerciseFromDB();

        try {
            regSetCount +=1;
            Connection con = DBConnector.getConnection();
            PreparedStatement stm = con.prepareStatement("INSERT INTO Exercise_In_Workout (workoutID,exerciseID,set_nr) VALUES(?,?,?)");
            stm.setInt(1, selectedWorkoutID);
            stm.setInt(2, selectedRegularExercise.getExerciseID());
            stm.setInt(3, regSetCount);
            stm.execute();
            stm.close();
            System.out.println("Inserted: " + selectedRegularExercise.getName() + " set: " + regSetCount + " into Exercise table");

            PreparedStatement stm2 = con.prepareStatement("INSERT INTO Regular_Exercise_In_Workout (workoutID,exerciseID,set_nr,set_comment) VALUES(?,?,?,?)");
            stm2.setInt(1, selectedWorkoutID);
            stm2.setInt(2, selectedRegularExercise.getExerciseID());
            stm2.setInt(3, regSetCount);
            stm2.setString(4, regularSetComment.getText());
            stm2.execute();
            stm2.close();

            System.out.println("Inserted: " + selectedRegularExercise.getName() + " set:" +  + regSetCount + " with comment: " + regularSetComment.getText() +" into Regular_Exercise_In_Workout table");

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

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(selectedExerciseInWorkout.getExerciseName());
        alert.setHeaderText(" ");
        alert.setWidth(300);


        if (selectedExerciseInWorkout.getType().equals("regular")){

            selectedRegularExercise = new regularExerciseModel(selectedExerciseInWorkout.getExerciseID(),selectedExerciseInWorkout.getExerciseName());
            loadSelectedRegularExerciseFromDB();

            System.out.println("reg: " + selectedRegularExercise.getName());

            ListView regularSetsList = new ListView<>(selectedRegularExerciseObservableList);
            regularSetsList.setPrefWidth(300);
            regularSetsList.setPrefHeight(300);

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


            alert.setGraphic(regularSetsList);
        }

        if (selectedExerciseInWorkout.getType().equals("equipment")){ //check type




            selectedEquipmentExercise = new equipmentExerciseModel(selectedExerciseInWorkout.getExerciseID(),selectedExerciseInWorkout.getExerciseName());
            loadSelectedEquipmentExerciseFromDB();

            ListView equipmentSetsList = new ListView<>(selectedEquipmentExerciseObservableList);
            equipmentSetsList.setPrefWidth(300);
            equipmentSetsList.setPrefHeight(300);

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

    public void loadExercises(){
        try {
            exerciseComboBox.getItems().clear();
            Connection con = DBConnector.getConnection();

            PreparedStatement q = con.prepareStatement("SELECT * FROM Exercise Natural Join Equipment_Exercise");

            ResultSet rs = q.executeQuery();
            while (rs.next()){
                exerciseObservableList.add(new exerciseModel(rs.getInt("exerciseID"), rs.getString("name"), rs.getString("type")));
            }
            q.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        exerciseComboBox.setItems(exerciseObservableList);

        //need override toString method to list the name of the equipment in the dropdown while getting the equipmentID as the value.
        StringConverter<exerciseModel> converter = new StringConverter<exerciseModel>() {
            @Override
            public String toString(exerciseModel object) {
                return object.getName();
            }

            @Override
            public exerciseModel fromString(String exerciseID) {
                return exerciseObservableList.stream()
                        .filter(item -> item.getName().equals(exerciseID))
                        .collect(Collectors.toList()).get(0);
            }
        };

        exerciseComboBox.setConverter(converter);

    }



    public void loadPersonalRecordsFromDB(){
        if(! exerciseComboBox.getSelectionModel().isEmpty()){
            try {
                PRObservableList.clear();
                Connection con = DBConnector.getConnection();

                PreparedStatement q = con.prepareStatement("Select kilos, name, reps, t1.workoutID, exerciseID, workout_date, workout_time from Workout inner join \n" +
                        "(select kilos, name, reps, exerciseID, workoutID From Exercise_In_Workout\n" +
                        " natural join Exercise  natural join Equipment_Exercise_In_Workout\n" +
                        " where ExerciseID = ?) as t1  on t1.workoutID = Workout.workoutID \n" +
                        " where Workout.workout_date>=?\n" +
                        "AND Workout.workout_date <=?\n" +
                        "order by workout_date asc, workout_time desc;");
                q.setInt(1, exerciseComboBox.getValue().getExerciseID());
                q.setString(2, fromDate.getValue().toString());
                q.setString(3, toDate.getValue().toString());

                ResultSet rs = q.executeQuery();
                while (rs.next()){
                    PRObservableList.add(new exercisePRModel(rs.getDate("workout_date"),rs.getTime("workout_time"),rs.getInt("exerciseID"),rs.getInt("t1.workoutID"),rs.getInt("reps"),rs.getInt("kilos"),rs.getString("name")));
                }
                q.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(! PRObservableList.isEmpty()){
                //takes out the max value of estimated 1RMs from the selected exercise;
                exercisePRModel oneRMset = PRObservableList.stream().max(Comparator.comparing(exercisePRModel::getOneRM)).get();

                exerciseName.setText(oneRMset.getName());
                oneRMLabel.setText("Estimated 1RM: " + oneRMset.getOneRM() + " kg");
                oneRMDateLabel.setText(oneRMset.getDate().toString() + " (" + oneRMset.getKilos() + " kg x " + oneRMset.getReps() + ")");
                exerciseName.setVisible(true);
                oneRMLabel.setVisible(true);
                oneRMDateLabel.setVisible(true);

                displayOneRMGraph();
                oneRMChart.setVisible(true);
            }
            else{
                exerciseName.setVisible(false);
                oneRMLabel.setVisible(false);
                oneRMDateLabel.setVisible(false);
                oneRMChart.setVisible(false);
            }


        }
    }


    public void displayOneRMGraph(){

        oneRMChart.getData().clear();

        List<exercisePRModel> prList = PRObservableList.stream().collect(Collectors.toList());

        ObservableList<exercisePRModel> sameWorkout = FXCollections.observableArrayList();
        ObservableList<exercisePRModel> prEachWorkout = FXCollections.observableArrayList();

        Date prevDate = prList.get(0).getDate();
        for(int i = 0; i <prList.size(); i++){

            Date workoutDate = prList.get(i).getDate();

            //each time we get to a new workout'
            if (! workoutDate.equals(prevDate)){
                exercisePRModel prInWorkout = sameWorkout.stream().max(Comparator.comparing(exercisePRModel::getOneRM)).get();
                prEachWorkout.add(prInWorkout);
                sameWorkout.clear();
                sameWorkout.add(prList.get(i));
                prevDate = prList.get(i).getDate();
            }

            else{
                sameWorkout.add(prList.get(i));
                prevDate = prList.get(i).getDate();
            }
        }

        //include the last workout in the set
        prEachWorkout.add(sameWorkout.stream().max(Comparator.comparing(exercisePRModel::getOneRM)).get());

        XYChart.Series<String,Number> series = new XYChart.Series();

        prEachWorkout.forEach((workout)-> {
            XYChart.Data node = new XYChart.Data(workout.getDate().toString(), workout.getOneRM());
            series.getData().add(node);
        });
        series.setName("click on a point!");

        oneRMChart.getData().addAll(series);
        oneRMChart.getXAxis().setTickLabelsVisible(false);
        oneRMChart.getXAxis().setOpacity(0);

        series.getData().forEach((pr)->{
            pr.getNode().setOnMouseClicked((MouseEvent event) -> {
                series.setName(pr.getYValue() + " kg (" + pr.getXValue() + ")");

            });

        });


    }






    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDate.setValue(LocalDate.now().minus(Period.ofDays(365)));
        toDate.setValue(LocalDate.now());

        loadPersonalRecordsFromDB();



        loadExercises();

        col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        col_time.setCellValueFactory(new PropertyValueFactory<>("time"));
        col_kilos.setCellValueFactory(new PropertyValueFactory<>("kilos"));
        col_reps.setCellValueFactory(new PropertyValueFactory<>("reps"));
        col_1RM.setCellValueFactory(new PropertyValueFactory<>("oneRM"));

        col_date.setOnEditStart(event -> {
            enterExistingWorkout(event.getRowValue().getWorkoutID());});
        col_time.setOnEditStart(event -> {

            enterExistingWorkout(event.getRowValue().getWorkoutID());});
        col_kilos.setOnEditStart(event -> {

            enterExistingWorkout(event.getRowValue().getWorkoutID());});
        col_reps.setOnEditStart(event -> {

            enterExistingWorkout(event.getRowValue().getWorkoutID());});
        col_1RM.setOnEditStart(event -> {

            enterExistingWorkout(event.getRowValue().getWorkoutID());});


        PRTableView.setItems(PRObservableList);

        selectedWorkoutPane.setVisible(false);
        oneRMChart.setVisible(false);



        //-----------------------SelectedWorkout------------------------
        fillTimeComboBox();


        SpinnerValueFactory<Integer> shapeValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 5);
        SpinnerValueFactory<Integer> performanceValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 5);
        SpinnerValueFactory<Integer> durationValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 480, 60, 5);
        shapeSpinner.setValueFactory(shapeValueFactory);
        performanceSpinner.setValueFactory(performanceValueFactory);
        durationSpinner.setValueFactory(durationValueFactory);

        datePicker.setValue(LocalDate.now());
        datePicker.setVisible(false);
        timeComboBox.setVisible(false);
        durationSpinner.setVisible(false);
        shapeSpinner.setVisible(false);
        performanceSpinner.setVisible(false);
        noteTextArea.setVisible(false);
        saveButton.setVisible(false);
        discardButton.setVisible(false);



        SpinnerValueFactory<Integer> weightValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 50, 5);
        SpinnerValueFactory<Integer> repsValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 5,1);
        weightSpinner.setValueFactory(weightValueFactory);
        repsSpinner.setValueFactory(repsValueFactory);

        exerciseSelectionBox.setVisible(false); //I chose that the user is not able to add new exercises to workout when entering from PR view, was some strange bug where you couldn't select the lists..
        addEquipmentSetsBox.setVisible(false);
        addRegularSetsBox.setVisible(false);
        selectedEquipmentExerciseLabel.setText("");
        selectedRegularExerciseLabel.setText("");

        selectedExercisesBox.setVisible(true);
        exerciseName.setVisible(false);
        oneRMLabel.setVisible(false);
        oneRMDateLabel.setVisible(false);







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
