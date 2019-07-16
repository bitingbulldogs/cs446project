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

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeVH>  {
    private List<String> immediateExpire;
    public RecipeAdapter(List<String> ie){
        immediateExpire = ie;
    }

    public static class RecipeVH extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView itemName;
        public RecipeVH(@NonNull View v) {
            super(v);
            itemName = v.findViewById(R.id.recipe_name);
        }
    }
    @NonNull
    @Override
    public RecipeAdapter.RecipeVH onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        final View view = inflater.inflate(R.layout.recipe_item, viewGroup, false);
        final RecipeAdapter.RecipeVH tempVH = new RecipeAdapter.RecipeVH(view);
        return tempVH;
    }

    @Override
    public int getItemCount() {
        return immediateExpire.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.RecipeVH recipeVH, int i) {
        String item = immediateExpire.get(i);
        recipeVH.itemName.setText(item);
    }
}
