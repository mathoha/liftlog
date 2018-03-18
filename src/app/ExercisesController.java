package app;

import app.models.equipmentExerciseModel;
import app.models.equipmentModel;
import app.models.exerciseGroupModel;
import app.models.regularExerciseModel;
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

public class ExercisesController implements Initializable{

    @FXML
    Button addRegularExerciseButton;
    @FXML
    Button addEquipmentExerciseButton;

    @FXML
    TableView regularExerciseTable;
    @FXML
    TableView equipmentExerciseTable;

    @FXML
    TableColumn<regularExerciseModel,String> col_regularExerciseName;
    @FXML
    TableColumn<regularExerciseModel,String> col_regularExerciseDescription;
    @FXML
    TableColumn<equipmentExerciseModel,String>  col_equipmentExerciseName;
    @FXML
    TableColumn<equipmentExerciseModel,String>  col_equipmentExerciseEquipment;

    private ObservableList<regularExerciseModel> regularExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<equipmentExerciseModel> equipmentExerciseObservableList = FXCollections.observableArrayList();
    private ObservableList<equipmentModel> equipmentObservableList = FXCollections.observableArrayList();
    private ObservableList<exerciseGroupModel> exerciseGroupObservableList = FXCollections.observableArrayList();


    public void addRegularExerciseButtonPressed() throws IOException {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add new Exercise");

        // Set the button types.
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 10, 10));

        TextField name = new TextField();
        TextArea description = new TextArea();
        description.setWrapText(true);
        description.setMaxWidth(250);

        ComboBox<exerciseGroupModel> exerciseGroupComboBox = new ComboBox<>();
        exerciseGroupComboBox.setItems(exerciseGroupObservableList);

        //need override toString method to list the name of the equipment in the dropdown while getting the exerciseID as the value.
        StringConverter<exerciseGroupModel> groupConverter = new StringConverter<exerciseGroupModel>() {
            @Override
            public String toString(exerciseGroupModel object) {
                return object.getExerciseGroupName();
            }

            @Override
            public exerciseGroupModel fromString(String groupID) {
                return exerciseGroupObservableList.stream()
                        .filter(item -> item.getExerciseGroupName().equals(groupID))
                        .collect(Collectors.toList()).get(0);
            }
        };

        exerciseGroupComboBox.setConverter(groupConverter);



        gridPane.add(new Label("Name:"), 1, 0);
        gridPane.add(name, 1, 1);
        gridPane.add(new Label("Description:"), 1, 2);
        gridPane.add(description, 1, 3);
        gridPane.add(new Label("Group:"),1, 4);
        gridPane.add(exerciseGroupComboBox,1, 5);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the username field by default.
        Platform.runLater(() -> name.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                if(! name.getText().isEmpty() && ! description.getText().isEmpty()){
                    return new Pair<>(name.getText(), description.getText());
                }
                return null;
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            System.out.println("From=" + pair.getKey() + ", To=" + pair.getValue());

            try {
                Connection con = DBConnector.getConnection();
                PreparedStatement stm = con.prepareStatement("INSERT INTO Exercise (name, type) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
                stm.setString(1, pair.getKey());
                stm.setString(2, "regular");
                stm.execute();

                ResultSet rs = stm.getGeneratedKeys();
                rs.next();
                int exerciseID = rs.getInt(1);
                stm.close();
                System.out.println("Inserted: " + pair.getKey() + " into Exercise table");

                PreparedStatement stm2 = con.prepareStatement("INSERT INTO Regular_Exercise (exerciseID, description) VALUES(?,?)");
                stm2.setInt(1, exerciseID);
                stm2.setString(2, pair.getValue());
                stm2.execute();
                stm2.close();

                System.out.println("Inserted: " + pair.getKey() + " into Regular_Exercise table" + " with description: " + pair.getValue());

                //insert into exerciseInGroup is group is selected:
                if (! exerciseGroupComboBox.getSelectionModel().isEmpty()){
                    PreparedStatement stm3 = con.prepareStatement("INSERT INTO Exercise_In_Group (exerciseID, groupID) VALUES(?,?)");
                    stm3.setInt(1, exerciseID);
                    stm3.setInt(2, exerciseGroupComboBox.getValue().getExerciseGroupID());
                    stm3.execute();
                    stm3.close();
                    System.out.println("Inserted exercise with ID: " + exerciseID + " into Exercise group " + exerciseGroupComboBox.getValue().getExerciseGroupName());
                }

                con.close();

                refreshRegularExerciseTable();


            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addEquipmentExerciseButtonPressed() throws IOException {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add new Exercise");

        // Set the button types.
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 20, 10, 10));

        TextField name = new TextField();
        ComboBox<equipmentModel> equipmentComboBox = new ComboBox<>();
        equipmentComboBox.setItems(equipmentObservableList);
        ComboBox<exerciseGroupModel> exerciseGroupComboBox = new ComboBox<>();
        exerciseGroupComboBox.setItems(exerciseGroupObservableList);




        //need override toString method to list the name of the equipment in the dropdown while getting the equipmentID as the value.
        StringConverter<equipmentModel> converter = new StringConverter<equipmentModel>() {
            @Override
            public String toString(equipmentModel object) {
               return object.getName();
            }

            @Override
            public equipmentModel fromString(String equipmentID) {
                return equipmentObservableList.stream()
                        .filter(item -> item.getId().equals(equipmentID))
                        .collect(Collectors.toList()).get(0);
            }
        };

        equipmentComboBox.setConverter(converter);

        //need override toString method to list the name of the equipment in the dropdown while getting the exerciseID as the value.
        StringConverter<exerciseGroupModel> groupConverter = new StringConverter<exerciseGroupModel>() {
            @Override
            public String toString(exerciseGroupModel object) {
                return object.getExerciseGroupName();
            }

            @Override
            public exerciseGroupModel fromString(String groupID) {
                return exerciseGroupObservableList.stream()
                        .filter(item -> item.getExerciseGroupName().equals(groupID))
                        .collect(Collectors.toList()).get(0);
            }
        };

        exerciseGroupComboBox.setConverter(groupConverter);


        gridPane.add(new Label("Name:"), 1, 0);
        gridPane.add(name, 1, 1);
        gridPane.add(new Label("Equipment:"), 1, 2);
        gridPane.add(equipmentComboBox, 1, 3);
        gridPane.add(new Label("Group:"), 1, 4);
        gridPane.add(exerciseGroupComboBox, 1, 5);

        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the name field by default.
        Platform.runLater(() -> name.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                if(! name.getText().isEmpty() && ! equipmentComboBox.getSelectionModel().isEmpty()){
                    return new Pair<>(name.getText(), equipmentComboBox.getValue().getId());
                }
                return null;
            }
            return null;
        });



        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {

            try {
                Connection con = DBConnector.getConnection();
                PreparedStatement stm = con.prepareStatement("INSERT INTO Exercise (name, type) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
                stm.setString(1, pair.getKey());
                stm.setString(2, "equipment");
                stm.execute();

                ResultSet rs = stm.getGeneratedKeys();
                rs.next();
                int exerciseID = rs.getInt(1);
                stm.close();
                System.out.println("Inserted: " + pair.getKey() + " into Exercise table");

                PreparedStatement stm2 = con.prepareStatement("INSERT INTO Equipment_Exercise (exerciseID, equipmentID) VALUES(?,?)");
                stm2.setInt(1, exerciseID);
                stm2.setString(2, pair.getValue());
                stm2.execute();
                stm2.close();

                System.out.println("Inserted: " + pair.getKey() + " into Equipment_Exercise table" + " with equipmentID: " + pair.getValue());

                refreshEquipmentExerciseTable();

                //insert into exerciseInGroup is group is selected:
                if (! exerciseGroupComboBox.getSelectionModel().isEmpty()){
                    PreparedStatement stm3 = con.prepareStatement("INSERT INTO Exercise_In_Group (exerciseID, groupID) VALUES(?,?)");
                    stm3.setInt(1, exerciseID);
                    stm3.setInt(2, exerciseGroupComboBox.getValue().getExerciseGroupID());
                    stm3.execute();
                    stm3.close();
                    System.out.println("Inserted exercise with ID: " + exerciseID + " into Exercise group " + exerciseGroupComboBox.getValue().getExerciseGroupName());
                }

                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }



        });
    }


    public void displayRegularExercise(regularExerciseModel model){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(model.getName());
        alert.setContentText(model.getDescription());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); //to get the text to wrap

        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(closeButton, deleteButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == deleteButton){

            Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
            deleteAlert.setTitle("Confirm Deletion");
            deleteAlert.setHeaderText("Delete exercise " + model.getName());
            deleteAlert.setContentText("Are sure you want to delete " + model.getName() +"?");

            Optional<ButtonType> deleteResult = deleteAlert.showAndWait();
            if (deleteResult.get() == ButtonType.OK){
                deleteExercise(model.getExerciseID());
                System.out.println(model.getName() + " deleted");
                refreshRegularExerciseTable();
            } else {
                System.out.println(model.getName() + " not deleted");
            }
        }
        else {
            // ... user chose CANCEL or closed the dialog
        }

    }

    public void displayEquipment(equipmentExerciseModel model){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(model.getEquipment_name());
        alert.setContentText(model.getEquipment_description());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); //to get the text to wrap
        alert.showAndWait();

    }

    public void displayEquipmentExercise(equipmentExerciseModel model){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(model.getExercise_name());
        alert.setContentText("Equipment: " + model.getEquipment_name() +"\n\n" + "(" + model.getEquipment_description() +")");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); //to get the text to wrap


        ButtonType deleteButton = new ButtonType("Delete");
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(closeButton, deleteButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == deleteButton){

            Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
            deleteAlert.setTitle("Confirm Deletion");
            deleteAlert.setHeaderText("Delete exercise " + model.getExercise_name());
            deleteAlert.setContentText("Are sure you want to delete " + model.getExercise_name() +"?");

            Optional<ButtonType> deleteResult = deleteAlert.showAndWait();
            if (deleteResult.get() == ButtonType.OK){
                deleteExercise(model.getExerciseID());
                System.out.println(model.getExercise_name() + " deleted");
                refreshEquipmentExerciseTable();
            } else {
                System.out.println(model.getExercise_name() + " not deleted");
            }
        }
        else {
            // ... user chose CANCEL or closed the dialog
        }


    }

    private void deleteExercise(int exerciseID) {
        try {
            Connection con = DBConnector.getConnection();
            PreparedStatement stm = con.prepareStatement("DELETE FROM Exercise WHERE ExerciseID = ?");
            stm.setInt(1, exerciseID);
            stm.execute();
            stm.close();
            System.out.println("Deleted " + exerciseID);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void refreshRegularExerciseTable(){
        try {
            regularExerciseObservableList.clear();

            Connection con = DBConnector.getConnection();

            ResultSet rs = con.createStatement().executeQuery("select exerciseID, name, description from Exercise natural join Regular_Exercise");
            while (rs.next()){
                regularExerciseObservableList.add(new regularExerciseModel(rs.getInt("exerciseID"),rs.getString("name"),rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        regularExerciseTable.setItems(regularExerciseObservableList);
    }


    public void refreshEquipmentExerciseTable(){
        try {
            equipmentExerciseObservableList.clear();

            Connection con = DBConnector.getConnection();

            ResultSet rs = con.createStatement().executeQuery("select t1.exerciseID, t1.name, Equipment.name, Equipment.description from Equipment join (Select * from Equipment_Exercise natural join Exercise) as t1 where Equipment.equipmentID = t1.equipmentID;");
            while (rs.next()){
                equipmentExerciseObservableList.add(new equipmentExerciseModel(rs.getInt("t1.exerciseID"), rs.getString("t1.name"),rs.getString("Equipment.name"),rs.getString("Equipment.description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        equipmentExerciseTable.setItems(equipmentExerciseObservableList);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            Connection con = DBConnector.getConnection();

            ResultSet rs = con.createStatement().executeQuery("select exerciseID, name, description from Exercise natural join Regular_Exercise");
            while (rs.next()){
                regularExerciseObservableList.add(new regularExerciseModel(rs.getInt("exerciseID"),rs.getString("name"),rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        col_regularExerciseName.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_regularExerciseDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        col_regularExerciseDescription.setOnEditStart(event -> {
            regularExerciseModel model = event.getRowValue();
            displayRegularExercise(model);});

        col_regularExerciseName.setOnEditStart(event -> {
            regularExerciseModel model = event.getRowValue();
            displayRegularExercise(model);});


        regularExerciseTable.setItems(regularExerciseObservableList);

        try {
            Connection con = DBConnector.getConnection();

            ResultSet rs = con.createStatement().executeQuery("select t1.exerciseID, t1.name, Equipment.name, Equipment.description from Equipment join (Select * from Equipment_Exercise natural join Exercise) as t1 where Equipment.equipmentID = t1.equipmentID;");
            while (rs.next()){
                equipmentExerciseObservableList.add(new equipmentExerciseModel(rs.getInt("t1.exerciseID"), rs.getString("t1.name"),rs.getString("Equipment.name"),rs.getString("Equipment.description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        col_equipmentExerciseName.setCellValueFactory(new PropertyValueFactory<>("exercise_name"));
        col_equipmentExerciseEquipment.setCellValueFactory(new PropertyValueFactory<>("equipment_name"));

        col_equipmentExerciseEquipment.setOnEditStart(event -> {
            equipmentExerciseModel model = event.getRowValue();
            displayEquipment(model);});

        col_equipmentExerciseName.setOnEditStart(event -> {
            equipmentExerciseModel model = event.getRowValue();
            displayEquipmentExercise(model);});


        equipmentExerciseTable.setItems(equipmentExerciseObservableList);


        //load equipment list from database
        try {
            Connection con = DBConnector.getConnection();

            ResultSet rs = con.createStatement().executeQuery("select equipmentID, name, description from Equipment");
            while (rs.next()){
                equipmentObservableList.add(new equipmentModel(rs.getString("equipmentID"),rs.getString("name"),rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //load exerciseGroup list from database
        try {
            Connection con = DBConnector.getConnection();

            ResultSet rs = con.createStatement().executeQuery("select groupID, name from Exercise_Group");
            while (rs.next()){
                exerciseGroupObservableList.add(new exerciseGroupModel(rs.getInt("groupID"),rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
