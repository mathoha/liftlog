package app;

import app.models.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ExerciseGroupsController implements Initializable{

    private ObservableList<exerciseGroupModel> exerciseGroupObservableList = FXCollections.observableArrayList();
    private ObservableList<exerciseInGroupModel> exerciseInGroupObservableList = FXCollections.observableArrayList();
    private ObservableList<exerciseModel> exerciseObservableList = FXCollections.observableArrayList();

    @FXML ListView<exerciseGroupModel> exerciseGroupList = new ListView<>(exerciseGroupObservableList);
    @FXML ListView<exerciseInGroupModel> exerciseInGroupList = new ListView<>(exerciseInGroupObservableList);


    @FXML Label exerciseGroupNameLabel;
    @FXML Button addExerciseGroupButton;
    @FXML TextField exerciseGroupNameInput;

    @FXML ComboBox<exerciseModel> exerciseComboBox = new ComboBox<>();
    @FXML Button addExerciseToGroupButton;



    public void addNewExerciseGroup(){
        if (! exerciseGroupNameInput.getText().isEmpty()){
            try {
                Connection con = DBConnector.getConnection();
                PreparedStatement stm = con.prepareStatement("INSERT INTO Exercise_Group (name) VALUES(?)");
                stm.setString(1, exerciseGroupNameInput.getText());
                stm.execute();
                stm.close();


            } catch (SQLException e) {
                e.printStackTrace();
            }

            System.out.println(exerciseGroupNameInput.getText() + " added to exercises groups");
            exerciseGroupNameInput.clear();
            loadExerciseGroupsFromDB();
        }

    }

    public void addExerciseToGroup(){
        try {
            Connection con = DBConnector.getConnection();

            if (!exerciseComboBox.getSelectionModel().isEmpty()) {
                PreparedStatement stm = con.prepareStatement("INSERT INTO Exercise_In_Group (exerciseID, groupID) VALUES(?,?)");
                stm.setInt(1, exerciseComboBox.getSelectionModel().getSelectedItem().getExerciseID());
                stm.setInt(2, exerciseGroupList.getSelectionModel().getSelectedItem().getExerciseGroupID());
                stm.execute();
                stm.close();
                con.close();
                System.out.println("Inserted exercise with ID: " + exerciseComboBox.getSelectionModel().getSelectedItem().getExerciseID() + " into Exercise group " + exerciseGroupList.getSelectionModel().getSelectedItem().getExerciseGroupID());
            }

        }

        catch (SQLException e) {
            e.printStackTrace();
        }

        loadExercisesInGroupFromDB(exerciseGroupList.getSelectionModel().getSelectedItem().getExerciseGroupID());
        loadExercisesNotInGroup(exerciseGroupList.getSelectionModel().getSelectedItem().getExerciseGroupID());

    }

    public void exerciseGroupSelected(){
        exerciseGroupModel selectedExerciseGroup = exerciseGroupList.getSelectionModel().getSelectedItem();
        loadExercisesInGroupFromDB(selectedExerciseGroup.getExerciseGroupID());
        loadExercisesNotInGroup(selectedExerciseGroup.getExerciseGroupID());
        exerciseInGroupList.setVisible(true);
        exerciseGroupNameLabel.setVisible(true);
        exerciseGroupNameLabel.setText(selectedExerciseGroup.getExerciseGroupName() + " Exercises");
        addExerciseToGroupButton.setVisible(true);
        exerciseComboBox.setVisible(true);
    }


    public void loadExerciseGroupsFromDB() {
        try {
            exerciseGroupList.getItems().clear();
            Connection con = DBConnector.getConnection();

            PreparedStatement q = con.prepareStatement("SELECT * FROM Exercise_Group");

            ResultSet rs = q.executeQuery();
            while (rs.next()){
                exerciseGroupObservableList.add(new exerciseGroupModel(rs.getInt("groupID"), rs.getString("name")));
            }
            q.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        exerciseGroupList.setCellFactory(param -> new ListCell<exerciseGroupModel>() {
            @Override
            protected void updateItem(exerciseGroupModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getClass() == null) {
                    setText(null);
                } else {
                    setText(item.getExerciseGroupName());
                }
            }
        });

        exerciseGroupList.setItems(exerciseGroupObservableList);
    }

    public void loadExercisesInGroupFromDB(int groupID) {
        try {
            exerciseInGroupList.getItems().clear();
            Connection con = DBConnector.getConnection();

            PreparedStatement q = con.prepareStatement("SELECT Exercise.ExerciseID, Exercise.name, t1.groupID, t1.name FROM Exercise Inner Join(Select * from Exercise_Group natural Join Exercise_In_Group) as t1 On Exercise.exerciseID = t1.exerciseID WHERE t1.groupID=(?)");
            q.setInt(1,groupID);

            ResultSet rs = q.executeQuery();
            while (rs.next()){
                exerciseInGroupObservableList.add(new exerciseInGroupModel(rs.getInt("Exercise.exerciseID"), rs.getInt("t1.groupID"), rs.getString("Exercise.name"), rs.getString("t1.name")));
            }
            q.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        exerciseInGroupList.setCellFactory(param -> new ListCell<exerciseInGroupModel>() {
            @Override
            protected void updateItem(exerciseInGroupModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getClass() == null) {
                    setText(null);
                } else {
                    setText(item.getExerciseName());
                }
            }
        });

        exerciseInGroupList.setItems(exerciseInGroupObservableList);
    }


    public void loadExercisesNotInGroup(int groupID){
        try {
            exerciseComboBox.getItems().clear();
            Connection con = DBConnector.getConnection();

            PreparedStatement q = con.prepareStatement("SELECT exerciseID, name, type FROM Exercise WHERE exerciseID NOT IN (SELECT exerciseID FROM Exercise NATURAL JOIN Exercise_In_Group WHERE groupID =(?))");
            q.setInt(1,groupID);

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



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadExerciseGroupsFromDB();

        exerciseGroupNameLabel.setVisible(false);
        addExerciseToGroupButton.setVisible(false);
        exerciseComboBox.setVisible(false);







    }
}
