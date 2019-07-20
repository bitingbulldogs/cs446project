package com.example.scbcchoi.eatemup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class RecipeAdapter extends BaseAdapter {
    private JSONArray allRecipes;
    LayoutInflater inflater;
    Context context;
    Bitmap bitImage;

    public RecipeAdapter(Context applicationContext, JSONArray ie) {
        context = applicationContext;
        allRecipes = ie;
        inflater = LayoutInflater.from(applicationContext);
    }

    @Override
    public int getCount() {
        return allRecipes.length();
    }

    @Override
    public Object getItem(int i) {
        Object obj = new Object();
        try {
            obj = allRecipes.get(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.recipe_item, null);
        final TextView itemName = view.findViewById(R.id.recipe_name);
        final ImageView thumbnail = view.findViewById(R.id.recipe_image);
        final TextView ingrd = view.findViewById(R.id.recipe_ingredients);
        String href = "";
        try {
            JSONObject obj = (JSONObject) allRecipes.get(i);
            String name = (String) obj.get("title");
            itemName.setText(name);

            String ing = (String) obj.get("ingredients");
            ingrd.setText(ing);

            String url = (String) obj.get("thumbnail");
//            if(!url.equals("")) {
            Bitmap bitmap = new getImage().execute(url).get();
            thumbnail.setImageBitmap(bitmap);
//            }

            href = (String) obj.get("href");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        final String link = href;

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                context.startActivity(i);
            }
        });

        return view;
    }

    private class getImage extends AsyncTask<String, Void, Bitmap> {

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
