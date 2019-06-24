package com.example.scbcchoi.eatemup;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.scbcchoi.eatemup.inventory.InventoryListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/*
initial app load
common food item: expiry date

items = {
  "apple": 10,
  "orange": 7,
  "egg": 14
}

user scans receipt
1.apple123
- does not exist in alias, find best match in items => "apple"
- add alias "apple123" : "apple"

2.apple123
- exists in alias, find matching item => "apple"
- find expiry date for apple => items["apple"]

3.egg
- does not exist in alias, exists in items
- return expiry date

4.grapes
- does not exist in alias, does not exist in items
- Call API, add to items


items = {
  //item : expiry date
  "apple": 10,
  "orange": 7,
  "egg": 14
}

alias = {
  "apple123": "apple"
  "oranges": "orange"
}

functions to provide users:
int getExpiryDate(String foodItem)
 */

public class ListsModel {

    private SharedPreferences commonItemList;
    private SharedPreferences aliasList;
    private SharedPreferences inventoryList;
    private SharedPreferences shoppingList;
    private SharedPreferences expiredHistory;


    public ListsModel(Context context){
        commonItemList = context.getSharedPreferences("commonItemList", Context.MODE_PRIVATE);
        aliasList = context.getSharedPreferences("aliasList", Context.MODE_PRIVATE);
        inventoryList = context.getSharedPreferences("inventoryList", Context.MODE_PRIVATE);
        shoppingList = context.getSharedPreferences("shoppingList", Context.MODE_PRIVATE);
        expiredHistory = context.getSharedPreferences("expiredHistory", Context.MODE_PRIVATE);
        initCommonList();
    }

    // hard code common food item & expiry date
    private void initCommonList() {
        // TODO: look into capitalizing food items
        SharedPreferences.Editor editor = commonItemList.edit();
        editor.putInt("apple", 10)
                .putInt("orange", 7)
                .putInt("egg", 14)
                .apply();

        //dummy for alias
        SharedPreferences.Editor aliasEditor = aliasList.edit();
        aliasEditor.putString("apple123", "apple").apply();

        //dummy for inventory
        SharedPreferences.Editor invEditor = inventoryList.edit();
        /*
        invEditor.putInt("apple", 1)
                .putInt("orange", 3)
                .putInt("banana", 5)
                .putInt("avocado", 6)
                .putInt("pineapple", 7)
                .putInt("kiwi", 11)
                .putInt("milk", 14)
                .putInt("watermelon", 14)
                .putInt("cherry", 20)
                .putInt("tomato", 22)
                .putInt("potato", 40)
                .putInt("cheese", 98)
                .apply();*/

        //dummy for expired history
        SharedPreferences.Editor historyEditor = expiredHistory.edit();
        historyEditor.putInt("apple", 1)
                .putInt("orange", 4)
                .putInt("banana", 2)
                .apply();

        //dummy for shopping list
        SharedPreferences.Editor shoppingListEditor = shoppingList.edit();
        shoppingListEditor.putBoolean("apple", true)
                .putBoolean("egg", true)
                .putBoolean("watermelon", true)
                .apply();
    }

    public List<InventoryListItem> getInventoryList(){
        Map<String, ?> map = inventoryList.getAll();
        List<InventoryListItem> list = new ArrayList<>();

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            InventoryListItem item = new InventoryListItem(entry.getKey(), Integer.parseInt(entry.getValue().toString()));
            list.add(item);
        }

