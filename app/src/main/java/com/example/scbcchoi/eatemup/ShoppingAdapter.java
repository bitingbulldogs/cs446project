package com.example.scbcchoi.eatemup;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingVH>  {
    private List<ShoppingItem> shoppingItemList;
    public ShoppingAdapter(List<ShoppingItem> s){
        shoppingItemList = s;
    }

    public static class ShoppingVH extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView itemName, itemCat, expireDate;
        private CheckBox checkbox;
        public ShoppingVH(@NonNull View v) {
            super(v);
            itemName = v.findViewById(R.id.shop_item_name);
            checkbox = v.findViewById(R.id.shop_checkbox);
        }
    }
    @NonNull
    @Override
    public ShoppingAdapter.ShoppingVH onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        final View view = inflater.inflate(R.layout.shop_item_list, viewGroup, false);
        final ShoppingAdapter.ShoppingVH tempVH = new ShoppingAdapter.ShoppingVH(view);
        return tempVH;
    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingAdapter.ShoppingVH shoppingVH, int i) {
        ShoppingItem item = shoppingItemList.get(i);
        shoppingVH.itemName.setText(item.getName());
        shoppingVH.checkbox.setChecked(ShoppingActivity.checkBoxes[i]);
        shoppingVH.checkbox.setText(Integer.toString(i));
        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
    }
}
