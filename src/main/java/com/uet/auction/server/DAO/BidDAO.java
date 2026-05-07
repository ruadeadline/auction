package com.uet.auction.server.DAO;

import com.uet.auction.server.config.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class BidDAO {
    
    public static class BidResult {
        public boolean success;
        public boolean timeExtended;
        public String message;
        public BidResult(boolean success, boolean timeExtended, String message) {
            this.success = success;
            this.timeExtended = timeExtended;
            this.message = message;
        }
    }

    public synchronized BidResult placeBid(int productId, String username, double bidAmount) {
        // Use FOR UPDATE to lock the row and prevent concurrent modifications
        String checkSql = "SELECT current_price, end_time, status FROM products WHERE id = ? FOR UPDATE";
        String updateSql = "UPDATE products SET current_price = ?, owner_name = ?, end_time = ? WHERE id = ?";
        String insertBidSql = "INSERT INTO bid_transactions (product_id, username, bid_amount) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                LocalDateTime currentEndTime = null;
                boolean timeExtended = false;

                try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                    pstmt.setInt(1, productId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        String status = rs.getString("status");
                        if (!"OPEN".equals(status)) {
                            conn.rollback();
                            return new BidResult(false, false, "Phiên đấu giá đã đóng.");
                        }

                        double currentPrice = rs.getDouble("current_price");
                        if (bidAmount <= currentPrice) {
                            conn.rollback();
                            return new BidResult(false, false, "Giá đặt thấp hơn hoặc bằng giá hiện tại.");
                        }
                        
                        Timestamp endTimeTs = rs.getTimestamp("end_time");
                        if (endTimeTs != null) {
                            currentEndTime = endTimeTs.toLocalDateTime();
                        }
                    } else {
                        conn.rollback();
                        return new BidResult(false, false, "Sản phẩm không tồn tại.");
                    }
                }

                // Anti-sniping logic: if less than 60 seconds remaining, extend by 120 seconds
                LocalDateTime now = LocalDateTime.now();
                if (currentEndTime != null && ChronoUnit.SECONDS.between(now, currentEndTime) <= 60) {
                    currentEndTime = currentEndTime.plusSeconds(120);
                    timeExtended = true;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setDouble(1, bidAmount);
                    pstmt.setString(2, username);
                    pstmt.setTimestamp(3, Timestamp.valueOf(currentEndTime));
                    pstmt.setInt(4, productId);
                    pstmt.executeUpdate();
                }

                try (PreparedStatement pstmt = conn.prepareStatement(insertBidSql)) {
                    pstmt.setInt(1, productId);
                    pstmt.setString(2, username);
                    pstmt.setDouble(3, bidAmount);
                    pstmt.executeUpdate();
                }

                conn.commit();
                return new BidResult(true, timeExtended, "Đặt giá thành công!");
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new BidResult(false, false, "Lỗi Server DB.");
        }
    }
}