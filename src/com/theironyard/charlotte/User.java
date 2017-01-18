package com.theironyard.charlotte;

import java.util.List;


public class User {

    private Integer id;
    private String name;
    private String email;
    private List<Order> orders;

    public User() {
    }

    public User(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
        //id in user table prob matches user_id in orders

//    public static User getUserByEmail(Connection conn, String email) throws SQLException {
//       User Email;
//       PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
//       stmt.setString(1, email);
//       stmt.execute();
//       ResultSet results = stmt.executeQuery();
//
//       if (results.next()) {
//           //Integer id = results.getInt("id");
//           String name = results.getString("username");
//           //String email = results.getString("email");
//           Email = new User(name, email);
//       }
//
//       return Email;
//}

//  orders belong to users and items belong to orders
// }

