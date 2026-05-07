package com.uet.auction.server.network;

import com.uet.auction.common.Response.AuctionResponse;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketServer {
    // Lưu danh sách tất cả người dùng đang online
    private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    
    // Observer Pattern: Map productId -> danh sách Client đang xem
    private static Map<Integer, List<ClientHandler>> observers = new ConcurrentHashMap<>();

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Đăng ký nhận thông báo cho 1 phiên đấu giá cụ thể
    public static void registerObserver(int productId, ClientHandler client) {
        observers.computeIfAbsent(productId, k -> new CopyOnWriteArrayList<>()).add(client);
    }

    // Hủy đăng ký nhận thông báo
    public static void removeObserver(int productId, ClientHandler client) {
        List<ClientHandler> list = observers.get(productId);
        if (list != null) {
            list.remove(client);
        }
    }

    // Gửi thông báo cho những ai đang xem phiên đấu giá productId
    public static void notifyObservers(int productId, AuctionResponse response) {
        List<ClientHandler> list = observers.get(productId);
        if (list != null) {
            for (ClientHandler client : list) {
                client.sendResponse(response);
            }
        }
    }

    // Hàm gửi thông báo cho tất cả Client (dùng cho cập nhật danh sách chung)
    public static void broadcast(AuctionResponse response) {
        for (ClientHandler client : clients) {
            client.sendResponse(response);
        }
    }

    // Xóa client khi họ thoát
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
        // Xóa khỏi tất cả các observers
        for (List<ClientHandler> list : observers.values()) {
            list.remove(client);
        }
    }
}