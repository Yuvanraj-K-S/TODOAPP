package com.todo;
import java.sql.Connection;
import com.todo.util.DatabaseConnection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection db_Connection = new DatabaseConnection();
        try{
            Connection cn = db_Connection.getDBConnection();
            System.out.println("Database Connection Successful");
        }
        catch(SQLException e){
            System.out.println("Database Connection Failed: " + e.getMessage());
        }
    }
}