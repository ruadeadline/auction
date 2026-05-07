package com.uet.auction.client.network;

import com.uet.auction.client.controller.AdminController;
import com.uet.auction.client.controller.RegisterController;
import com.uet.auction.common.Response.AuctionResponse;
import com.uet.auction.client.controller.LoginController;
import com.uet.auction.client.controller.UserController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;

public class ResponseListener implements Runnable {
    private BufferedReader in;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new com.google.gson.TypeAdapter<LocalDateTime>() {
                @Override
                public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws IOException {
                    out.value(value == null ? null : value.toString());
                }
                @Override
                public LocalDateTime read(com.google.gson.stream.JsonReader in) throws IOException {
                    if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    return LocalDateTime.parse(in.nextString());
                }
            })
            .create();

    public ResponseListener(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                AuctionResponse res = gson.fromJson(line, AuctionResponse.class);
                String type = res.getType();

                switch (type) {
                    case "LOGIN_RESULT":
                        if (LoginController.instance != null) {
                            LoginController.instance.handleLoginResponse(res.isSuccess(), res.getMessage());
                        }
                        break;
                    case "REGISTER_RESULT":
                        if (RegisterController.instance != null) {
                            RegisterController.instance.handleRegisterResponse(res.isSuccess(), res.getMessage());
                        }
                        break;
                    case "ADD_PRODUCT_RESULT":
                        if (com.uet.auction.client.controller.SellerController.instance != null) {
                            com.uet.auction.client.controller.SellerController.instance.handleSellerResponse(res.isSuccess(), res.getMessage());
                        }
                        break;
                    case "CHANGE_STATUS_RESULT":
                        if (AdminController.instance != null) {
                            AdminController.instance.handleAdminResponse(type, res.isSuccess(), res.getMessage());
                        }
                        break;
                    case "GET_PRODUCTS_RESULT":
                        if (res.getData() != null) {
                            try {
                                String jsonList = gson.toJson(res.getData());
                                java.util.List<com.uet.auction.common.DTO.ProductDTO> list = gson.fromJson(jsonList, new com.google.gson.reflect.TypeToken<java.util.List<com.uet.auction.common.DTO.ProductDTO>>(){}.getType());
                                
                                if (AdminController.instance != null) {
                                    AdminController.instance.updatePendingList(list);
                                }
                                if (UserController.instance != null) {
                                    UserController.instance.displayProducts(list);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case "UPDATE_PRICE":
                        Platform.runLater(() -> {
                            if (UserController.instance != null) {
                                UserController.instance.loadProducts();
                            }
                        });
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Mất kết nối với Server.");
        }
    }
}