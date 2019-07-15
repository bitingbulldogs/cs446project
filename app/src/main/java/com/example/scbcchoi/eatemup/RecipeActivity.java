package com.example.scbcchoi.eatemup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.scbcchoi.eatemup.inventory.InventoryListItem;

import java.util.List;

public class RecipeActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.setting);


        printInventory();

    }

    public void printInventory() {
        ListsModel lm = new ListsModel(this);
        List<InventoryListItem> invList = lm.getInventoryList();
        if(invList.size() == 0) {
            Toast.makeText(this, "Zero Items", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Items", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < invList.size(); i++) {
                System.out.println(invList.get(i).getName() + " " + invList.get(i).getDate());
            }
        }

    }
}
