package com.uet.auction.client.controller;

import com.uet.auction.client.network.SocketClient;
import com.uet.auction.client.util.AlertHelper;
import com.uet.auction.client.util.SceneManager;
import com.uet.auction.common.Request.AuctionRequest;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class SellerController {
    @FXML private javafx.scene.control.TableView<?> tableSellerProducts;

    @FXML
    public void handleAddNewProduct() {
        javafx.scene.control.Dialog<Object[]> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Đăng sản phẩm mới");
        dialog.setHeaderText("Vui lòng nhập thông tin sản phẩm");

        javafx.scene.control.DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(javafx.scene.control.ButtonType.OK, javafx.scene.control.ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Tên sản phẩm");
        TextField descField = new TextField();
        descField.setPromptText("Mô tả");
        javafx.scene.control.ComboBox<String> categoryBox = new javafx.scene.control.ComboBox<>();
        categoryBox.getItems().addAll("ELECTRONICS", "ART", "VEHICLE", "GENERAL");
        categoryBox.setValue("GENERAL");
        TextField priceField = new TextField();
        priceField.setPromptText("Giá khởi điểm");
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        grid.add(new javafx.scene.control.Label("Tên sản phẩm:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new javafx.scene.control.Label("Mô tả:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new javafx.scene.control.Label("Danh mục:"), 0, 2);
        grid.add(categoryBox, 1, 2);
        grid.add(new javafx.scene.control.Label("Giá khởi điểm:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new javafx.scene.control.Label("Ngày bắt đầu:"), 0, 4);
        grid.add(startDatePicker, 1, 4);
        grid.add(new javafx.scene.control.Label("Ngày kết thúc:"), 0, 5);
        grid.add(endDatePicker, 1, 5);

        dialogPane.setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == javafx.scene.control.ButtonType.OK) {
                try {
                    String name = nameField.getText();
                    String desc = descField.getText();
                    String category = categoryBox.getValue();
                    double price = Double.parseDouble(priceField.getText());
                    LocalDateTime startTime = startDatePicker.getValue().atStartOfDay();
                    LocalDateTime endTime = endDatePicker.getValue().atTime(LocalTime.MAX);

                    if (startTime.isAfter(endTime)) {
                        AlertHelper.showError("Thời gian kết thúc phải sau thời gian bắt đầu!");
                        return null;
                    }

                    return new Object[]{name, desc, category, price, currentSeller, startTime, endTime};
                } catch (Exception e) {
                    AlertHelper.showError("Vui lòng điền đủ và đúng định dạng thông tin!");
                    return null;
                }
            }
            return null;
        });

        java.util.Optional<Object[]> result = dialog.showAndWait();
        result.ifPresent(itemData -> {
            SocketClient.sendRequest(new AuctionRequest("ADD_PRODUCT", itemData));
        });
    }

    public static SellerController instance;
    private String currentSeller = "seller_test"; // Ideally this comes from UserSession

    @FXML
    public void initialize() {
        instance = this;
    }

    public void handleSellerResponse(boolean success, String message) {
        javafx.application.Platform.runLater(() -> {
            if (success) {
                AlertHelper.showInfo(message);
            } else {
                AlertHelper.showError(message);
            }
        });
    }

    @FXML
    public void onLogoutClick() {
        if (tableSellerProducts != null) {
            Stage stage = (Stage) tableSellerProducts.getScene().getWindow();
            SceneManager.switchScene(stage, "/com/uet/auction/view/Login.fxml");
        }
    }
}