package com.uet.auction.common.DTO;

import java.io.Serializable;

public class UserDTO implements Serializable {
    // serialVersionUID giúp định danh phiên bản lớp khi truyền đối tượng qua Socket
    private static final long serialVersionUID = 2L;

    private int id;
    private String username;
    private String role; // ADMIN, SELLER, USER
    private String message; // Dùng để chứa thông báo lỗi hoặc thành công từ Server

    // Constructor mặc định
    public UserDTO() {
    }

    // Constructor đầy đủ thông tin
    public UserDTO(int id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    // --- GETTERS & SETTERS ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}