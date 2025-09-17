package com.todo;
import java.sql.Connection;

import com.todo.gui.TodoAppGUI;
import com.todo.util.DatabaseConnection;
import java.sql.SQLException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e){
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
        SwingUtilities.invokeLater(
            ()->{
                try {
                new TodoAppGUI().setVisible(true);
                }
                catch (Exception e) {
                    System.err.println("Failed to launch GUI: " + e.getMessage());
                }
        });
    }
}