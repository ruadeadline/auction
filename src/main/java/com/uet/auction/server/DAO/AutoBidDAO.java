package com.uet.auction.server.DAO;

import com.uet.auction.server.config.DatabaseConnection;
import com.uet.auction.server.model.AutoBidConfig;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AutoBidDAO {

    public boolean addAutoBid(int productId, String username, double maxBid, double increment) {
        String sql = "INSERT INTO auto_bids (product_id, username, max_bid, increment) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE max_bid = ?, increment = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.setString(2, username);
            pstmt.setDouble(3, maxBid);
            pstmt.setDouble(4, increment);
            pstmt.setDouble(5, maxBid);
            pstmt.setDouble(6, increment);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AutoBidConfig> getAutoBidsByProduct(int productId) {
        List<AutoBidConfig> configs = new ArrayList<>();
        String sql = "SELECT * FROM auto_bids WHERE product_id = ? ORDER BY created_at ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                AutoBidConfig config = new AutoBidConfig();
                config.setAuctionId(rs.getInt("product_id"));
                config.setBidderUsername(rs.getString("username"));
                config.setMaxBid(BigDecimal.valueOf(rs.getDouble("max_bid")));
                config.setIncrement(BigDecimal.valueOf(rs.getDouble("increment")));
                config.setRegisteredAt(rs.getTimestamp("created_at").toLocalDateTime());
                configs.add(config);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return configs;
    }
}
