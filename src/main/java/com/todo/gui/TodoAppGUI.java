package com.todo.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.todo.dao.TodoAPPDAO;
import com.todo.model.Todo;
import javax.swing.event.ListSelectionEvent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.List;

public class TodoAppGUI extends JFrame{
    private TodoAPPDAO todoDAO;
    private JTable todoTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JCheckBox completCheckBox;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton refreshButton;
    private JComboBox<String> filterComboBox;

    public TodoAppGUI(){
        this.todoDAO = new TodoAPPDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadTodos();
    }

    private void initializeComponents(){
        setTitle("Todo Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,600);
        setLocationRelativeTo(null);

        String[] columnNames = {"ID","Title","Description","Completed","Created At","Updated At"};
        tableModel = new DefaultTableModel(columnNames,0){
            @Override
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };

        todoTable = new JTable(tableModel);
        todoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        todoTable.getSelectionModel().addListSelectionListener(
            (e)->{
                if(!e.getValueIsAdjusting()){
                    loadSelectedtodo();
                }
            }
        );

        titleField = new JTextField(20);
        descriptionArea = new JTextArea(3,20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        completCheckBox = new JCheckBox("Completed");

        addButton = new JButton("Add Todo");
        updateButton = new JButton("Update Todo");
        deleteButton = new JButton("Delete Todo");
        refreshButton = new JButton("Refresh Todo");

        String[] filteroptions = {"All","Completed","Pending"};
        filterComboBox = new JComboBox<>(filteroptions);
        filterComboBox.addActionListener((e) -> {
            filterTodos();
        });
    }
    private void setupLayout(){
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Title"),gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField,gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel("Description"),gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JScrollPane(descriptionArea),gbc);

        // gbc.gridx = 1;
        gbc.gridx = 2;
        inputPanel.add(completCheckBox,gbc);

        JPanel buttoPanel = new JPanel(new FlowLayout());
        buttoPanel.add(addButton);
        buttoPanel.add(updateButton);
        buttoPanel.add(deleteButton);
        buttoPanel.add(refreshButton);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterComboBox);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(inputPanel,BorderLayout.CENTER);
        northPanel.add(buttoPanel,BorderLayout.SOUTH);
        northPanel.add(filterPanel,BorderLayout.NORTH);

        add(northPanel,BorderLayout.NORTH);

        add(new JScrollPane(todoTable),BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Select a todo item to update or delete."));
        add(statusPanel,BorderLayout.SOUTH);
    }


    private void setupEventListeners(){
        addButton.addActionListener(e->{
            addTodos();
        });
        updateButton.addActionListener(e->{
            updateTodos();
        });
        deleteButton.addActionListener(e->{
            deleteTodos();
        });
        refreshButton.addActionListener(e->{
            refreshTodos();
        });
    }
    
    private void addTodos(){
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        boolean completed = completCheckBox.isSelected();

        if (title.isEmpty()){
            JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try{
            Todo todo = new Todo(title,description);
            todo.setCompleted(completed);
            int newId = todoDAO.createtodo(todo);
            todo.setId(newId);

            loadTodos();
            titleField.setText("");
            descriptionArea.setText("");
            completCheckBox.setSelected(false);
            JOptionPane.showMessageDialog(this, "Todo added successfully with ID: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error adding todo: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedtodo(){
        int selectedRow = todoTable.getSelectedRow();
        if(selectedRow >= 0){
            String title = (String)tableModel.getValueAt(selectedRow, 1);
            String description = (String)tableModel.getValueAt(selectedRow, 2);
            boolean completed = (boolean)tableModel.getValueAt(selectedRow, 3);

            titleField.setText(title);
            descriptionArea.setText(description);   
            completCheckBox.setSelected(completed);
        }
    }

    private void updateTodos(){
        tableModel.getRowCount();
        int selectedRow = todoTable.getSelectedRow();
        if(selectedRow < 0){
            JOptionPane.showMessageDialog(this, "Please select a todo to update.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String title = titleField.getText().trim();
        if(title.isEmpty()){
            JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int)todoTable.getValueAt(selectedRow, 0);
        try{
            Todo todo = todoDAO.getTodoById(id);
            if(todo != null){
                todo.setTitle(title);
                todo.setDescription(descriptionArea.getText().trim());
                todo.setCompleted(completCheckBox.isSelected());
                todoDAO.updatetodo(todo);
                JOptionPane.showMessageDialog(this, "Todo updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTodos();
            }
            else{
                JOptionPane.showMessageDialog(this, "Failed to update Todo", "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error updating todo: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTodos(){
        int selectedRows = todoTable.getSelectedRow();
        if(selectedRows < 0){
            JOptionPane.showMessageDialog(this, "Please select a todo to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int)todoTable.getValueAt(selectedRows, 0);
        try{
            boolean deleted = todoDAO.deleteTodo(id);
            if(deleted){
                JOptionPane.showMessageDialog(this,"Todo deleted successfully.","Success",JOptionPane.INFORMATION_MESSAGE);
                titleField.setText("");
                descriptionArea.setText("");
                completCheckBox.setSelected(false);
                loadTodos();
            }
            else{
                JOptionPane.showMessageDialog(this, "Failed to delete Todo", "Delete Error", JOptionPane.ERROR_MESSAGE);
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error deleting todo: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTodos(){
        todoTable.clearSelection();
        titleField.setText("");
        descriptionArea.setText("");
        completCheckBox.setSelected(false);
        loadTodos();
        JOptionPane.showMessageDialog(this, "Todo list refreshed.", "Refreshed", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadTodos(){
        try {
            List<Todo> todos = todoDAO.getAllTodos();
            filterComboBox.setSelectedIndex(0);
            updateTable(todos);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading todos: " + e.getMessage(), "Dtabase Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTodos(){
        String filter = (String)filterComboBox.getSelectedItem();
        try{
            List<Todo> todos;
            if(filter.equals("Completed")){
                todos = todoDAO.getCompletedTodos();
            }
            else if(filter.equals("Pending")){
                todos = todoDAO.getNotCompletedTodos();
            }
            else{
                todos = todoDAO.getAllTodos();
            }
            titleField.setText("");
            descriptionArea.setText("");
            completCheckBox.setSelected(false);
            updateTable(todos);
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error filtering todos: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Todo> todos){
        tableModel.setRowCount(0);
        for(Todo todo: todos){
            Object[] row = {
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getCreated_at(),
                todo.getUpdated_at()
            };
            tableModel.addRow(row);

        }
    }

}