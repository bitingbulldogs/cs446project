package com.example.opengltest;
import android.opengl.GLES20;
import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import 	java.nio.ByteOrder;
import java.nio.IntBuffer;

public class Cube {
    private FloatBuffer vertexBuffer;
    private int COORDS_PER_VERTEX = 3;
    private int vPMatrixHandle;
    private final int mProgram;
    private float radius;
    float cubeCoordinates[];
    int lines[] = { //recording 12 lines
            0, 1,
            1, 2,
            2, 3,
            3, 0,
            4, 5,
            5, 6,
            6, 7,
            7, 4,
            0, 4,
            1, 5,
            2, 6,
            3, 7
    };
    float linesCoordinate[];
    float center[] = {0.0f, 0.0f, 0.0f};

    float color[] = { 0.9f, 0.2f, 0.2f, 1.0f };
    final int vbo[] = new int[1];
    final int ibo[] = new int[1];
    public Cube(float center[],  float rad){
        radius = rad;
        float ox, oy, oz; //center of the cube
        ox = center[0];
        oy = center[1];
        oz = center[2];
        center = new float[]{ox, oy, oz};

        cubeCoordinates = new float[]{ //8 corner points when center is at (0,0,0)
                    -radius, -radius, radius,
                    -radius, radius, radius,
                radius, radius, radius,
                radius, -radius, radius,
                    -radius, -radius, -radius,
                    -radius, radius, -radius,
                radius, radius, -radius,
                radius, -radius, -radius
        };

        //update the 8 corner points of this cube
        for(int i = 0; i < 24;){
            cubeCoordinates[i++] += ox;
            cubeCoordinates[i++] += oy;
            cubeCoordinates[i++] += oz;
        }

        //generate lines coordinates. 12 lines in total
        int count = 0;
        linesCoordinate = new float[72];
        for(int i = 0; i < 12; i++){
            int curRow = i*2;
            linesCoordinate[count++] = cubeCoordinates[lines[curRow]*3];
            linesCoordinate[count++] = cubeCoordinates[lines[curRow]*3 + 1];
            linesCoordinate[count++] = cubeCoordinates[lines[curRow]*3 + 2];
            //no colors for now, can add color at here later.

            linesCoordinate[count++] = cubeCoordinates[lines[curRow + 1]*3];
            linesCoordinate[count++] = cubeCoordinates[lines[curRow + 1]*3 + 1];
            linesCoordinate[count++] = cubeCoordinates[lines[curRow + 1]*3 + 2];
            //add color here
        }

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(linesCoordinate.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(linesCoordinate);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);


        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                MainActivity.vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                MainActivity.fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);


    }

    private int positionHandle;
    private int colorHandle;

    private int linesCount;
    private int vertexStride;

    public void draw(float[] mvpMatrix) {
        COORDS_PER_VERTEX = 3;
        linesCount = linesCoordinate.length / COORDS_PER_VERTEX;
        vertexStride = COORDS_PER_VERTEX * 4;

        //System.out.println("107 works fine");

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the cube
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, linesCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);


    }


}
