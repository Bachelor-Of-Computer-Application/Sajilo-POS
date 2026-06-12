package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.Company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CompanyDAO - Data Access Object for Company operations
 */
public class CompanyDAO {

    /**
     * Get company by company code
     */
    public Company getCompanyByCode(String companyCode) {
        String sql = "SELECT company_id, company_name, company_code, created_at " +
                     "FROM companies WHERE company_code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, companyCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Company(
                        rs.getInt("company_id"),
                        rs.getString("company_name"),
                        rs.getString("company_code"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching company by code: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get company by ID
     */
    public Company getCompanyById(int companyId) {
        String sql = "SELECT company_id, company_name, company_code, created_at " +
                     "FROM companies WHERE company_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Company(
                        rs.getInt("company_id"),
                        rs.getString("company_name"),
                        rs.getString("company_code"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching company by id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Get all companies
     */
    public List<Company> getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT company_id, company_name, company_code, created_at " +
                     "FROM companies ORDER BY company_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                companies.add(new Company(
                        rs.getInt("company_id"),
                        rs.getString("company_name"),
                        rs.getString("company_code"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all companies: " + e.getMessage());
            e.printStackTrace();
        }

        return companies;
    }

    /**
     * Create new company
     */
    public boolean createCompany(Company company) {
        String sql = "INSERT INTO companies (company_name, company_code) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, company.getCompanyName());
            stmt.setString(2, company.getCompanyCode());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating company: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update company
     */
    public boolean updateCompany(Company company) {
        String sql = "UPDATE companies SET company_name = ?, company_code = ? WHERE company_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, company.getCompanyName());
            stmt.setString(2, company.getCompanyCode());
            stmt.setInt(3, company.getCompanyId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating company: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete company
     */
    public boolean deleteCompany(int companyId) {
        String sql = "DELETE FROM companies WHERE company_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting company: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
