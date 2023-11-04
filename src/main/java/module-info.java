module com.mycompany.todolist {
    requires javafx.controls;
    requires java.sql;
    requires javafx.fxml;
    
    opens com.mycompany.todolist to javafx.fxml;
    exports com.mycompany.todolist;
}
