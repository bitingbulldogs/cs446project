package com.example.scbcchoi.eatemup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeVH>  {
    private JSONArray allRecipes;
    public RecipeAdapter(JSONArray ie){
        allRecipes = ie;
    }
    Bitmap bitImage;

    public static class RecipeVH extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView itemName;
        private ImageView thumbnail;
        private TextView ingrd;
        public RecipeVH(@NonNull View v) {
            super(v);
            itemName = v.findViewById(R.id.recipe_name);
            thumbnail = v.findViewById(R.id.recipe_image);
            ingrd = v.findViewById(R.id.recipe_ingredients);
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
        return allRecipes.length();
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.RecipeVH recipeVH, int i) {
        try {
            JSONObject obj = (JSONObject) allRecipes.get(i);
            String name = (String) obj.get("title");
            recipeVH.itemName.setText(name);

            String ing = (String) obj.get("ingredients");
            recipeVH.ingrd.setText(ing);

            String url = (String) obj.get("thumbnail");
            if(!url.equals("")) {
                Bitmap bitmap = new getImage().execute(url).get();
                recipeVH.thumbnail.setImageBitmap(bitmap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class getImage extends AsyncTask<String, Void, Bitmap> {
        ImageView iv;

//        public getImage(ImageView iv) {
//            this.iv = iv;
//        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String imgUrl = url[0];
            bitImage = null;
            try {
                InputStream in = new java.net.URL(imgUrl).openStream();
                bitImage = BitmapFactory.decodeStream(in);
            }
            catch (Exception e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
            return bitImage;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }
}
