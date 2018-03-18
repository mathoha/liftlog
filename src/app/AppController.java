package app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable{

    @FXML private Button newWorkout;
    @FXML private Button workouts;
    @FXML private Button exercises;
    @FXML private Button equipment;
    @FXML private Label currentSceneLabel;


    @FXML public BorderPane borderPane;


    public void goToNewWorkout(ActionEvent event) throws IOException {
        Parent newWorkoutView = FXMLLoader.load(getClass().getResource("newWorkout.fxml"));
        borderPane.setCenter(newWorkoutView);
        currentSceneLabel.setText("New workout");
    }

    public void goToWorkouts(ActionEvent event) throws IOException {
        Parent workoutsView = FXMLLoader.load(getClass().getResource("workouts.fxml"));
        borderPane.setCenter(workoutsView);
        currentSceneLabel.setText("Workout log");

    }

    public void goToExistingWorkout() throws IOException {
        Parent existingWorkoutView = FXMLLoader.load(getClass().getResource("existingWorkout.fxml"));
        borderPane.setCenter(existingWorkoutView);
        currentSceneLabel.setText("Edit Workout");

    }

    public void goToExercises(ActionEvent event) throws IOException {
        Parent exercisesView = FXMLLoader.load(getClass().getResource("exercises.fxml"));
        borderPane.setCenter(exercisesView);
        currentSceneLabel.setText("Exercises");

    }

    public void goToExerciseGroups(ActionEvent event) throws IOException {
        Parent exerciseGroupView = FXMLLoader.load(getClass().getResource("exerciseGroups.fxml"));
        borderPane.setCenter(exerciseGroupView);
        currentSceneLabel.setText("Exercise Groups");

    }


    public void goToEquipment(ActionEvent event) throws IOException {
        Parent equipmentView = FXMLLoader.load(getClass().getResource("equipment.fxml"));
        borderPane.setCenter(equipmentView);
        currentSceneLabel.setText("Equipment");

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentSceneLabel.setText("");


    }
}
