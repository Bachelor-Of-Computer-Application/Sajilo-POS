package com.possystem.sajilopos.service;

import com.possystem.sajilopos.dao.CategoryDAO;
import com.possystem.sajilopos.model.Category;

import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

    public String addCategory(String name) {
        if (name == null || name.trim().isEmpty()) return "Category name cannot be empty.";
        boolean success = categoryDAO.addCategory(new Category(0, name.trim()));
        return success ? "Category added successfully." : "Failed to add category.";
    }

    public String updateCategory(int id, String newName) {
        if (newName == null || newName.trim().isEmpty()) return "Category name cannot be empty.";
        boolean success = categoryDAO.updateCategory(new Category(id, newName.trim()));
        return success ? "Category updated successfully." : "Failed to update category.";
    }

    public String deleteCategory(int id) {
        boolean success = categoryDAO.deleteCategory(id);
        return success ? "Category deleted successfully." : "Failed to delete category.";
    }
}
