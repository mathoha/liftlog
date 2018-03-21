package app;

import app.models.equipmentModel;
import app.models.exerciseGroupModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EquipmentController implements Initializable{

    @FXML private Button addEquipmentButton;

    @FXML private
    TableView<equipmentModel> table;

    @FXML private
    TableColumn<equipmentModel,String> col_name;

    @FXML private
    TableColumn<equipmentModel,String> col_description;

    ObservableList<equipmentModel> observableList = FXCollections.observableArrayList();


    @FXML VBox equipmentBox;
    @FXML Label equipmentName;
    @FXML Label equipmentDescription;



    public void addEquipmentButtonPressed() throws IOException {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add Equipment");

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


        gridPane.add(new Label("Name:"), 1, 0);
        gridPane.add(name, 1, 1);
        gridPane.add(new Label("Description:"), 1, 2);
        gridPane.add(description, 1, 3);
        dialog.getDialogPane().setContent(gridPane);

        // Request focus on the name field by default.
        Platform.runLater(() -> name.requestFocus());

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
            System.out.println("name: " + pair.getKey() + " Description: " + pair.getValue());


            try {
                Connection con = DBConnector.getConnection();
                PreparedStatement stm = con.prepareStatement("INSERT INTO Equipment (name, description) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
                stm.setString(1, pair.getKey());
                stm.setString(2, pair.getValue());
                stm.execute();

                System.out.println("Inserted: " + name.getText() + " : " + description.getText());

                stm.close();
                con.close();
                equipmentBox.setVisible(true);
                equipmentName.setText(name.getText());
                equipmentDescription.setText(description.getText());
                loadEquipmentFromDB();



            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void displayEquipment(equipmentModel selectedEquipment){
        System.out.println(selectedEquipment.getName() + " "+ selectedEquipment.getDescription());
        equipmentName.setText(selectedEquipment.getName());
        equipmentDescription.setText(selectedEquipment.getDescription());
        equipmentBox.setVisible(true);

    }

    public void loadEquipmentFromDB(){
        observableList.clear();
        try {
            Connection con = DBConnector.getConnection();

            ResultSet rs = con.createStatement().executeQuery("select equipmentID, name, description from Equipment");
            while (rs.next()){
                observableList.add(new equipmentModel(rs.getString("equipmentID"),rs.getString("name"),rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_description.setCellValueFactory(new PropertyValueFactory<>("description"));


        col_name.setOnEditStart(event -> {
            equipmentModel selectedEquipment = event.getRowValue();
            displayEquipment(selectedEquipment);});

        col_description.setOnEditStart(event -> {
            equipmentModel selectedEquipment = event.getRowValue();
            displayEquipment(selectedEquipment);});

        table.setItems(observableList);
        table.refresh();

    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadEquipmentFromDB();
        equipmentBox.setVisible(false);


    }
}
