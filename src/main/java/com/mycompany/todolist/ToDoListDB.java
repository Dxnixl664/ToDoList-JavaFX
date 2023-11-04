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
        statement.execute("CREATE TABLE IF NOT EXISTS tasks (id INTEGER PRIMARY KEY, completed BOOLEAN, description TEXT, details TEXT)");
        statement.close();
        connection.close();
    }
    
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:tasks.db");
    }
    
    public void addTask(String description, String details) throws SQLException {
        try (Connection connection = connect()) {
            String sql = "INSERT INTO tasks (completed, description, details) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setBoolean(1, false);
                preparedStatement.setString(2, description);
                preparedStatement.setString(3, details);
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
                    boolean completed = resultSet.getBoolean("completed");
                    String description = resultSet.getString("description");
                    String details = resultSet.getString("details");
                    tasks.add(new Task(id, completed, description, details));
                }
            }
        }
        return tasks;
    }
    
    public void updateTask(Task task) throws SQLException {
        try (Connection connection = connect()) {
            String sql = "UPDATE tasks SET completed = ?, description = ?, details = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setBoolean(1, task.isCompleted());
                preparedStatement.setString(2, task.getDescription());
                preparedStatement.setString(3, task.getDetails());
                preparedStatement.setInt(4,task.getId());
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