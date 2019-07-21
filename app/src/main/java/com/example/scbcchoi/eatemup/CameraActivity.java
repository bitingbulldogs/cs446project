package com.example.scbcchoi.eatemup;

import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scbcchoi.eatemup.inventory.InventoryListItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private int resultSize = 0;
    List<ScanItem> scanlist;
    static boolean checkBoxes[];
    private String keys[];
    private int vals[];
    private boolean allischecked = false;
    private ScanAdapter scanA;

    public boolean similar(String s1, String s2) {
        String filterds1 = s1.replaceAll("[^a-zA-Z]", "");
        String filterds2 = s2.replaceAll("[^a-zA-Z]", "");
        if(filterds1.equals(filterds2)) return true;
        else {
            boolean ret = false;

            int count = 0;
            int minSize = Math.min(filterds1.length(), filterds2.length());
            System.out.println("minSize = " + minSize);
            for(int i = 0; i < minSize; ++i){
                if(filterds1.substring(i,i+1).equals(filterds2.substring(i,i+1))) count++;
            }
            double rate = count / Math.min(filterds1.length(), filterds1.length());
            if(rate > 0.8) ret = true;
            System.out.println(filterds1 +", and " + filterds2+ " are " + rate);

            return ret;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        resultSize = 0;

        Intent intent = getIntent();
        ArrayList<String> result = intent.getStringArrayListExtra("result");
        if(!(result == null)) {
            //filter repeated results
            int maxLength = 0;
            String bestMatchItem = "";
            int bestMatchItemExpiry = -1;
            Boolean matchFound = false;
            resultSize = result.size();
            for(int k = 0; k < resultSize; ++k){
                for(int j = k+1; j < resultSize; ++j){
                    System.out.println("similar start for " + result.get(k) + ", and " + result.get(j));
                    boolean theyareSimilar = similar(result.get(k), result.get(j));
                    System.out.println("they are similar? " + theyareSimilar);
                    System.out.println("j = " + j + ", resultSize = " + resultSize);
                    if(theyareSimilar) {
                        result.remove(j);
                        j--;
                        resultSize--;
                    }
                }
            }
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
                expiryDate = lm.getExpiryDate(result.get(i)).first;
                String cat = lm.getExpiryDate(result.get(i)).second;
                keys[i] = result.get(i);
                vals[i] = expiryDate;
                if(lm.aliasExists(result.get(i)) || expiryDate > 0) checkBoxes[i] = true;
                scanlist.add(new ScanItem(result.get(i), cat, expiryDate));
            }
            scanA = new ScanAdapter(scanlist);
            recyclerView.setAdapter(scanA);

            //hide the scan button
//            Button b = findViewById(R.id.button);
//            b.setVisibility(View.GONE);
        }

        else {
            Intent ocrIntent = new Intent(this, OcrCaptureActivity.class);
            startActivity(ocrIntent);
            finish();
        }
    }

    /*
    public void scan(View v){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        startActivity(intent);
        finish();
    }
    */

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
        scanlist = scanA.getScanItemList();
        for(int i = 0; i < resultSize; ++i) {
            if(checkBoxes[i]) {
                String key = scanlist.get(i).getName();
                int val = scanlist.get(i).getExpireDate(); //date
                String cat = scanlist.get(i).getCategory();
                lm.addToList("inventory", key, val);
                lm.addToList("alias", key.toLowerCase(), cat.toLowerCase());
                lm.addToList("common", cat.toLowerCase(), val);
            }
        }
        System.out.println("This is invetory list");
        lm.printList("inventory");
        System.out.println("This is alias list");
        lm.printList("alias");
        System.out.println("This is common list");
        lm.printList("common");
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        finish();
    }
    public void setCheck(View v){
        CheckBox c = v.findViewById(R.id.scan_checkbox);
        int i = Integer.valueOf(c.getText().toString());
        checkBoxes[i] = c.isChecked();
    }

    private Dialog datePickerDialog;
    public void pickDate(View view){
        datePickerDialog = new Dialog(view.getContext());
        datePickerDialog.setContentView(R.layout.dialog_date_picker);
        datePickerDialog.show();
    }

    private void dateSelectHelper(View view, EditText editText){
        DatePicker datePicker = datePickerDialog.findViewById(R.id.date_picker);
        Calendar tempCalendar = Calendar.getInstance();
        long millis1 = tempCalendar.getTimeInMillis();

        tempCalendar.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),0,0);

        long millis2 = tempCalendar.getTimeInMillis();

        if(millis2 < millis1){
            Toast.makeText(view.getContext(), "Hey,it's already expired, add it to the shopping list", Toast.LENGTH_SHORT).show();
            return;
        }
        editText.setText((String.valueOf((millis2 - millis1)/(1000*3600*24))));
        datePickerDialog.dismiss();
    }

    public void dateSelect(View view){
        EditText editText;
        editText = findViewById(R.id.rv_scandate);
        dateSelectHelper(view, editText);
    }

    public void dateCancel(View v){
        datePickerDialog.dismiss();
    }
}