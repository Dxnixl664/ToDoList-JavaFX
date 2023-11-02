package com.mycompany.todolist;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ToDoListGUI extends Application {
    
    private TextField taskInputField;
    private TableView<Task> taskTableView;
    private Label statusLabel;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        
        HBox topArea = new HBox(10);
        topArea.setPadding(new Insets(10));
        taskInputField = new TextField();
        taskInputField.setPromptText("Enter task here");
        
        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        
        addButton.setOnAction(event -> addTask());
        updateButton.setOnAction(event -> updateTask());
        deleteButton.setOnAction(event -> deleteTask());
        
        topArea.getChildren().addAll(taskInputField, addButton, updateButton, deleteButton);
        root.setTop(topArea);
        
        taskTableView = new TableView<>();
        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Task");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        TableColumn<Task, Boolean> completedColumn = new TableColumn<>("Completed");
        // Checkbox for completed status (to be added)
        taskTableView.getColumns().addAll(descriptionColumn, completedColumn);
        root.setCenter(taskTableView);
        
        statusLabel = new Label("Ready");
        root.setBottom(statusLabel);
        
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("To-Do List Application");
        primaryStage.setScene(scene);
        primaryStage.show();
        loadTasks();
    }
    
    private void addTask() {
        String description = taskInputField.getText();
        if (!description.trim().isEmpty()) {
            try {
                ToDoListDB db = new ToDoListDB();
                db.addTask(description);
                taskInputField.clear();
                loadTasks();
                statusLabel.setText("Added task succesfully");
            } catch (SQLException e) {
                statusLabel.setText("Error: Unable to add task");
            }
        } else {
            statusLabel.setText("Error: Task description cannot be empty.");
        }
    }
    
    private void updateTask() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            String newDescription = taskInputField.getText();
            if (!newDescription.trim().isEmpty()) {
                try {
                    selectedTask.setDescription(newDescription);
                    ToDoListDB db = new ToDoListDB();
                    db.updateTask(selectedTask);
                    taskInputField.clear();
                    loadTasks();
                    statusLabel.setText("Updated task succesfully.");
                } catch (SQLException e) {
                    statusLabel.setText("Error: Could not update task.");
                }
            } else {
                statusLabel.setText("Error: Task description cannot be empty.");
            }
        } else {
            statusLabel.setText("Error: No task selected.");
        }
    }
    
    private void deleteTask() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            try {
                ToDoListDB db = new ToDoListDB();
                db.deleteTask(selectedTask);
                loadTasks();
                statusLabel.setText("Task deleted succesfully");
            } catch (SQLException e) {
                statusLabel.setText("Error: Could not delete task");
            }
        } else {
            statusLabel.setText("Error: No task selected.");
        }
    }
    
    private void loadTasks() {
        try {
            ToDoListDB db = new ToDoListDB();
            ObservableList<Task> tasks = db.getTasks();
            taskTableView.setItems(tasks);
        } catch (SQLException e) {
            statusLabel.setText("Error: Could not load tasks.");
        }
    }
}