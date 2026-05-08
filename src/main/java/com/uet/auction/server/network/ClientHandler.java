package com.uet.auction.server.network;

import com.uet.auction.common.Response.AuctionResponse;
import com.uet.auction.server.service.AuctionService;
import com.uet.auction.server.service.AuthService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private AuthService authService = new AuthService();
    private AuctionService auctionService = new AuctionService();

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new com.google.gson.TypeAdapter<LocalDateTime>() {
                @Override
                public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws java.io.IOException {
                    out.value(value == null ? null : value.toString());
                }
                @Override
                public LocalDateTime read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
                    if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    return LocalDateTime.parse(in.nextString());
                }
            })
            .create();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            String line;
            while ((line = in.readLine()) != null) {
                JsonObject request = gson.fromJson(line, JsonObject.class);
                String type = request.get("type").getAsString();
                JsonElement dataElement = request.get("data");
                AuctionResponse response = null;

                switch (type) {
                    case "LOGIN":
                        JsonObject loginData = dataElement.getAsJsonObject();
                        response = authService.login(loginData.get("username").getAsString(), loginData.get("password").getAsString());
                        sendResponse(response);
                        break;
                    case "REGISTER":
                        JsonArray regData = dataElement.getAsJsonArray();
                        response = authService.register(regData.get(0).getAsString(), regData.get(1).getAsString(), regData.get(2).getAsString());
                        sendResponse(response);
                        break;
                    case "ADD_PRODUCT":
                        JsonArray addData = dataElement.getAsJsonArray();
                        Object[] parsedData = new Object[]{
                                addData.get(0).getAsString(), // name
                                addData.get(1).getAsString(), // description
                                addData.get(2).getAsString(), // category
                                addData.get(3).getAsDouble(), // price
                                addData.get(4).getAsString(), // sellerName
                                LocalDateTime.parse(addData.get(5).getAsJsonObject().get("date").getAsString() + "T" + addData.get(5).getAsJsonObject().get("time").getAsJsonObject().get("hour").getAsString() + ":" + addData.get(5).getAsJsonObject().get("time").getAsJsonObject().get("minute").getAsString()), // startTime
                                LocalDateTime.parse(addData.get(6).getAsJsonObject().get("date").getAsString() + "T" + addData.get(6).getAsJsonObject().get("time").getAsJsonObject().get("hour").getAsString() + ":" + addData.get(6).getAsJsonObject().get("time").getAsJsonObject().get("minute").getAsString()) // endTime
                        };
                        response = auctionService.addProduct(parsedData);
                        sendResponse(response);
                        break;
                    case "GET_PENDING_PRODUCTS":
                        response = auctionService.getProductsByStatus("PENDING");
                        sendResponse(response);
                        break;
                    case "GET_OPEN_PRODUCTS":
                        response = auctionService.getProductsByStatus("OPEN");
                        sendResponse(response);
                        break;
                    case "CHANGE_PRODUCT_STATUS":
                        JsonArray statusData = dataElement.getAsJsonArray();
                        response = auctionService.changeProductStatus(statusData.get(0).getAsInt(), statusData.get(1).getAsString());
                        sendResponse(response);
                        break;
                    case "SUBSCRIBE_AUCTION":
                        int subProductId = dataElement.getAsInt();
                        SocketServer.registerObserver(subProductId, this);
                        sendResponse(new AuctionResponse(true, "SUBSCRIBE_SUCCESS", "Subscribed to auction " + subProductId));
                        break;
                    case "UNSUBSCRIBE_AUCTION":
                        int unsubProductId = dataElement.getAsInt();
                        SocketServer.removeObserver(unsubProductId, this);
                        sendResponse(new AuctionResponse(true, "UNSUBSCRIBE_SUCCESS", "Unsubscribed from auction " + unsubProductId));
                        break;
                    case "PLACE_BID":
                        JsonArray bidData = dataElement.getAsJsonArray();
                        int productId = bidData.get(0).getAsInt();
                        response = auctionService.placeBid(productId, bidData.get(1).getAsString(), bidData.get(2).getAsDouble());
                        sendResponse(response);
                        if (response.isSuccess()) {
                            // Observer Pattern: Only notify clients watching this specific product
                            SocketServer.notifyObservers(productId, new AuctionResponse(true, "UPDATE_PRICE", null));
                            // Optional: also broadcast to refresh global lists
                            SocketServer.broadcast(new AuctionResponse(true, "UPDATE_PRICE", null));
                        }
                        break;
                    default:
                        sendResponse(new AuctionResponse(false, "ERROR", "Unknown request"));
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Client disconnected.");
            SocketServer.removeClient(this);
        }
    }

    public void sendResponse(AuctionResponse response) {
        if (out != null) {
            String json = gson.toJson(response);
            out.println(json);
        }
    }
}