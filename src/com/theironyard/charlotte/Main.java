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

    public static void insertItem(Order order, Item item) throws SQLException {
        //insert into items these values
        PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO items VALUES (null, ?, ?, ?, ?)");
        stmt.setString(1, item.getName());
        stmt.setInt(2, item.getQuantity());
        stmt.setDouble(3, item.getPrice());
        stmt.setInt(4, order.getId());
        stmt.executeUpdate();

        // stmt.setInt(4, item.getOrderId()); //this is wrong and what was tripping me up for a day
    }

    public static List<Item> getOrderItems(Order order) throws SQLException {
        List<Item> orderList = null;

        if (order != null) {

            PreparedStatement stmt = Main.getConnection().prepareStatement("SELECT * FROM items WHERE order_id = ?");
            stmt.setInt(1, order.getId()); //set to 1 maybe

            ResultSet results = stmt.executeQuery();

            while (results.next()) {
                if (orderList == null) {
                    orderList = new ArrayList<>();
                }

                orderList.add(
                        new Item(results.getInt("id"),
                                 results.getString("name"),
                                 results.getInt("quantity"),
                                 results.getDouble("price")));
            }
        }
        return orderList;
    }

    //allows user to create an order
    public static void createNewOrder(Order order) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO orders VALUES (NULL, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, order.getUserId());
        stmt.setBoolean(2, order.isOpen());
        stmt.executeUpdate();

        ResultSet results = stmt.getGeneratedKeys();
        if (results.next()) {
            order.setId(results.getInt(1));
        }
    }

    public static Order getLatestCurrentOrder(User user) throws SQLException {
        Order order = null;

        PreparedStatement stmt = getConnection().prepareStatement("SELECT TOP 1 * FROM orders WHERE user_id = ? AND open = true");

        stmt.setInt(1, user.getId());
        ResultSet results = stmt.executeQuery();

        if (results.next()) {
            order = new Order(results.getInt("id"), user.getId(), true);
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

    public static User getUserByEmail(String email) throws SQLException {
        User user = null;

            PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM users WHERE email = ?");
            stmt.setString(1, email);

            ResultSet results = stmt.executeQuery();

            if(results.next()) {
                user = new User(results.getInt("id"), results.getString("name"), results.getString("email"));
            }
        return  user;
    }




    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        initializeDatabase();

        Spark.post("/order", (request, response) -> {
            //this could be wrong
            Session session = request.session();

            User current = getUserById(session.attribute("user_id"));

            if (current != null) {
                //see if there is a current order
                Order currentOrder = getLatestCurrentOrder(current);

                if (currentOrder == null) {
                    //if not make a new one
                    currentOrder = new Order(current.getId(), true);
                    createNewOrder(currentOrder);
                    //add item to order
                    insertItem(currentOrder, new Item(request.queryParams("name"),
                            Integer.valueOf(request.queryParams("quantity")),
                            Double.valueOf(request.queryParams("price"))));
                }
            }
            //redirect
            response.redirect("/");
            return "";
        });

        Spark.post("/checkout", (request, response) -> {
            HashMap m = new HashMap();
            Session session = request.session();





            return new MustacheTemplateEngine();
        });

        Spark.get("/", (request, response) -> {
            HashMap m = new HashMap();
            Session session = request.session();

            //check if the user has a valid user_id
            User current = getUserById(session.attribute("user_id"));

            if (current != null) {
                // pass user into model
                List<Item> items = getOrderItems(getLatestCurrentOrder(current));

                m.put("items", items);
                m.put("user", current);
            }

            return new ModelAndView(m, "home.html");

        }, new MustacheTemplateEngine();

        Spark.get("/login", (request, response) -> {
            return new ModelAndView(new HashMap(), "login.html");

        }, new MustacheTemplateEngine());


        Spark.post("/login", (request, response) -> {
            Session session = request.session();

           User current = getUserByEmail(request.queryParams("email"));

            if (current != null) {
                session.attribute("user_id", current.getId());
            }

            response.redirect("/");
            return "";
        });


    }
}

//            Spark.externalStaticFileLocation("public");