package com.example.scbcchoi.eatemup;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ShoppingActivity extends AppCompatActivity {
    private Dialog addDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_activity);
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
    }
}
