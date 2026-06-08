package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM categories";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("category_id"),
                    rs.getString("category_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public boolean addCategory(Category category) {
        String sql = "INSERT INTO categories (category_name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET category_name = ? WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setInt(2, category.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
