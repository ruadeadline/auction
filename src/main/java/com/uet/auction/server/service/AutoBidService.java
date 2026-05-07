package com.uet.auction.server.service;

import com.uet.auction.server.DAO.AutoBidDAO;
import com.uet.auction.server.DAO.BidDAO;
import com.uet.auction.server.DAO.ProductDAO;
import com.uet.auction.server.model.AutoBidConfig;
import com.uet.auction.common.DTO.ProductDTO;
import com.uet.auction.common.Response.AuctionResponse;
import com.uet.auction.server.network.SocketServer;

import java.math.BigDecimal;
import java.util.List;

public class AutoBidService {
    private AutoBidDAO autoBidDAO = new AutoBidDAO();
    private BidDAO bidDAO = new BidDAO();
    private ProductDAO productDAO = new ProductDAO();

    public void triggerAutoBids(int productId, String currentWinner, double currentPrice) {
        List<AutoBidConfig> autoBids = autoBidDAO.getAutoBidsByProduct(productId);
        if (autoBids == null || autoBids.isEmpty()) return;

        boolean bidPlaced = false;

        for (AutoBidConfig config : autoBids) {
            // Không tự đấu giá đè lên chính mình
            if (config.getBidderUsername().equals(currentWinner)) {
                continue;
            }

            BigDecimal currentPriceBD = BigDecimal.valueOf(currentPrice);
            if (config.getMaxBid().compareTo(currentPriceBD) > 0) {
                // Tính giá mới = giá hiện tại + bước giá
                BigDecimal nextBid = currentPriceBD.add(config.getIncrement());
                
                // Nếu giá mới vượt quá max_bid, thì đặt bằng max_bid
                if (nextBid.compareTo(config.getMaxBid()) > 0) {
                    nextBid = config.getMaxBid();
                }

                // Nếu nextBid <= currentPriceBD (xảy ra nếu max_bid == currentPrice), bỏ qua
                if (nextBid.compareTo(currentPriceBD) <= 0) {
                    continue;
                }

                // Thực hiện đặt giá
                BidDAO.BidResult result = bidDAO.placeBid(productId, config.getBidderUsername(), nextBid.doubleValue());
                if (result.success) {
                    bidPlaced = true;
                    // Báo hiệu giá thay đổi
                    SocketServer.broadcast(new AuctionResponse(true, "UPDATE_PRICE", null));
                    
                    if (result.timeExtended) {
                        SocketServer.broadcast(new AuctionResponse(true, "TIME_EXTENDED", "Phiên đấu giá được gia hạn thêm thời gian!"));
                    }

                    // Đệ quy gọi lại để các AutoBid khác phản ứng với giá vừa được tạo
                    triggerAutoBids(productId, config.getBidderUsername(), nextBid.doubleValue());
                    break; // Dừng vòng lặp vì đệ quy đã xử lý tiếp
                }
            }
        }
    }

    public AuctionResponse registerAutoBid(int productId, String username, double maxBid, double increment) {
        boolean success = autoBidDAO.addAutoBid(productId, username, maxBid, increment);
        if (success) {
            // Ngay sau khi đăng ký thành công, kích hoạt thử
            List<ProductDTO> products = productDAO.getProductsByStatus("OPEN");
            for (ProductDTO p : products) {
                if (p.getId() == productId) {
                    triggerAutoBids(productId, p.getOwnerName(), p.getCurrentPrice());
                    break;
                }
            }
            return new AuctionResponse(true, "AUTO_BID_REGISTER", "Đăng ký Auto-bid thành công!");
        }
        return new AuctionResponse(false, "AUTO_BID_REGISTER", "Lỗi khi đăng ký Auto-bid.");
    }
}
