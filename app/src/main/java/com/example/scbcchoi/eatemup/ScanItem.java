package com.example.scbcchoi.eatemup;

public class ScanItem {
    private String name;
    private String category;
    private int expireDate;
    public ScanItem(String n, String c, int e){
        name = n;
        category = c;
        expireDate = e;
    }
    public String getName(){
        return name;
    }
    public String getCategory(){
        return category;
    }
    public int getExpireDate(){
        return expireDate;
    }

}
