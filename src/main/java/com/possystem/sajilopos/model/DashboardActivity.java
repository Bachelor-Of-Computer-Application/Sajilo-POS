package com.possystem.sajilopos.model;

import java.sql.Timestamp;

public class DashboardActivity {
    private final String icon;
    private final String description;
    private final Timestamp timestamp;

    public DashboardActivity(String icon, String description, Timestamp timestamp) {
        this.icon = icon;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
