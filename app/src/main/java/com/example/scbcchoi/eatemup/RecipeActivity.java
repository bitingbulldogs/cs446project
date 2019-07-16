package com.example.scbcchoi.eatemup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.scbcchoi.eatemup.inventory.InventoryListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {


    List<String> immediateExpire = new ArrayList<>();
    private RecyclerView recyclerView;
    public static String data = "";
    JSONArray allRecipes = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_activity);
        recyclerView = findViewById(R.id.rv_recipe);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getItems();
        makeRecipeList();

        RecipeAdapter list = new RecipeAdapter(immediateExpire);
        recyclerView.setAdapter(list);
        recyclerView.getAdapter().notifyDataSetChanged();

    }

    public void makeRecipeList() {
        data = "";
        String all = "";
        for(int i = 0; i<immediateExpire.size(); i++) {
            all = all + immediateExpire.get(i) + ",";
        }
        new getRecipes().execute(all);
        try {
            JSONObject JO = new JSONObject(data);
            JSONArray recipeArray = (JSONArray) JO.get("recipes");

            int num = Math.min(7, recipeArray.length());
            for (int j = 0; j < num; j++) {
                allRecipes.put(recipeArray.get(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i = 0; i<immediateExpire.size(); i++) {
            data = "";
            new getRecipes().execute(immediateExpire.get(i));

            try {
                JSONObject JO = new JSONObject(data);
                JSONArray recipeArray = (JSONArray) JO.get("recipes");

                int num = Math.min(7, recipeArray.length());
                for (int j = 0; j < num; j++) {
                    allRecipes.put(recipeArray.get(j));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getItems() {
        ListsModel lm = new ListsModel(this);
        List<InventoryListItem> invList = lm.getInventoryList();
        int days = Integer.parseInt(invList.get(0).getDate());
        int num = Math.min(invList.size(), 5);
        for(int i=0; i<num; i++) {
            if(Integer.parseInt(invList.get(i).getDate()) <= days) {
                immediateExpire.add(invList.get(i).getName());
            }
            else {
                break;
            }
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
