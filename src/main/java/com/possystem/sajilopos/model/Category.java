package com.possystem.sajilopos.model;

public class Category {
    private int id;
    private int companyId;
    private String name;

    public Category(int id, int companyId, String name) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
    }

    // Constructor without id (for new categories)
    public Category(int companyId, String name) {
        this.id = 0;
        this.companyId = companyId;
        this.name = name;
    }

    public int getId()        { return id; }
    public int getCompanyId() { return companyId; }
    public String getName()   { return name; }

    public void setId(int id)                { this.id = id; }
    public void setCompanyId(int companyId)  { this.companyId = companyId; }
    public void setName(String name)         { this.name = name; }

    @Override
    public String toString() { return name; } // so ComboBox displays the name directly
}
