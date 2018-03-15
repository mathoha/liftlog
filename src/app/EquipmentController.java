package app;

import app.models.equipmentModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EquipmentController implements Initializable{

    @FXML private Button addEquipmentButton;

    @FXML private
    TableView<equipmentModel> table;

    @FXML private
    TableColumn<equipmentModel,String> col_name;

    @FXML private
    TableColumn<equipmentModel,String> col_description;

    ObservableList<equipmentModel> observableList = FXCollections.observableArrayList();



    public void addEquipment() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("addEquipment.fxml"));
        Dialog dialog = new Dialog();
        dialog.getDialogPane().setContent(root);
        dialog.show();

        //to enable closing out dialog
        Window window = dialog.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

        table.setItems(observableList);

    }
}
