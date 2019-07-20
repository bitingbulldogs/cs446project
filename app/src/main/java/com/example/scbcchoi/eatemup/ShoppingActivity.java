package com.example.scbcchoi.eatemup;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class ShoppingActivity extends AppCompatActivity {
    private Dialog addDialog;
    public static boolean checkBoxes[];
    List<ShoppingItem> shoppinglist;
    private RecyclerView recyclerView;
    private boolean allischecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_activity);
        recyclerView = findViewById(R.id.rv_shopping);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateShoppingList();
    }

    public void add(String val){
        ListsModel lm = new ListsModel(this);
        lm.addToList("shopping",val, "");
    }

    public void addButtonOnClick(View v){
        addDialog = new Dialog(v.getContext());
        addDialog.setContentView(R.layout.dialog_addshop);
        addDialog.show();
    }

    public void shopcancel(View v){
        addDialog.dismiss();
    }

    public void addShoppinglist(View v){
        EditText nameText = addDialog.findViewById(R.id.dialog_shopname_add);
        String name = nameText.getText().toString();
        this.add(name);
        addDialog.dismiss();

        //update adapter
        updateShoppingList();

    }

    //it update the shoppinglist from local storage, and update recycle view.
    private void updateShoppingList(){
        shoppinglist = new ArrayList<>();
        ListsModel lm = new ListsModel(this);
        List<String> shopls = lm.getShoppingList();
        checkBoxes = new boolean[shopls.size()];
        for(int i = 0; i < shopls.size(); ++i){
            checkBoxes[i] = false;
            shoppinglist.add(new ShoppingItem(shopls.get(i)));
        }
        ShoppingAdapter shopA = new ShoppingAdapter(shoppinglist);
        recyclerView.setAdapter(shopA);
        recyclerView.getAdapter().notifyDataSetChanged();

        //set the top checkbox to initial false value
        CheckBox checkAll = findViewById(R.id.shop_checkALL);
        checkAll.setChecked(false);
    }

    public void setshopCheck(View v){
        CheckBox c = v.findViewById(R.id.shop_checkbox);
        int i = Integer.valueOf(c.getText().toString());
        checkBoxes[i] = c.isChecked();
    }

    public void removeButtonOnClick(View v){
        ListsModel lm = new ListsModel(this);
        List<String> shopls = lm.getShoppingList();
        for(int i = 0; i < shoppinglist.size(); ++i){
            if(checkBoxes[i]) {
                String removeItem = shopls.get(i);
                lm.removeFromList("shopping", removeItem);
            }
        }
        updateShoppingList();
    }

    public void selectAllShopItems(View v){
        allischecked = !allischecked;
        boolean checked = allischecked;
        for(int i = 0; i < checkBoxes.length; ++i) checkBoxes[i] = checked;

        int i = 0;
        View rv;
        CheckBox c;
        while(true){
            rv = recyclerView.getLayoutManager().findViewByPosition(i++);
            if(rv == null) break;
            c = rv.findViewById(R.id.shop_checkbox);
            c.setChecked(checked);
        }
    }

    public void gotoMain(View v){
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        finish();
    }

    public void shoppingTest(View v){
        BackgroundService.oneDayHasPassed(this);
    }

    public void clearHistory(View v){
        ListsModel lm = new ListsModel(this);
        lm.clearList("history");
        Settings.setStr("todaysDate", "", this);
        lm.addToList("history", "apple", 3);
    }
}
