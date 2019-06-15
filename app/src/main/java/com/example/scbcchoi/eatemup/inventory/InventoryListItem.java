package com.example.scbcchoi.eatemup.inventory;

//items to display on the inventory list;
//

public class InventoryListItem {

    private String name;
    private int date;

    public InventoryListItem(String name, int date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return Integer.toString(date);
    }
}
