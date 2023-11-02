package com.mycompany.todolist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ToDoListDB {
    
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not fround!");
            e.printStackTrace();
        }
    }
    
    public void init() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:tasks.db");
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS tasks (id INTEGER PRIMARY KEY, description TEXT, completed BOOLEAN)");
        statement.close();
        connection.close();
    }
    
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:tasks.db");
    }
    
    public void addTask(String description) throws SQLException {
        try (Connection connection = connect()) {
            String sql = "INSERT INTO tasks (description, completed) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, description);
                preparedStatement.setBoolean(2, false);
                preparedStatement.executeUpdate();
            }
        }
    }
    
    public ObservableList<Task> getTasks() throws SQLException {
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        try (Connection connection = connect()) {
            String sql = "SELECT * FROM tasks";
            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String description = resultSet.getString("description");
                    boolean completed = resultSet.getBoolean("completed");
                    tasks.add(new Task(id, description, completed));
                }
            }
        }
        return tasks;
    }
    
    public void updateTask(Task task) throws SQLException {
        try (Connection connection = connect()) {
            String sql = "UPDATE tasks SET description = ?, completed = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, task.getDescription());
                preparedStatement.setBoolean(2, task.isCompleted());
                preparedStatement.setInt(3,task.getId());
                preparedStatement.executeUpdate();
            }
        }
    }
    
    public void deleteTask(Task task) throws SQLException {
        try (Connection connection = connect()) {
            String sql = "DELETE FROM tasks WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, task.getId());
                preparedStatement.executeUpdate();
            }
        }
    }
}