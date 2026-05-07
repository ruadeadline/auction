package com.uet.auction.server.service;

import com.uet.auction.server.DAO.BidDAO;
import com.uet.auction.common.Response.AuctionResponse;
import com.uet.auction.server.network.SocketServer;

public class BidService {
    private BidDAO bidDAO = new BidDAO();
    private AutoBidService autoBidService = new AutoBidService();

    public AuctionResponse processBid(int productId, String user, double amount) {
        BidDAO.BidResult result = bidDAO.placeBid(productId, user, amount);
        if (result.success) {
            AuctionResponse res = new AuctionResponse(true, "BID_SUCCESS", result.message);
            
            // Sau khi đặt giá thành công, báo cho tất cả mọi người cập nhật UI
            SocketServer.broadcast(new AuctionResponse(true, "UPDATE_PRICE", null));
            
            if (result.timeExtended) {
                // Nếu thời gian được gia hạn (Anti-sniping), gửi thông báo cập nhật
                SocketServer.broadcast(new AuctionResponse(true, "TIME_EXTENDED", "Phiên đấu giá được gia hạn thêm thời gian!"));
            }

            // Kích hoạt Auto-Bidding cho những người dùng khác
            autoBidService.triggerAutoBids(productId, user, amount);

            return res;
        }
        return new AuctionResponse(false, "BID_ERROR", result.message);
    }
}
