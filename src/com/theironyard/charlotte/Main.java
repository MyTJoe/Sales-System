package com.theironyard.charlotte;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection("jdbc:h2:./main");
    }

    public static void initializeDatabase() throws SQLException {
        Statement stmt = getConnection().createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, email VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS orders (id IDENTITY, user_id INTEGER, open BOOLEAN)");
        stmt.execute("CREATE TABLE IF NOT EXISTS items (id IDENTITY, name VARCHAR, quantity INTEGER, price DOUBLE, order_id INTEGER)");
    }

    public static void insertItem(Item item) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO items VALUES (null, ?, ?, ?, ?)");
        stmt.setString(1, item.getName());
        stmt.setInt(2, item.getQuantity());
        stmt.setDouble(3, item.getPrice());
        stmt.setInt(4, item.getOrderId()); //this is probably wrong
        stmt.execute();

    }

    //allows user to create an order
    public static int createNewOrder(Integer i) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO orders VALUES (NULL, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, i);
        stmt.setBoolean(2, false);
        stmt.executeUpdate();

        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();

        return keys.getInt(1);
    }

    public static Order getLatestCurrentOrder(Integer userId) throws SQLException {
        Order order = null;

        if (userId != null) {

            PreparedStatement stmt = getConnection().prepareStatement("SELECT TOP 1 * FROM orders WHERE user_id = ? AND open = true");

            stmt.setInt(1, userId);
            ResultSet results = stmt.executeQuery();

            if (results.next()) {
                order = new Order(results.getInt("id"), userId, true);
                //order = new Order(results.getInt("id"), results.getInt("user_id"), false);
            }
        }

        return order;
    }

    public static List<Order> getOrdersForUser(Integer userId) throws SQLException {
        ArrayList order = null;
        PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM orders WHERE user_id = ?");

        stmt.setInt(1, userId);
        ResultSet results = stmt.executeQuery();

        if (results.next()){
            order = new ArrayList<Order>(results.getInt("user_id"));
        }
        return order;
    }

    public static User getUserById(Integer id) throws SQLException {
        User user = null;

        if (id != null) {
            PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM users WHERE id = ?");

            stmt.setInt(1, id);
            ResultSet results = stmt.executeQuery();

            if (results.next()) {
                user = new User(id, results.getString("username"), results.getString("email"));

                user.setOrders(getOrdersForUser(id));
            }
        }

        return user;
    }

    public static Integer getUserIdByEmail(String email) throws SQLException {
        Integer userId = null;

        if (email != null) {
            PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM users WHERE email = ?");
            stmt.setString(1, email);

            ResultSet results = stmt.executeQuery();

            if(results.next()) {
                userId = results.getInt("id");
            }
        }

        return  userId;
    }

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        initializeDatabase();

        Spark.post("/order", (request, response) -> {
            Session session = request.session();

            User current = getUserById(session.attribute("user_id"));

            if (current != null) {
                //see if there is a current order
                Order currentOrder = getLatestCurrentOrder(current.getId());

                if (currentOrder == null) {
                    //if not make a new one
                    int orderId = createNewOrder(current.getId());

                    //get item from post data
                    Item postedItem = new Item(request.queryParams("name"), Integer.valueOf(request.queryParams("quantity")), Double.valueOf(request.queryParams("price")), orderId);

                    //add item to order
                    insertItem(postedItem);
                }
            }

            //redirect
            response.redirect("/");
            return "";
        });

        Spark.get("/", (request, response) -> {
            HashMap m = new HashMap();
            Session session = request.session();

            User current = getUserById(session.attribute("user_id"));

            if (current != null) {
                // pass user into model
                m.put("user", current);

                return new ModelAndView(m, "home.html");
            } else {
                return new ModelAndView(m, "login.html");
            }
        }, new MustacheTemplateEngine());

        Spark.post("/login", (request, response) -> {

            String email = request.queryParams("email");

            // looks up the user by email address
            Integer userId = getUserIdByEmail(email);

            // if the user exists, saves the id in session.
            if (userId != null) {
                Session session = request.session();
                session.attribute("user_id", userId);
            }
            response.redirect("/");
            return "";
        });


    }
}

//            Spark.externalStaticFileLocation("public");