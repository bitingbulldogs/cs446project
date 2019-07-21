package com.example.scbcchoi.eatemup.inventory;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.scbcchoi.eatemup.R;

import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryVH> {


    private List<InventoryListItem> inventoryList;
    public Dialog inventoryDialog;
    private int pos;// last clicked item
    private boolean showMultiSelect;
    private onClickListener onClickListener;

    private SparseBooleanArray selectedItem;

    public InventoryAdapter(List<InventoryListItem> inventoryList){
        this.inventoryList = inventoryList;
        this.showMultiSelect = false;
        this.selectedItem = new SparseBooleanArray();
    }


    public interface onClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }


    public void setOnClickListener(onClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public InventoryVH onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        final View view = inflater.inflate(R.layout.inventory_list, viewGroup, false);
        final InventoryVH tempVH = new InventoryVH(view);

        inventoryDialog = new Dialog(viewGroup.getContext());
        inventoryDialog.setContentView(R.layout.dialog_inventory);



        return tempVH;
    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryVH inventoryVH, final int position) {

        InventoryListItem inventory = inventoryList.get(position);
        inventoryVH.checkBox.setTag(position);

        Integer days = inventory.getDateInt();
        inventoryVH.textViewName.setText(inventory.getName());
        inventoryVH.textViewDate.setText(date_conversion(days).toString());
        inventoryVH.day_s.setText(date_name_conversion(days));

        if(showMultiSelect){
            inventoryVH.checkBox.setVisibility(View.VISIBLE);
            inventoryVH.has_expired.setVisibility(View.GONE);
            inventoryVH.day_s.setVisibility(View.GONE);
            inventoryVH.expire_in.setVisibility(View.GONE);
            inventoryVH.textViewDate.setVisibility(View.GONE);
            inventoryVH.checkBox.setChecked(selectedItem.get(position));

        }else {

            if(days == -1) {
                inventoryVH.has_expired.setVisibility(View.VISIBLE);
                inventoryVH.day_s.setVisibility(View.GONE);
                inventoryVH.expire_in.setVisibility(View.GONE);
                inventoryVH.textViewDate.setVisibility(View.GONE);
                inventoryVH.checkBox.setVisibility(View.GONE);
                inventoryVH.checkBox.setChecked(false);

            } else  {
                inventoryVH.has_expired.setVisibility(View.GONE);
                inventoryVH.day_s.setVisibility(View.VISIBLE);
                inventoryVH.expire_in.setVisibility(View.VISIBLE);
                inventoryVH.textViewDate.setVisibility(View.VISIBLE);
                inventoryVH.checkBox.setVisibility(View.GONE);
                inventoryVH.checkBox.setChecked(false);
            }
        }

        inventoryVH.inventoryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = position;
                if(showMultiSelect){
                    //already selected
                    if (selectedItem.get(position,false)){
                        selectedItem.delete(position);
                        inventoryVH.checkBox.setChecked(false);
                        //if no item is selected
                        if(selectedItem.size() == 0){
                            showMultiSelect = false;
                        }
                    } else {//not selected
                        selectedItem.put(position,true);
                        inventoryVH.checkBox.setChecked(true);
                    }
                    onClickListener.onItemClick(view, position);
                } else {
                    showDialog(position);
                }
            }
        });


        inventoryVH.inventoryCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                notifyDataSetChanged();
                showMultiSelect = true;
                selectedItem.put(position,true);
                onClickListener.onItemLongClick(view, position);
                inventoryVH.checkBox.setChecked(true);
                return true;
            }
        });

        inventoryVH.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int pos = (int) buttonView.getTag();
                if (isChecked) {
                    selectedItem.put(pos, true);
                } else {
                    selectedItem.delete(pos);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    class InventoryVH extends RecyclerView.ViewHolder{
        private TextView textViewName, textViewDate, expire_in, day_s, has_expired;
        private CardView inventoryCardView;
        private CheckBox checkBox;

        public InventoryVH (@NonNull View itemView) {
            super(itemView);
            has_expired = itemView.findViewById(R.id.has_expired);
            expire_in = itemView.findViewById(R.id.will_expire_in);
            day_s = itemView.findViewById(R.id.day_s);
            textViewName = itemView.findViewById(R.id.rv_inventory_name);
            textViewDate = itemView.findViewById(R.id.rv_inventory_date);
            inventoryCardView = itemView.findViewById(R.id.cv_inventory);
            checkBox = itemView.findViewById(R.id.inventory_checkbox);
        }
    }

     private Integer date_conversion(Integer i){
        if(i > 365){
            return (i / 365);
        } else if(i > 30){
            return (i / 30);
        } else if (i > 7){
            return (i / 7);
        } else {
            return i;
        }
    }

    private String date_name_conversion(Integer i){
        if(i > 365){
            return "year (s)";
        } else if(i > 30){
            return "month (s)";
        } else if (i > 7){
            return "week (s)";
        } else {
            return "day (s)";
        }
    }


    public  void makeSelection(int position){
        if (selectedItem.get(position,false)){
            selectedItem.delete(position);
        } else {
            selectedItem.put(position,true);
        }
        notifyItemChanged(position);
    }


    public void showDialog(int position){

        EditText dialogName = inventoryDialog.findViewById(R.id.dialog_name);
        EditText dialogDate = inventoryDialog.findViewById(R.id.dialog_date);
        ImageButton imageButton = inventoryDialog.findViewById(R.id.image_button_calender);
        dialogName.setText(inventoryList.get(position).getName());
        if(inventoryList.get(position).getDateInt() == -1){
            imageButton.setFocusable(false);
            imageButton.setClickable(false);
            dialogDate.setText("Expired");
            dialogDate.setFocusable(false);
            dialogDate.setTextColor(Color.RED);
        } else {
            imageButton.setFocusable(true);
            imageButton.setClickable(true);
            dialogDate.setText(inventoryList.get(position).getDate());
            dialogDate.setTextColor(Color.BLACK);
            dialogDate.setFocusableInTouchMode(true);
            dialogDate.setFocusable(true);
        }


        inventoryDialog.show();
    }


    public InventoryListItem updateItem(){
        EditText nameText = inventoryDialog.findViewById(R.id.dialog_name);
        EditText dateText = inventoryDialog.findViewById(R.id.dialog_date);
        String date = dateText.getText().toString();
        String name = nameText.getText().toString();
        
        if (name.equals("") || date.equals("")) {
            return new InventoryListItem("",0);
        } else {
            inventoryDialog.dismiss();
            if(date.equals("Expired")){
                date = "-1";
                return new InventoryListItem(name, -1);
            } else {
                return new InventoryListItem(name, Integer.parseInt(date));
            }
        }
    }

    public int getPos(){
        return pos;
    }

    public boolean getShowMultiSelect(){
        return showMultiSelect;
    }


    public void setShowMultiSelect(boolean b){
        showMultiSelect = b;
    }

    public void clearSelection(){
        selectedItem.clear();
        showMultiSelect = false;
        notifyDataSetChanged();
    }


    public List<Integer> getSelection(){
        List<Integer> selectionList = new ArrayList<Integer>();
        int size = selectedItem.size();
        for (int i = 0; i < size; i++){
            selectionList.add(selectedItem.keyAt(i));
        }
        return selectionList;
    }

    public int getSelectionCount(){
        return  selectedItem.size();
    }

    public void clearInventory(){
        inventoryList.clear();
    }


    public void noUpdate(){
        inventoryDialog.dismiss();
    }
}

