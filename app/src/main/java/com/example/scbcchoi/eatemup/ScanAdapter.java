package com.example.scbcchoi.eatemup;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ScanVH>  {
    private List<ScanItem> scanItemList;
    public ScanAdapter(List<ScanItem> s){
        scanItemList = s;
    }

    public static class ScanVH extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private EditText itemName, itemCat, expireDate;
        private CheckBox checkbox;
        public ScanVH(@NonNull View v) {
            super(v);
            itemName = v.findViewById(R.id.rv_scanname);
            itemCat = v.findViewById(R.id.rv_scancat);
            expireDate = v.findViewById(R.id.rv_scandate);
            checkbox = v.findViewById(R.id.scan_checkbox);


        }
    }

    @NonNull
    @Override
    public ScanVH onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        final View view = inflater.inflate(R.layout.scan_list, viewGroup, false);
        final ScanVH tempVH = new ScanVH(view);
        return tempVH;
    }

    @Override
    public int getItemCount() {
        return scanItemList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ScanVH scanVH, int i) {
        ScanItem item = scanItemList.get(i);
        scanVH.itemName.setText(item.getName());
        scanVH.itemCat.setText(item.getCategory());
        scanVH.expireDate.setText(Integer.toString(item.getExpireDate()));
        scanVH.checkbox.setChecked(CameraActivity.checkBoxes[i]);
        scanVH.checkbox.setText(Integer.toString(i));


    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ScanVH scanVH){
        String name = scanVH.itemName.getText().toString();
        String cat = scanVH.itemCat.getText().toString();
        int date = Integer.valueOf(scanVH.expireDate.getText().toString());
        ScanItem scani = scanItemList.get(scanVH.getAdapterPosition());
        scani.setName(name);
        scani.setCat(cat);
        scani.setDate(date);
    }

    public List<ScanItem> getScanItemList(){
        return scanItemList;
    }
}
