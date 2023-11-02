package com.mycompany.todolist;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
 
    private final IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private final StringProperty description = new SimpleStringProperty(this, "description");
    private final BooleanProperty completed = new SimpleBooleanProperty(this, "completed");

    public Task(int id, String description, boolean completed) {
        this.id.set(id);
        this.description.set(description);
        this.completed.set(completed);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public BooleanProperty completedProperty() {
        return completed;
    }
    
    public int getId() {
        return id.get();
    }
    
    public void setId(int id) {
        this.id.set(id);
    }
    
    public String getDescription() {
        return description.get();
    }
    
    public void setDescription(String description) {
        this.description.set(description);
    }
    
    public boolean isCompleted() {
        return completed.get();
    }
    
    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }
}