        return list;
    }

    public Map<String, Integer> getExpiredHistoryList(){
        Map<String, ?> map = expiredHistory.getAll();
        Map<String, Integer> newMap = new HashMap<>();

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            newMap.put(entry.getKey() ,Integer.parseInt(entry.getValue().toString()));
        }

        return newMap;
    }

    public List<String> getShoppingList(){
        List<String> list = new ArrayList<>();
        Map<String, ?> map = shoppingList.getAll();

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String item = entry.getKey();
            list.add(item);
        }

        return list;
    }

    public void addToList(String listType, String key, String val){
        //TODO: look into providing different list types
        if (listType == "alias"){
            SharedPreferences.Editor aliasEditor = aliasList.edit();
            aliasEditor.putString(key, val).apply();
        } else if (listType == "shopping"){
            SharedPreferences.Editor shoppingEditor = shoppingList.edit();
            shoppingEditor.putBoolean(key, true).apply();
        }
    }

    public void addToList(String listType, String key, Integer val){
        //TODO: look into providing different list types
        if (listType == "common"){
            SharedPreferences.Editor editor = commonItemList.edit();
            editor.putInt(key, val).apply();
        } else if (listType == "inventory"){
            SharedPreferences.Editor inventoryEditor = inventoryList.edit();
            inventoryEditor.putInt(key, val).apply();
        } else if (listType == "history") {
            SharedPreferences.Editor historyEditor = expiredHistory.edit();
            historyEditor.putInt(key, val).apply();
        }
    }

    public void removeFromList(String listType, String key){
        //TODO: look into providing different list types
        SharedPreferences.Editor editor;
        if (listType == "common"){
            editor = commonItemList.edit();
        } else if (listType == "alias"){
            editor = aliasList.edit();
        } else if (listType == "inventory"){
            editor = inventoryList.edit();
        } else if (listType == "shopping"){
            editor = shoppingList.edit();
        } else { //"history"
            editor = expiredHistory.edit();
        }
        editor.remove(key).apply();
    }

    public void printList(String listType){
        if (listType == "common"){
            System.out.println(commonItemList.getAll());
        } else if (listType == "alias"){
            System.out.println(aliasList.getAll());
        } else if (listType == "inventory"){
            System.out.println(inventoryList.getAll());
        } else if (listType == "shopping"){
            System.out.println(shoppingList.getAll());
        } else { //"history"
            System.out.println(expiredHistory.getAll());
        }
    }

    public void clearShoppingList(){
        SharedPreferences.Editor shoppingEditor = shoppingList.edit();
        shoppingEditor.clear().apply();
    }

    public void clearInventory(){
        SharedPreferences.Editor editor = inventoryList.edit();
        editor.clear().apply();
    }

    public void clearAlias(){
        SharedPreferences.Editor editor = aliasList.edit();
        editor.clear().apply();
    }

    public void clearCommonItems(){
        SharedPreferences.Editor editor = commonItemList.edit();
        editor.clear().apply();
    }

    private boolean matchesSubstring(String substring, String target){
        return target.contains(substring);
    }

    // given item, return an item that has the longest common substring matching in common item list
    // TODO: BRUTE FORCE ALGO, NEED TO IMPROVE
    public int closestItemMatch(String foodItem){
        // if found, return expiry date, map alias with found item and add to alias list
        // else return -1 to indicate no match is found
        int maxLength = 0;
        String bestMatchItem = "";
        int bestMatchItemExpiry = -1;

        Map<String, ?> map = commonItemList.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            for (int i=0; i<foodItem.length(); i++){
                for (int j=i+1; j<=foodItem.length(); j++){
                    String substring = foodItem.substring(i, j);
                    int len = matchesSubstring(substring, entry.getKey())? substring.length() : 0;
                    if (len > maxLength && (double)len/entry.getKey().length() >= 0.65){
                        maxLength = len;
                        bestMatchItem = entry.getKey();
                        bestMatchItemExpiry = Integer.parseInt(entry.getValue().toString());
                    }
                }
            }
        }

        //addToList("alias", foodItem, bestMatchItem);


        return bestMatchItemExpiry;
    }

    // new item, call api to get expiry date
    private int fetchExpiryDate(String foodItem){
        return -1;
    }

    public int getExpiryDate(String foodItem){
        if (aliasExists(foodItem)){
            String nameInItemDir = aliasList.getString(foodItem, null);
            return commonItemList.getInt(nameInItemDir, 0);
        } else if (itemExists(foodItem)){
            return commonItemList.getInt(foodItem, 0);
        } else {
            int expiry = closestItemMatch(foodItem);
            expiry = expiry != -1 ? expiry : 5;/*fetchExpiryDate(foodItem)*/
            return expiry;
        }
    }

    public boolean aliasExists(String s){
        return aliasList.contains(s);
    }

    public boolean itemExists(String s){
        return commonItemList.contains(s);
    }

}
