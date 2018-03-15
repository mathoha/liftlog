package app;


import app.models.equipmentModel;
import app.models.workoutModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class WorkoutsController implements Initializable{

    @FXML private TableView workoutsTableView;

    @FXML private TableColumn<workoutModel,Date> col_date;
    @FXML private TableColumn<workoutModel,Time> col_time;
    @FXML private TableColumn<workoutModel,Integer> col_duration;
    @FXML private TableColumn<workoutModel,Integer> col_shape;
    @FXML private TableColumn<workoutModel,Integer> col_performance;
    @FXML private TableColumn<workoutModel,String> col_note;

    ObservableList<workoutModel> workoutsObservableList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Connection con = DBConnector.getConnection();

            ResultSet rs = con.createStatement().executeQuery("select * from Workout");
            while (rs.next()){
                workoutsObservableList.add(new workoutModel(rs.getInt("workoutID"), rs.getDate("workout_date"),rs.getTime("workout_time"),rs.getInt("duration"),rs.getInt("shape"),rs.getInt("performance"),rs.getString("note")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        col_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        col_time.setCellValueFactory(new PropertyValueFactory<>("time"));
        col_duration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        col_shape.setCellValueFactory(new PropertyValueFactory<>("shape"));
        col_performance.setCellValueFactory(new PropertyValueFactory<>("performance"));
        col_note.setCellValueFactory(new PropertyValueFactory<>("note"));

        workoutsTableView.setItems(workoutsObservableList);
    }
}
