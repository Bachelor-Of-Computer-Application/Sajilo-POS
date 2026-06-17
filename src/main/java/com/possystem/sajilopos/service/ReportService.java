package com.possystem.sajilopos.service;

import com.possystem.sajilopos.dao.ReportDAO;

import java.time.LocalDate;
import java.util.Map;

public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();
    private final UserService userService = new UserService();

    // Daily report — today
    public void printDailyReport() {
        userService.requireManagerOrAbove();
        LocalDate today = LocalDate.now();
        printReport("DAILY REPORT - " + today, today, today);
    }

    // Weekly report — last 7 days
    public void printWeeklyReport() {
        userService.requireManagerOrAbove();
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(6);
        printReport("WEEKLY REPORT (" + from + " to " + to + ")", from, to);
    }

    // Monthly report — current month
    public void printMonthlyReport() {
        userService.requireManagerOrAbove();
        LocalDate now = LocalDate.now();
        LocalDate from = now.withDayOfMonth(1);
        LocalDate to = now.withDayOfMonth(now.lengthOfMonth());
        printReport("MONTHLY REPORT - " + now.getMonth() + " " + now.getYear(), from, to);
    }

    // Custom range report
    public void printCustomReport(LocalDate from, LocalDate to) {
        userService.requireManagerOrAbove();
        printReport("CUSTOM REPORT (" + from + " to " + to + ")", from, to);
    }

    // Get total sales for a date range
    public double getTotalSales(LocalDate from, LocalDate to) {
        userService.requireManagerOrAbove();
        return reportDAO.getTotalSales(from, to);
    }

    // Get total transactions for a date range
    public int getTotalTransactions(LocalDate from, LocalDate to) {
        userService.requireManagerOrAbove();
        return reportDAO.getTotalTransactions(from, to);
    }

    // Get top selling products
    public Map<String, Integer> getTopSellingProducts(LocalDate from, LocalDate to, int limit) {
        userService.requireManagerOrAbove();
        return reportDAO.getTopSellingProducts(from, to, limit);
    }

    // Get daily sales breakdown
    public Map<String, Double> getDailySalesSummary(LocalDate from, LocalDate to) {
        userService.requireManagerOrAbove();
        return reportDAO.getDailySalesSummary(from, to);
    }

    // Internal print helper
    private void printReport(String title, LocalDate from, LocalDate to) {
        double total = reportDAO.getTotalSales(from, to);
        int transactions = reportDAO.getTotalTransactions(from, to);
        Map<String, Integer> topProducts = reportDAO.getTopSellingProducts(from, to, 5);

        System.out.println("========== " + title + " ==========");
        System.out.printf("Total Sales:        Rs. %.2f%n", total);
        System.out.printf("Total Transactions: %d%n", transactions);
        System.out.println("Top 5 Products:");
        topProducts.forEach((name, qty) ->
            System.out.printf("  %-20s %d units%n", name, qty));
        System.out.println("==========================================");
    }
}
