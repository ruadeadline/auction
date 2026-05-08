package com.uet.auction.server.service;

import com.uet.auction.common.DTO.ProductDTO;
import com.uet.auction.common.Response.AuctionResponse;
import com.uet.auction.server.DAO.ProductDAO;
import com.uet.auction.server.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;

public class AuctionService {

    private ProductDAO productDAO = new ProductDAO();

    public AuctionResponse getProductsByStatus(String status) {
        List<ProductDTO> list = productDAO.getProductsByStatus(status);
        return new AuctionResponse(true, "GET_PRODUCTS_RESULT", list);
    }

    public AuctionResponse changeProductStatus(int productId, String newStatus) {
        boolean success = productDAO.updateProductStatus(productId, newStatus);
        if (success) {
            return new AuctionResponse(true, "CHANGE_STATUS_RESULT", "Cập nhật thành công!");
        }
        return new AuctionResponse(false, "CHANGE_STATUS_RESULT", "Cập nhật thất bại!");
    }

    public AuctionResponse addProduct(Object[] data) {
        try {
            String name = (String) data[0];
            String description = (String) data[1];
            String categoryStr = (String) data[2];
            double price = (Double) data[3];
            String sellerName = (String) data[4];
            LocalDateTime startTime = (LocalDateTime) data[5];
            LocalDateTime endTime = (LocalDateTime) data[6];

            com.uet.auction.server.model.ItemCategory category = com.uet.auction.server.model.ItemCategory.valueOf(categoryStr);
            
            // Factory Pattern & Polymorphism requirement
            com.uet.auction.server.model.Item item = com.uet.auction.server.model.ItemFactory.createItem(
                category, name, description, java.math.BigDecimal.valueOf(price), sellerName
            );
            item.printInfo(); // Demonstrates Polymorphism

            boolean success = productDAO.addProduct(name, description, categoryStr, price, sellerName, startTime, endTime);
            if (success) {
                return new AuctionResponse(true, "ADD_PRODUCT_RESULT", "Gửi yêu cầu đăng bán thành công, chờ Admin duyệt!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AuctionResponse(false, "ADD_PRODUCT_RESULT", "Lỗi server khi đăng sản phẩm.");
    }

    public AuctionResponse placeBid(int productId, String bidderName, double bidAmount) {
        String selectSql = "SELECT current_price, status FROM products WHERE id = ?";
        String updateSql = "UPDATE products SET current_price = ?, owner_name = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            
            selectStmt.setInt(1, productId);
            try (java.sql.ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    double currentPrice = rs.getDouble("current_price");
                    String status = rs.getString("status");

                    if (!"OPEN".equals(status)) {
                        throw new com.uet.auction.common.exception.AuctionClosedException("Phiên đấu giá đã đóng hoặc chưa mở!");
                    }
                    if (bidAmount <= currentPrice) {
                        throw new com.uet.auction.common.exception.InvalidBidException("Giá đặt phải lớn hơn giá hiện tại!");
                    }

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, bidAmount);
                        updateStmt.setString(2, bidderName);
                        updateStmt.setInt(3, productId);
                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            return new AuctionResponse(true, "BID_RESULT", "Đặt giá thành công!");
                        }
                    }
                } else {
                    return new AuctionResponse(false, "BID_RESULT", "Không tìm thấy sản phẩm!");
                }
            }
        } catch (com.uet.auction.common.exception.AuctionClosedException | com.uet.auction.common.exception.InvalidBidException ex) {
            return new AuctionResponse(false, "BID_RESULT", ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new AuctionResponse(false, "BID_RESULT", "Lỗi dữ liệu, lỗi kết nối Server.");
        }
        return new AuctionResponse(false, "BID_RESULT", "Đặt giá thất bại!");
    }
}