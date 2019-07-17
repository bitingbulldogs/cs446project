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

public class getRecipes extends AsyncTask<String, Void, String> {

    String data = "";

    @Override
    protected String doInBackground(String... expItem) {
        try {
            URL url = new URL("http://www.recipepuppy.com/api/?i="+expItem[0]);
            System.out.println(expItem);
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
        return data;
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
    }
}
