package com.todo.dao;

import com.todo.model.Todo;
import com.todo.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.ResultSet;

public class TodoAPPDAO {

    private static final String SELECT_ALL_TODOS = "SELECT * FROM todos ORDER BY created_at DESC";
    private static final String INSERT_TODO = "INSERT INTO todos (title, description, completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_TODO_BY_ID = "SELECT * FROM todos WHERE id = ?";
    private static final String UPDATE_TODO = "UPDATE todos SET title = ?, description = ?, completed = ?, updated_at = ? WHERE id = ?";

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

    public Todo getTodoById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_TODO_BY_ID);) {
            stmt.setInt(1, id);
            try (ResultSet res = stmt.executeQuery();) {
                if (res.next()) {
                    return getTodoRow(res);
                }
            }
        }
        return null;
    }

    public boolean updatetodo(Todo todo) throws SQLException {
        try(Connection conn = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = conn.prepareStatement(UPDATE_TODO);
        ){
            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(5, todo.getId());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
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
