package com.uet.auction.common.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ProductDTO implements Serializable {
    // ID phiên bản để tránh lỗi khi gửi qua mạng (Socket)
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private double currentPrice;
    private String sellerName;
    private String ownerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    // Constructor rỗng (Bắt buộc phải có khi làm việc với DTO)
    public ProductDTO() {
    }

    // Constructor đầy đủ
    public ProductDTO(int id, String name, double currentPrice, String sellerName, String ownerName, LocalDateTime startTime, LocalDateTime endTime, String status) {
        this.id = id;
        this.name = name;
        this.currentPrice = currentPrice;
        this.sellerName = sellerName;
        this.ownerName = ownerName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // --- GETTERS & SETTERS ---
    // (Lưu ý: Tên hàm get/set phải chuẩn xác để TableView của Admin đọc được dữ liệu)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}