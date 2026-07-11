package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    /** Get all categories for a specific company */
    public List<Category> getCategoriesByCompany(int companyId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, company_id, category_name FROM categories WHERE company_id = ? ORDER BY category_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(new Category(
                    rs.getInt("category_id"),
                    rs.getInt("company_id"),
                    rs.getString("category_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }
        return categories;
    }

    /** Add a new category */
    public boolean addCategory(Category category) {
        String sql = "INSERT INTO categories (company_id, category_name) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, category.getCompanyId());
            stmt.setString(2, category.getName());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
        }
        return false;
    }

    /** Update category name */
    public boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET category_name = ? WHERE category_id = ? AND company_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setInt(2, category.getId());
            stmt.setInt(3, category.getCompanyId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
        }
        return false;
    }

    /** Delete a category (only if no products reference it) */
    public boolean deleteCategory(int categoryId, int companyId) {
        String sql = "DELETE FROM categories WHERE category_id = ? AND company_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            stmt.setInt(2, companyId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
        }
        return false;
    }
}
