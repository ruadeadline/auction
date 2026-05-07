package com.uet.auction.client.controller;

import com.uet.auction.common.Request.AuctionRequest;
import com.uet.auction.client.network.SocketClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class BidHistoryController {
    @FXML
    private LineChart<Number, Number> priceChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Label lblProductName;
    @FXML
    private Label lblCurrentPrice;

    private XYChart.Series<Number, Number> series;
    private int productId;
    private int timeStep = 0;

    public static BidHistoryController instance;

    @FXML
    public void initialize() {
        instance = this;
        series = new XYChart.Series<>();
        series.setName("Lịch sử giá");
        priceChart.getData().add(series);
        
        xAxis.setLabel("Thời gian (giây)");
        yAxis.setLabel("Giá đấu (VNĐ)");
        
        // Hide animation to avoid lag on fast updates
        priceChart.setAnimated(false);
    }

    public void setProductData(int productId, String name, double currentPrice) {
        this.productId = productId;
        lblProductName.setText(name);
        lblCurrentPrice.setText("Giá hiện tại: " + currentPrice);
        
        // Subscribe to real-time updates for this product
        SocketClient.sendRequest(new AuctionRequest("SUBSCRIBE_AUCTION", productId));
        
        // Add initial point
        addPricePoint(currentPrice);
    }

    public void addPricePoint(double price) {
        Platform.runLater(() -> {
            series.getData().add(new XYChart.Data<>(timeStep, price));
            lblCurrentPrice.setText("Giá hiện tại: " + price);
            timeStep++;
        });
    }

    // Call this when closing the window
    public void onClose() {
        SocketClient.sendRequest(new AuctionRequest("UNSUBSCRIBE_AUCTION", productId));
    }
}
