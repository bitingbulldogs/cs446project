package com.example.scbcchoi.eatemup;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.scbcchoi.eatemup.inventory.InventoryListItem;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private int resultSize = 0;
    List<ScanItem> scanlist;
    static boolean checkBoxes[];
    private String keys[];
    private int vals[];
    private boolean allischecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        resultSize = 0;

        Intent intent = getIntent();
        ArrayList<String> result = intent.getStringArrayListExtra("result");
        if(!(result == null)) {
            resultSize = result.size();
            checkBoxes = new boolean[resultSize];
            for(int j = 0; j < checkBoxes.length; ++j) checkBoxes[j] = false;
            keys = new String[resultSize];
            vals = new int[resultSize];

            /*
            String scannedText = "";
            for(int i = 0; i < result.size(); ++i) {
                System.out.println(result.get(i));
                scannedText = scannedText + result.get(i) + "\n";
            }
            */

            Toolbar tbar = (Toolbar) findViewById(R.id.toolbar_cam);
            tbar.setVisibility(View.VISIBLE);

            //set up recyclerView
            recyclerView = findViewById(R.id.rv_scan);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            scanlist = new ArrayList<>();
            ListsModel lm = new ListsModel(this);


            for(int i = 0; i < resultSize; ++i){
                int expiryDate = 0;
                expiryDate = lm.getExpiryDate(result.get(i));
                keys[i] = result.get(i);
                vals[i] = expiryDate;
                if(lm.aliasExists(result.get(i))) checkBoxes[i] = true;
                scanlist.add(new ScanItem(result.get(i), "Null Cat", expiryDate));
            }
            ScanAdapter scanA = new ScanAdapter(scanlist);
            recyclerView.setAdapter(scanA);



            //hide the scan button
            Button b = findViewById(R.id.button);
            b.setVisibility(View.GONE);
        }
    }

    public void scan(View v){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        startActivity(intent);
        finish();
    }

    public void selectAllScans(View v){
        allischecked = !allischecked;
        boolean checked = allischecked;
        for(int i = 0; i < checkBoxes.length; ++i) checkBoxes[i] = checked;

        int i = 0;
        View rv;
        CheckBox c;
        while(true){
            rv = recyclerView.getLayoutManager().findViewByPosition(i++);
            if(rv == null) break;
            c = rv.findViewById(R.id.scan_checkbox);
            c.setChecked(checked);
        }
    }

    public void doneScanning(View v){
        ListsModel lm = new ListsModel(this);
        for(int i = 0; i < resultSize; ++i) {
            if(checkBoxes[i]) {
                String key = keys[i];
                int val = vals[i];
                lm.addToList("inventory", key, val);
            }
        }
        System.out.println("send intent");
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        finish();
    }
    public void setCheck(View v){
        CheckBox c = v.findViewById(R.id.scan_checkbox);
        int i = Integer.valueOf(c.getText().toString());
        checkBoxes[i] = c.isChecked();
    }
}