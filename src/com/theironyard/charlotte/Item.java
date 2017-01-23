package com.theironyard.charlotte;

public class Item {
    private Integer id;
    private String name;
    private Integer quantity;
    private double price;
    private Integer orderId;

    public Item() {}

    public Item(String name, Integer quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public Item(String name, Integer quantity, double price, Integer orderId) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.orderId = orderId;
    }

    public Item(Integer id, String name, Integer quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }


//
//    public static ArrayList<Item> createItem(Connection conn) throws SQLException {
//        ArrayList<Item> item = new ArrayList<>();
//        Statement stmt = conn.createStatement();
//        ResultSet results = stmt.executeQuery("SELECT * FROM items");
//        while (results.next()) {
//            Integer id = results.getInt("id");
//            String name = results.getString("name");
//            Integer quantity = results.getInt("quantity");
//            double price = results.getDouble("price");
//            Integer orderId = results.getInt("order_id");
//            item.add(new Item(id, name, quantity, price, orderId));
//        }
//        return item;
//    }
}
