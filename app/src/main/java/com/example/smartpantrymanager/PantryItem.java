package com.example.smartpantrymanager;

public class PantryItem {

    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String category;
    private String expirationDate;

    public PantryItem(int id, String name, double quantity, String unit,
                      String category, String expirationDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.category = category;
        this.expirationDate = expirationDate;
    }

    // Constructor for items not yet saved (id assigned by the DB on insert).
    public PantryItem(String name, double quantity, String unit,
                      String category, String expirationDate) {
        this(-1, name, quantity, unit, category, expirationDate);
    }

    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    @Override
    public String toString() {
        return name + " (" + quantity + " " + unit + ")";
    }
}
