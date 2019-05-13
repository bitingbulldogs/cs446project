package com.example.opengltest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.opengl.GLSurfaceView;

import java.io.InputStream;
import java.util.Scanner;



public class MainActivity extends AppCompatActivity {

    private GLSurfaceView gLView;

    public static String vertexShaderCode;
    public static String fragmentShaderCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        try{
            InputStream in = getAssets().open("vertexShader.txt");
            Scanner scanner = new Scanner(in);
            vertexShaderCode = scanner.useDelimiter("\\A").next();
            System.out.println("vertexShaderCode = " + vertexShaderCode);
            scanner.close(); // Put this call in a finally block

            in.close();
            in = getAssets().open("fragShader.txt");
            scanner = new Scanner(in);
            fragmentShaderCode = scanner.useDelimiter("\\A").next();
            System.out.println("fragShaderCode = " + fragmentShaderCode);
            scanner.close(); // Put this call in a finally block

        }
        catch(Exception e){
            System.out.println(e.getMessage()+"failed to open vertexShader.txt");
        }


        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gLView = new MyGLSurfaceView(this);
        setContentView(gLView);

    }



}
