package com.uet.auction.client.network;

import com.uet.auction.common.Request.AuctionRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class SocketClient {
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static final Gson gson = new GsonBuilder()
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

    public static void connect() {
        try {
            socket = new Socket("localhost", 8080);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            new Thread(new ResponseListener(in)).start();
            System.out.println("Đã kết nối với Server!");
        } catch (Exception e) {
            System.err.println("Không thể kết nối Server!");
        }
    }

    public static void sendRequest(AuctionRequest request) {
        if (out != null) {
            String json = gson.toJson(request);
            out.println(json);
        }
    }
}