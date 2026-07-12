package com.possystem.sajilopos.repository;

import com.possystem.sajilopos.dao.DashboardDAO;
import com.possystem.sajilopos.model.DashboardActivity;
import com.possystem.sajilopos.model.DashboardAggregateModel;
import com.possystem.sajilopos.model.DashboardFooterSummary;
import com.possystem.sajilopos.model.DashboardLowStockItem;
import com.possystem.sajilopos.model.DashboardTopProduct;
import com.possystem.sajilopos.model.DashboardTransaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DashboardRepository {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public DashboardAggregateModel getDashboardSummary(int companyId) {
        return new DashboardAggregateModel(
                dashboardDAO.getTodayRevenue(companyId),
                dashboardDAO.getTodayProfit(companyId),
                dashboardDAO.getTransactionsToday(companyId),
                dashboardDAO.getProductsSoldToday(companyId),
                dashboardDAO.getActiveCustomersToday(companyId),
                dashboardDAO.getLowStockCount(companyId),
                dashboardDAO.getInventoryValue(companyId)
        );
    }

    public double getTodayRevenue(int companyId) {
        return dashboardDAO.getTodayRevenue(companyId);
    }

    public double getYesterdayRevenue(int companyId) {
        return dashboardDAO.getYesterdayRevenue(companyId);
    }

    public double getTodayProfit(int companyId) {
        return dashboardDAO.getTodayProfit(companyId);
    }

    public int getTransactionsToday(int companyId) {
        return dashboardDAO.getTransactionsToday(companyId);
    }

    public List<DashboardLowStockItem> getLowStockProducts(int companyId, int limit) {
        return dashboardDAO.getLowStockProducts(companyId, limit);
    }

    public List<DashboardTopProduct> getTopSellingProducts(int companyId, LocalDate from, LocalDate to, int limit) {
        return dashboardDAO.getTopSellingProducts(companyId, from, to, limit);
    }

    public Map<LocalDate, Double> getSalesAnalytics(int companyId, LocalDate from, LocalDate to) {
        return dashboardDAO.getSalesAnalytics(companyId, from, to);
    }

    public Map<String, Double> getSalesDistribution(int companyId, LocalDate from, LocalDate to) {
        return dashboardDAO.getSalesDistribution(companyId, from, to);
    }

    public List<DashboardTransaction> getRecentTransactions(int companyId, int limit) {
        return dashboardDAO.getRecentTransactions(companyId, limit);
    }

    public List<DashboardActivity> getRecentActivities(int companyId, int limit) {
        return dashboardDAO.getRecentActivities(companyId, limit);
    }

    public DashboardFooterSummary getFooterSummary(int companyId) {
        return new DashboardFooterSummary(
                dashboardDAO.getTotalProducts(companyId),
                dashboardDAO.getTotalCustomers(companyId),
                dashboardDAO.getTotalSuppliers(companyId),
                dashboardDAO.getInventoryValue(companyId)
        );
    }
}
