package com.example.opengltest;

import android.content.Context;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.opengles.GL10;
import 	android.opengl.GLES20;
import android.opengl.Matrix;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private Triangle tri;
    private Cube cub;
    private Sphere sph;
    private float[] rotationMatrix = new float[16];
    public static float curRotateDeg = 0f;
    public static float rotateAcc = 0f;
    public static float forward = 0f;
    public static float forAcc = 0f;

    public static int displayingObject = 0; //0 means sphere, 1 means cube

    MyGLRenderer(Context ctx){

    }



    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.8f, 0.8f, 1.0f);
        cub = new Cube(new float[]{0.0f,0.0f,0.0f}, 0.5f);
        sph = new Sphere(new float[]{0.0f,0.0f,0.0f}, 1.0f, 30.0f, 60);
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -8, 0f, 0f, 0f, 0f, 1.0f, 0.0f);


        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        forward += forAcc;
        Matrix.translateM(vPMatrix, 0, 0f, 0f, forward);

        float[] scratch = new float[16];
        //long time = SystemClock.uptimeMillis() % 4000L;
        curRotateDeg = (curRotateDeg + rotateAcc) % 360.0f;
        Matrix.setRotateM(rotationMatrix, 0, curRotateDeg, 0, 1.0f, 0);

        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);



        //tri.draw(vPMatrix);
        //cub.draw(scratch);
        if(displayingObject == 0) sph.draw(scratch);
        else if(displayingObject == 1) cub.draw(scratch);
    }

    private float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];


    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 20);
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }


}
