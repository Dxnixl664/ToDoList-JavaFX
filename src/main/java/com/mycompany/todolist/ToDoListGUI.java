package com.mycompany.todolist;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ToDoListGUI extends Application {
    
    private TableView<Task> taskTableView;
    private Label statusLabel;
    private Scene mainScene;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        ToDoListDB db = new ToDoListDB();
        try {
            db.init(); // Initialize DB
        } catch (SQLException e) {
            System.out.println("Error initializing the database.");
            e.printStackTrace();
            return; // Exits if db couldnt be initialized
        }
        
        BorderPane root = new BorderPane();
        
        HBox topArea = new HBox(10);
        topArea.setPadding(new Insets(10));
        
        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");
        
        addButton.setOnAction(event -> showAddScene(primaryStage));
        updateButton.setOnAction(event -> showUpdateScene(primaryStage));
        deleteButton.setOnAction(event -> deleteTask());
        
        topArea.getChildren().addAll(addButton, updateButton, deleteButton);
        root.setTop(topArea);
        
        taskTableView = new TableView<>();
        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Task");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        
        TableColumn<Task, Boolean> completedColumn = new TableColumn<>("Status");
        completedColumn.setCellValueFactory(cellData -> cellData.getValue().completedProperty());
        completedColumn.setCellFactory(column -> new CheckBoxTableCell<Task, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    TableRow<Task> currentRow = getTableRow();
                    CheckBox checkBox = (CheckBox) getGraphic();
                    checkBox.setOnAction(e -> {
                        Task task = currentRow.getItem();
                        if (task != null) {
                            task.setCompleted(checkBox.isSelected());
                            updateTaskInDb(task);
                        }
                    });
                }
            }
        });
        
        completedColumn.setEditable(true);
        taskTableView.setEditable(true);
        
        completedColumn.setOnEditCommit(event -> {
            Task task = event.getRowValue();
            boolean completedStatus = event.getNewValue();
            task.setCompleted(completedStatus);
            updateTaskInDb(task);
        });
        
        TableColumn<Task, String> detailsColumn = new TableColumn<>("Description");
        detailsColumn.setCellValueFactory(cellData -> cellData.getValue().detailsProperty());
        
        taskTableView.getColumns().addAll(completedColumn, descriptionColumn, detailsColumn);
        root.setCenter(taskTableView);
        
        statusLabel = new Label("Ready");
        root.setBottom(statusLabel);
        
        mainScene = new Scene(root, 300, 250);
        primaryStage.setTitle("To-Do List Application");
        primaryStage.setScene(mainScene);
        primaryStage.show();
        loadTasks();
    }
    
    private void addTask(String description, String details) {
        if (!description.trim().isEmpty()) {
            try {
                ToDoListDB db = new ToDoListDB();
                db.addTask(description, details);
                loadTasks();
                statusLabel.setText("Added task succesfully");
            } catch (SQLException e) {
                statusLabel.setText("Error: Unable to add task");
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Error: Task description cannot be empty.");
        }
    }
    
/*    private void updateTask() {
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            String newDescription = "temp";
            if (!newDescription.trim().isEmpty()) {
                try {
                    selectedTask.setDescription(newDescription);
                    ToDoListDB db = new ToDoListDB();
                    db.updateTask(selectedTask);
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
    }*/
    
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
    
    private void updateTaskInDb(Task task) {
        try {
            ToDoListDB db = new ToDoListDB();
            db.updateTask(task);
            statusLabel.setText("Task updated succesfully.");
        } catch (SQLException e) {
            statusLabel.setText("Error: Could not update task status.");
        }
    }
    
    private void showAddScene(Stage primaryStage) {
        VBox dialogVBox = new VBox(10);
        TextField taskNameInputField = new TextField();
        taskNameInputField.setPromptText("Enter task");
        TextArea detailsInputField = new TextArea();
        detailsInputField.setPromptText("Enter task details");
        
        Button okButton = new Button("Ok");
        Button cancelButton = new Button("Cancel");
        
        okButton.setOnAction(event -> {
            String taskName = taskNameInputField.getText();
            String taskDetails = detailsInputField.getText();
            addTask(taskName, taskDetails);
            primaryStage.setScene(mainScene);
        });
        
        cancelButton.setOnAction(event -> primaryStage.setScene(mainScene));
        
        dialogVBox.getChildren().addAll(taskNameInputField, detailsInputField, okButton, cancelButton);
        
        Scene dialogScene = new Scene(dialogVBox, 300, 200);
        primaryStage.setScene(dialogScene);
    }
    
    private void showUpdateScene(Stage primaryStage) {
        VBox dialogVBox = new VBox(10);
        dialogVBox.setPadding(new Insets(10));
        
        TextField taskNameInputField = new TextField();
        taskNameInputField.setPromptText("Enter task");
        TextArea taskDetailsInputField = new TextArea();
        taskDetailsInputField.setPromptText("Enter task details");

        // Retrieve selected task
        Task selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            taskNameInputField.setText(selectedTask.getDescription());
            taskDetailsInputField.setText(selectedTask.getDetails());
        }
        
        Button okButton = new Button("Update");
        Button cancelButton = new Button("Cancel");
        
        okButton.setOnAction(event -> {
            if (selectedTask != null) {
                selectedTask.setDescription(taskNameInputField.getText());
                selectedTask.setDetails(taskDetailsInputField.getText());
                updateTaskInDb(selectedTask);
            }
            primaryStage.setScene(mainScene);
        });
        
        cancelButton.setOnAction(event -> primaryStage.setScene(mainScene));
        
        dialogVBox.getChildren().addAll(taskNameInputField, taskDetailsInputField, okButton, cancelButton);
        
        Scene dialogScene = new Scene(dialogVBox);
        primaryStage.setScene(dialogScene);
    }
}