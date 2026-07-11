package com.possystem.sajilopos.service;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.CategoryDAO;
import com.possystem.sajilopos.model.Category;

import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final UserService userService = new UserService();
    private final SessionManager session = SessionManager.getInstance();

    public List<Category> getAllCategories() {
        int companyId = session.getCurrentCompanyId();
        return categoryDAO.getCategoriesByCompany(companyId);
    }

    public String addCategory(String name) {
        userService.requireManagerOrAbove();
        if (name == null || name.trim().isEmpty()) return "Category name cannot be empty.";
        int companyId = session.getCurrentCompanyId();
        boolean success = categoryDAO.addCategory(new Category(companyId, name.trim()));
        return success ? "Category added successfully." : "Failed to add category.";
    }

    public String updateCategory(int id, String newName) {
        userService.requireManagerOrAbove();
        if (newName == null || newName.trim().isEmpty()) return "Category name cannot be empty.";
        int companyId = session.getCurrentCompanyId();
        boolean success = categoryDAO.updateCategory(new Category(id, companyId, newName.trim()));
        return success ? "Category updated successfully." : "Failed to update category.";
    }

    public String deleteCategory(int id) {
        userService.requireAdmin();
        int companyId = session.getCurrentCompanyId();
        boolean success = categoryDAO.deleteCategory(id, companyId);
        return success ? "Category deleted successfully." : "Failed to delete category.";
    }
}
