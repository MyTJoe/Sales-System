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
    private static Connection getConnection() throws SQLException{
        return DriverManager.getConnection("jdbc:h2:./main");
    }

    private static void initializeDatabase() throws SQLException {
        Statement stmt = getConnection().createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, email VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS orders (id IDENTITY, user_id INTEGER)");
        stmt.execute("CREATE TABLE IF NOT EXISTS items (id IDENTITY, name VARCHAR, quantity INTEGER, price DOUBLE, order_id INTEGER)");
    }
    public static void createOrder(Item i) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement("INSERT INTO  VALUES (NULL, ?, ?, ?)");
        stmt.setString(1, i.getName());
        stmt.setInt(2, i.getQuantity());
        stmt.setDouble(3, i.getPrice());
        stmt.execute();
    }
//      figure this method out somehow he had list not arraylist as an example

//    private static List<Order> getOrdersForUser(Integer userId) throw SQLException {
//        PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM orders WHERE user_id = ?");
//        return new ArrayList<>();
//    }


    private static List<Order> getOrdersForUser(Integer userId) throws SQLException {
        ArrayList<Order> orders = new ArrayList<>();
        PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM orders WHERE user_id = ?");

        stmt.setInt(1, userId);
        stmt.execute();

    return new ArrayList<>();
    }

    private static User getUserById(Integer id) throws SQLException {
        User user = null;

        if (id != null) {
            PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM users WHERE id = ?");

            stmt.setInt(1, id);
            ResultSet results = stmt.executeQuery();

            if (results.next()) {
                user = new User(id, results.getString("name"), results.getString("email"));

                user.setOrders(getOrdersForUser(id));
            }
        }

        return user;
    }

    private static Integer getUserIdByEmail(String email) throws SQLException {
        Integer userId = null;

        if (email != null) {
            PreparedStatement stmt = getConnection().prepareStatement("SELECT * FROM users Where email = ?");
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

        Spark.get("/", (request, response) -> {
            HashMap model = new HashMap();
            Session session = request.session();

            User current = getUserById(session.attribute("fancy_user_id"));

            if (current != null) {
                // pass user into model
                model.put("user", current);


                return new ModelAndView(model, "home.html");
            } else {
                return new ModelAndView(model, "login.html");
            }
        }, new MustacheTemplateEngine());

        Spark.post("/login", (request, response) -> {

            String email = request.queryParams("email");

            // look up the user by email address
            Integer userId = getUserIdByEmail(email);

            // if the user exists, save the id in session.
            if (userId != null) {
                Session session = request.session();
                session.attribute("fancy_user_id", userId);
            }
            response.redirect("/");
            return "";
        });


        initializeDatabase();
    }
}

//            Spark.externalStaticFileLocation("public");