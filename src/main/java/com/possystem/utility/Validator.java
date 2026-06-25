package com.possystem.sajilopos.util;

public class Validator {

    public static boolean isValidPrice(String value) {
        try {
            double price = Double.parseDouble(value.trim());
            return price > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidQuantity(String value) {
        try {
            int qty = Integer.parseInt(value.trim());
            return qty > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String cleaned = phone.trim().replaceAll("[\\s\\-]", "");
        return cleaned.matches("^[0-9]{7,15}$");
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidStock(String value) {
        try {
            int stock = Integer.parseInt(value.trim());
            return stock >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
