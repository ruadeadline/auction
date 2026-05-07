package com.uet.auction.server;

import com.uet.auction.server.network.SocketServer;
import com.uet.auction.server.service.AuctionTimer;
import com.uet.auction.server.util.Logger;

public class ServerApplication {
    public static void main(String[] args) {
        int port = 8080;

        // 1. Khởi động bộ đếm giờ đấu giá (Chạy ngầm để đóng/mở phiên)
        AuctionTimer auctionTimer = new AuctionTimer();
        auctionTimer.startChecking();
        Logger.info("Auction Timer đang chạy ngầm...");

        // 2. Khởi động Socket Server để nhận kết nối từ các máy Client
        SocketServer server = new SocketServer();
        Logger.info("Auction Server đang khởi động tại cổng " + port + "...");

        // Gọi hàm start và truyền cổng vào đây
        server.start(port);
    }
}