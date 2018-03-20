package app;

import app.models.regularExerciseModel;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ResourceBundle;

public class AppController implements Initializable{

    @FXML private Button newWorkout;
    @FXML private Button workouts;
    @FXML private Button exercises;
    @FXML private Button exerciseGroups;
    @FXML private Button equipment;
    @FXML private Button personalRecords;
    @FXML private Label currentSceneLabel;


    @FXML private VBox setDatabaseBox;
    @FXML private Button loadDBButton;
    @FXML private Circle connectionCircle;
    @FXML private Label connectionStatus;

    @FXML private TextField host;
    @FXML private TextField database;
    @FXML private TextField user;
    @FXML private PasswordField password;



    private ObservableList<Button> menuObservableList = FXCollections.observableArrayList();




    @FXML public BorderPane borderPane;


    public void connectToDB() throws IOException {
        DBConnector.setUrl(host.getText());
        DBConnector.setDbName(database.getText());
        DBConnector.setUser(user.getText());
        DBConnector.setPassword(password.getText());
        try {
            DBConnector.getConnection();
            connectionCircle.setFill(Color.rgb(0, 255, 0));
            System.out.println("Connected to: " + DBConnector.getUrl() +"/" + DBConnector.getDbName() + " with user: " + DBConnector.getUser());
            connectionStatus.setText("Connected to: " + DBConnector.getUrl() +"/" + DBConnector.getDbName() + " with user: " + DBConnector.getUser());
            connectionStatus.setTextFill(Color.rgb(51, 204, 51));
            goToWorkouts();

            setDatabaseBox.setVisible(false);
            currentSceneLabel.setText("");
            newWorkout.setVisible(true);
            workouts.setVisible(true);
            exercises.setVisible(true);
            exerciseGroups.setVisible(true);
            equipment.setVisible(true);
            personalRecords.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            connectionCircle.setFill(Color.rgb(255,0,0));

        }

        animateConnectionCircle(200,20);




    }

    public void animateConnectionCircle(long duration, int count){
        FadeTransition ft = new FadeTransition();
        ft.setNode(connectionCircle);
        ft.setDuration(javafx.util.Duration.millis(duration));
        ft.setFromValue(1);
        ft.setToValue(0.3);
        ft.setCycleCount(count);
        ft.setAutoReverse(true);
        ft.play();
    }

    public void logoClicked() {
        currentSceneLabel.setText("Select Database");
        setDatabaseBox.setVisible(true);
        borderPane.setCenter(null);
        animateConnectionCircle(100,4);


    }

    public void showConnectionData() {
        connectionStatus.setVisible(true);

    }

    public void hideConnectionData() {
        connectionStatus.setVisible(false);

    }

    public void disableSelectedMenu(Button selectedMenu){
        menuObservableList.forEach((menu) ->{
            if(menu.equals(selectedMenu)){
                menu.setDisable(true);
            }
            else {
                menu.setDisable(false);
            }
        });

    }


    public void goToNewWorkout(ActionEvent event) throws IOException {
        disableSelectedMenu(newWorkout);
        Parent newWorkoutView = FXMLLoader.load(getClass().getResource("newWorkout.fxml"));
        borderPane.setCenter(newWorkoutView);
        currentSceneLabel.setText("New workout");
        setDatabaseBox.setVisible(false);



    }

    public void goToWorkouts() throws IOException {
        disableSelectedMenu(workouts);
        Parent workoutsView = FXMLLoader.load(getClass().getResource("workouts.fxml"));
        borderPane.setCenter(workoutsView);
        currentSceneLabel.setText("Workout log");
        setDatabaseBox.setVisible(false);



    }

    public void goToPersonalRecords(ActionEvent event) throws IOException {
        disableSelectedMenu(personalRecords);
        Parent personalRecordsView = FXMLLoader.load(getClass().getResource("personalRecords.fxml"));
        borderPane.setCenter(personalRecordsView);
        currentSceneLabel.setText("Personal Records");
        setDatabaseBox.setVisible(false);


    }

    public void goToExercises(ActionEvent event) throws IOException {
        disableSelectedMenu(exercises);
        Parent exercisesView = FXMLLoader.load(getClass().getResource("exercises.fxml"));
        borderPane.setCenter(exercisesView);
        currentSceneLabel.setText("Exercises");
        setDatabaseBox.setVisible(false);


    }

    public void goToExerciseGroups(ActionEvent event) throws IOException {
        disableSelectedMenu(exerciseGroups);
        Parent exerciseGroupView = FXMLLoader.load(getClass().getResource("exerciseGroups.fxml"));
        borderPane.setCenter(exerciseGroupView);
        currentSceneLabel.setText("Exercise Groups");
        setDatabaseBox.setVisible(false);


    }


    public void goToEquipment(ActionEvent event) throws IOException {
        disableSelectedMenu(equipment);
        Parent equipmentView = FXMLLoader.load(getClass().getResource("equipment.fxml"));
        borderPane.setCenter(equipmentView);
        currentSceneLabel.setText("Equipment");
        setDatabaseBox.setVisible(false);


    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentSceneLabel.setText("Select Database");
        newWorkout.setVisible(false);
        workouts.setVisible(false);
        exercises.setVisible(false);
        exerciseGroups.setVisible(false);
        equipment.setVisible(false);
        personalRecords.setVisible(false);

        connectionStatus.setVisible(false);

        menuObservableList.addAll(newWorkout,workouts,exercises,exerciseGroups,equipment,personalRecords);

        host.setText("localhost:3306");
        database.setText("liftlog");
        user.setText("root");
        password.setText("database");


    }
}
