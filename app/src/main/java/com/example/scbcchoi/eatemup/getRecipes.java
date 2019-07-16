package com.example.scbcchoi.eatemup;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class getRecipes extends AsyncTask<String, Void, Void> {

    String data = "";

    @Override
    protected Void doInBackground(String... expItem) {
        try {
            URL url = new URL("https://www.food2fork.com/api/search?key=bb50c4e176919e4d1143ff8e68cff5c3&q="+expItem);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream is = httpURLConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while(line != null) {
                line = br.readLine();
                data = data + line;
            }
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        RecipeActivity.data = this.data;
    }
}
