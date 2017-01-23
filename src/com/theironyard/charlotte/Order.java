package com.theironyard.charlotte;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.theironyard.charlotte.Main.getConnection;

public class Order {
    private Integer id;
    private Integer userId;
    private boolean open;
    private List<Item> items;

    public Order() {}

    public Order(Integer id, Integer userId, boolean open) {
        this.id = id;
        this.userId = userId;
        this.open = open;
    }

    public Order(Integer userId, boolean open) {
        this.userId = userId;
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Order(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
