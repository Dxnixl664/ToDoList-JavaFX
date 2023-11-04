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
    private final StringProperty details = new SimpleStringProperty(this, "details");

    public Task(int id, boolean completed, String description, String details) {
        this.id.set(id);
        this.completed.set(completed);
        this.description.set(description);
        this.details.set(details);
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
    
    public StringProperty detailsProperty() {
        return details;
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
    
    public String getDetails() {
        return details.get();
    }
    
    public void setDetails(String details) {
        this.details.set(details);
    }
    
    public boolean isCompleted() {
        return completed.get();
    }
    
    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }
}
