package com.example.opengltest;

import android.opengl.GLSurfaceView;
import 	android.content.Context;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyGLRenderer renderer;

    public MyGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        float halfwidth = getWidth()/2;
        float halfheight = getHeight()/2;

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if(x > halfwidth) { //tap the right hand screen
                    if(y > getHeight() - 100) {
                        MyGLRenderer.displayingObject = 1 - MyGLRenderer.displayingObject;
                        MyGLRenderer.rotateAcc = 0.0f;
                        MyGLRenderer.curRotateDeg = 0.0f;
                        MyGLRenderer.forward = 0.0f;
                    }
                    else if(MyGLRenderer.rotateAcc == 0.0f) MyGLRenderer.rotateAcc = 0.5f;
                    else MyGLRenderer.rotateAcc = 0.0f;
                }
                else {
                    if(y > halfheight && MyGLRenderer.forAcc == 0.0f) MyGLRenderer.forAcc = 0.01f;
                    else if(MyGLRenderer.forAcc == 0.0f) MyGLRenderer.forAcc = -0.01f;
                    else MyGLRenderer.forAcc = 0.0f;
                }

        }

        previousX = x;
        previousY = y;
        return true;
    }
}
