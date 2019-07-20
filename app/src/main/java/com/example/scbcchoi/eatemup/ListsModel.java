package com.example.scbcchoi.eatemup;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.example.scbcchoi.eatemup.inventory.InventoryListItem;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/*
initial app load

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

        initListModel(context);
    }

    // hard code common food item & expiry date
    private void initListModel(Context context) {
        // initialize common list with items scraped from stilltasty.com
        initCommonList(context);
    }

    private void initCommonList(Context context){
        InputStream is = context.getResources().openRawResource(R.raw.parsed);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        InputStreamReader isr;

        try {
            try {
                isr = new InputStreamReader(is, "UTF-8");
                Reader reader = new BufferedReader(isr);
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch(Exception e) {
                Log.e("ERROR", "InputStreamReader Exception");
            }
        } finally {
            try {
                is.close();
            } catch(Exception e){
                Log.e("ERROR", "Input stream close exception");
            }
        }

        String jsonString = writer.toString();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            for (int i = 0; i<jsonObject.names().length(); i++){
                String key = jsonObject.names().getString(i);
                int val = Integer.parseInt(jsonObject.get(key).toString());
                addToList("common", key, val);
            }
        } catch (Exception e){
            Log.e("ERROR", "JSON Object init exception");
        }
        
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

    public Map<String, String> getAliasList(){
        Map<String, ?> map = aliasList.getAll();
        Map<String, String> newMap = new HashMap<>();

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            newMap.put(entry.getKey() ,entry.getValue().toString());
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
        if (listType == "alias"){
            SharedPreferences.Editor aliasEditor = aliasList.edit();
            aliasEditor.putString(key, val).apply();
        } else if (listType == "shopping"){
            SharedPreferences.Editor shoppingEditor = shoppingList.edit();
            shoppingEditor.putBoolean(key, true).apply();
        }
    }

    public void addToList(String listType, String key, Integer val){
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

    public void clearList(String listType){
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
        editor.clear().apply();
    }

    private boolean matchesSubstring(String substring, String target){
        return target.contains(substring);
    }

    // given item, return an item that has the longest common substring matching in common item list
    // TODO: BRUTE FORCE ALGO, NEED TO IMPROVE
    public Pair<Integer, String> closestItemMatch(String foodItem){
        // if found, return expiry date, map alias with found item and add to alias list
        // else return -1 to indicate no match is found
        int maxLength = 0;
        String bestMatchItem = "";
        int bestMatchItemExpiry = -1;
        Boolean matchFound = false;

        Map<String, ?> map = commonItemList.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            for (int i=0; i<foodItem.length(); i++){
                for (int j=i+1; j<=foodItem.length(); j++){
                    String substring = foodItem.substring(i, j);
                    int len = matchesSubstring(substring, entry.getKey())? substring.length() : 0;
                    if (len > maxLength && (double)len/entry.getKey().length() >= 0.7){
                        System.out.println(len);
                        System.out.println(entry.getKey().length());
                        System.out.println(entry.getKey());
                        System.out.println((double)len/entry.getKey().length());
                        matchFound = true;
                        maxLength = len;
                        bestMatchItem = entry.getKey();
                        bestMatchItemExpiry = Integer.parseInt(entry.getValue().toString());
                    }
                }
            }
        }

        // if foodItem matches an item in items list, add the mapping to alias list
        if (matchFound) {
            addToList("alias", foodItem, bestMatchItem);
            System.out.println("yooo");
            System.out.println(maxLength);
            return new Pair(bestMatchItemExpiry, bestMatchItem);
        }

        return new Pair(0, "not found");
    }


    public Pair<Integer, String> getExpiryDate(String foodItem){
        foodItem = foodItem.toLowerCase();
        if (aliasExists(foodItem)){
            String nameInItemDir = aliasList.getString(foodItem, null);
            return new Pair(commonItemList.getInt(nameInItemDir, 0),nameInItemDir);
        } else if (itemExists(foodItem)){
            return new Pair(commonItemList.getInt(foodItem, 0), foodItem);
        } else {
            Pair<Integer, String> result = closestItemMatch(foodItem);
            return result;
        }
    }

    public boolean aliasExists(String s){
        return aliasList.contains(s);
    }

    public boolean itemExists(String s){
        return commonItemList.contains(s);
    }

}
