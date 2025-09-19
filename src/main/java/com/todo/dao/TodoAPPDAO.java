package com.todo.dao;

import com.todo.model.Todo;
import com.todo.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.ResultSet;

public class TodoAPPDAO {

    private static final String SELECT_ALL_TODOS = "SELECT * FROM todos ORDER BY created_at DESC";
    private static final String INSERT_TODO = "INSERT INTO todos (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";

    public int createtodo(Todo todo) throws SQLException {
        try(Connection conn = DatabaseConnection.getDBConnection();
        PreparedStatement stmt = conn.prepareStatement(INSERT_TODO, PreparedStatement.RETURN_GENERATED_KEYS);){
            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());
            stmt.setTimestamp(4, Timestamp.valueOf(todo.getCreated_at()));
            stmt.setTimestamp(5, Timestamp.valueOf(todo.getUpdated_at()));
            int affectedRows = stmt.executeUpdate();
            if(affectedRows == 0){
                throw new SQLException("Creating todo failed, no rows affected.");
            }
            try(ResultSet generatedKeys = stmt.getGeneratedKeys();){
                if(generatedKeys.next()){
                    return generatedKeys.getInt(1);
                }
                else{
                    throw new SQLException("Creating todo failed, no ID obtained.");
                }
            }
        }
    }

    private Todo getTodoRow(ResultSet r) throws SQLException {
        int id = r.getInt("id");
        String title = r.getString("title");
        String description = r.getString("description");
        boolean completed = r.getBoolean("completed");
        java.time.LocalDateTime created_at = r.getTimestamp("created_at").toLocalDateTime();
        java.time.LocalDateTime updated_at = r.getTimestamp("updated_at").toLocalDateTime();
        Todo todo = new Todo(id, title, description, completed, created_at, updated_at);
        return todo;
    }

    public List<Todo> getAllTodos() throws SQLException {
        // Implementation here
        List<Todo> todos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getDBConnection()) {
            PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_TODOS);
            ResultSet res = stmt.executeQuery();

            while (res.next()) {
                todos.add(getTodoRow(res));
            }
        }
        return todos;
    }
}
