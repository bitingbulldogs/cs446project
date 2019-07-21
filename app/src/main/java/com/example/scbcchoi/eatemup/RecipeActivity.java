package com.example.scbcchoi.eatemup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.scbcchoi.eatemup.inventory.InventoryListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RecipeActivity extends AppCompatActivity {


    List<String> immediateExpire = new ArrayList<>();
    private GridView recipeGrid;
    public static String data = "";
    JSONArray allRecipes = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_activity);
        recipeGrid = findViewById(R.id.recipe_grid);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getItems();
        try {
            makeRecipeList();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RecipeAdapter list = new RecipeAdapter(getApplicationContext(), allRecipes);
        recipeGrid.setAdapter(list);
//        recipeGrid.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        immediateExpire.clear();
        allRecipes = new JSONArray();
        //TODO: dialog to ask what items consumed completely
    }

    public void makeRecipeList() throws ExecutionException, InterruptedException {
        String all = "";
        for(int i = 0; i<immediateExpire.size(); i++) {
            all = all + immediateExpire.get(i) + ",";
        }
        data = new getRecipes().execute(all).get();
        try {
            JSONObject JO = new JSONObject(data);
            JSONArray recipeArray = (JSONArray) JO.get("results");

            int num = Math.min(7, recipeArray.length());
            int temp = 0;
            for (int j = 0; temp < num; j++) {
                if(((JSONObject)recipeArray.get(j)).get("thumbnail").equals("") ||
                        (((String)((JSONObject)recipeArray.get(j)).get("href")).contains("kraftfoods") ||
                        ((String)((JSONObject)recipeArray.get(j)).get("href")).contains("eatingwell"))) {
                    continue;
                }
                allRecipes.put(recipeArray.get(j));
                temp++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(immediateExpire.size() > 1) {
            for (int i = 0; i < immediateExpire.size(); i++) {
                data = new getRecipes().execute(immediateExpire.get(i)).get();

                try {
                    JSONObject JO = new JSONObject(data);
                    JSONArray recipeArray = (JSONArray) JO.get("results");

                    int num = Math.min(7, recipeArray.length());
                    int temp = 0;
                    for (int j = 0; temp < num; j++) {
                        if(((JSONObject)recipeArray.get(j)).get("thumbnail").equals("") ||
                                (((String)((JSONObject)recipeArray.get(j)).get("href")).contains("kraftfoods") ||
                                        ((String)((JSONObject)recipeArray.get(j)).get("href")).contains("eatingwell") ||
                                        ((String)((JSONObject)recipeArray.get(j)).get("href")).contains("cookeatshare"))) {
                            continue;
                        }
                        allRecipes.put(recipeArray.get(j));
                        temp++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getItems() {
        ListsModel lm = new ListsModel(this);
        List<InventoryListItem> invList = lm.getInventoryList();
        Map<String, String> alias = lm.getAliasList();
        int days = 0;
        if(Integer.parseInt(invList.get(0).getDate()) > 5) {
            days = Integer.parseInt(invList.get(0).getDate());
        }
        int num = 0;
        if(days > 5) {
            num = Math.min(invList.size(), 5);
        }
        else {
            for(int i=0; i<invList.size(); i++) {
                if(Integer.parseInt(invList.get(i).getDate()) <= 5) {
                    num++;
                }
            }
        }
        for(int i=0; i<num; i++) {
            String name = invList.get(i).getName().toLowerCase();
            int date = Integer.parseInt(invList.get(i).getDate());
            String item = "";
            if(days > 5 && date == days) {
                if(lm.aliasExists(name)) {
                    item = alias.get(name);
                }
                else {
                    item = name;
                }
                immediateExpire.add(item);
            }
            else if(days <= 5 && date <=5) {
                if(lm.aliasExists(name)) {
                    item = alias.get(name);
                }
                else {
                    item = name;
                }
                immediateExpire.add(item);
            }
            else {
                break;
            }
//            ListsModel lm = new ListsModel(this);
//            List<InventoryListItem> invList = lm.getInventoryList();
//            Map<String, String> alias = lm.getAliasList();
//            int days = Integer.parseInt(invList.get(0).getDate());
//            int num = Math.min(invList.size(), 5);
//            for(int i=0; i<num; i++) {
//                String name = invList.get(i).getName();
//                int date = Integer.parseInt(invList.get(i).getDate());
//                String item = "";
//                if(date <= days) {
////                    immediateExpire.add(invList.get(i).getName());
//                    if(lm.aliasExists(name)) {
//                        item = alias.get(name);
//                    }
//                    else {
//                        item = name;
//                    }
//                    immediateExpire.add(item);
//
//                }
//                else {
//                    break;
//            }
        }


//        if(invList.size() == 0) {
//            Toast.makeText(this, "Zero Items", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(this, "Items", Toast.LENGTH_SHORT).show();
//            for (int i = 0; i < invList.size(); i++) {
//                System.out.println(invList.get(i).getName() + " " + invList.get(i).getDate());
//            }
//        }

    }
}
