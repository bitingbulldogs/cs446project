package com.example.scbcchoi.eatemup.inventory;


import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.scbcchoi.eatemup.R;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryVH> {


    private List<InventoryListItem> inventoryList;
    private Dialog inventoryDialog;
    private int pos;// last clicked item

    public InventoryAdapter(List<InventoryListItem> inventoryList){
        this.inventoryList = inventoryList;
    }

    @NonNull
    @Override
    public InventoryVH onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        final View view = inflater.inflate(R.layout.inventory_list, viewGroup, false);
        final InventoryVH tempVH = new InventoryVH(view);

        inventoryDialog = new Dialog(viewGroup.getContext());
        inventoryDialog.setContentView(R.layout.dialog_inventory);


        tempVH.inventoryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText dialogName = inventoryDialog.findViewById(R.id.dialog_name);
                EditText dialogDate = inventoryDialog.findViewById(R.id.dialog_date);
                pos = tempVH.getAdapterPosition();
                dialogName.setText(inventoryList.get(pos).getName());
                dialogDate.setText(inventoryList.get(pos).getDate());


                inventoryDialog.show();
            }
        });

        return tempVH;
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryVH inventoryVH, int i) {

        InventoryListItem inventory = inventoryList.get(i);

        inventoryVH.textViewName.setText(inventory.getName());
        inventoryVH.textViewDate.setText(inventory.getDate());


    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    class InventoryVH extends RecyclerView.ViewHolder{
        private TextView textViewName, textViewDate;
        private CardView inventoryCardView;

        public InventoryVH (@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.rv_inventory_name);
            textViewDate = itemView.findViewById(R.id.rv_inventory_date);
            inventoryCardView = itemView.findViewById(R.id.cv_inventory);
        }
    }

    public InventoryListItem updateItem(){
        EditText nameText = inventoryDialog.findViewById(R.id.dialog_name);
        EditText dateText = inventoryDialog.findViewById(R.id.dialog_date);
        int date = Integer.parseInt(dateText.getText().toString());
        String name = nameText.getText().toString();
        inventoryDialog.dismiss();
        return new InventoryListItem(name, date);
    }

    public int getPos(){
        return pos;
    }


    public void noUpdate(){
        inventoryDialog.dismiss();
    }
}

