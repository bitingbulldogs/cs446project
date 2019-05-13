package com.example.opengltest;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.lang.Math;

public class Sphere {
    private FloatBuffer vertexBuffer;
    private int COORDS_PER_VERTEX = 3;
    private int vPMatrixHandle;
    private final int mProgram;
    private float radius;
    private float center[] = {0,0,0};
    float sphereCoordinates[];
    float linecoord[];
    float N; //N is number of samples of the sphere
    float theta;
    int k;//k is the number of samples in each circle
    public Sphere(float[] cen, float rad, float npra, int t){
        radius = rad;
        center = cen;
        N = npra;
        k = t;
        float L = 0f;
        sphereCoordinates = new float[k*3*((int)N - 1)];
        int sphereCorCount = 0;

        //generate all those points
        for(float i = 1.0f; i < N; ++i){
            L = radius * (float)Math.sqrt(1 - (N - 2*i)*(N - 2*i) / (N*N));
            theta = 360.0f / (float)k;
            for(int j = 0; j < k; ++j){
                float curtheta = theta * (float)j;
                float x, y, z;
                x = L * (float)Math.cos(Math.toRadians(curtheta));
                y = (N - 2*i)*radius/N;
                z = L * (float)Math.sin(Math.toRadians(curtheta));
                sphereCoordinates[sphereCorCount++] = x;
                sphereCoordinates[sphereCorCount++] = y;
                sphereCoordinates[sphereCorCount++] = z;
            }
        }

        //generate all those lines
        linecoord = new float[k*6*((int)N * 2-1)];
        int lineCount = 0;
        sphereCorCount = 0;
        for(float i = 0.0f; i < N; ++i){
            //add lines that connects through this ith circle
            int initSphereCorCount = sphereCorCount;

            if(i == 0.0f){ //we are adding lines of the north pole to our first circle
                float nx, ny, nz;
                nx = 0.0f;
                ny = radius;
                nz = 0.0f;
                for(int j = 0; j < k; ++j){
                    linecoord[lineCount++] = nx;
                    linecoord[lineCount++] = ny;
                    linecoord[lineCount++] = nz;
                    linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];
                    linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];
                    linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];
                }
                sphereCorCount = initSphereCorCount;
            }
            else if(i == N - 1.0f){ //adding lines of south pole to last circle
                float nx, ny, nz;
                nx = 0.0f;
                ny = -radius;
                nz = 0.0f;
                for(int j = 0; j < k; ++j){
                    linecoord[lineCount++] = nx;
                    linecoord[lineCount++] = ny;
                    linecoord[lineCount++] = nz;
                    linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];
                    linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];
                    linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];
                }
                sphereCorCount = initSphereCorCount;
            }
            else{ //not on the north pole or south pole
                for(int j = 0; j < k; ++j){

                    if(i != N-1.0f){//calculate lines btween this circle and next circle,
                                    // note the N-1th circle is connecting to south pole
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount];
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount + 1];
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount + 2];
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount + k*3];
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount + 1 + k*3];
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount + 2 + k*3];
                    }


                    //lines on the same circle
                    if(j != k-1) {
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];

                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount];
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount + 1];
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount + 2];
                    }
                    else{ //last round the end connnects to the start
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];
                        linecoord[lineCount++] = sphereCoordinates[sphereCorCount++];

                        linecoord[lineCount++] = sphereCoordinates[initSphereCorCount];
                        linecoord[lineCount++] = sphereCoordinates[initSphereCorCount + 1];
                        linecoord[lineCount++] = sphereCoordinates[initSphereCorCount + 2];
                    }
                }
            }


        }

        ByteBuffer bb = ByteBuffer.allocateDirect(linecoord.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(linecoord);
        vertexBuffer.position(0);
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                MainActivity.vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                MainActivity.fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

    }

    private int positionHandle;
    private int colorHandle;

    private int linesCount;
    private int vertexStride;
    float color[] = { 0.9f, 0.2f, 0.2f, 1.0f };
    public void draw(float[] mvpMatrix) {
        COORDS_PER_VERTEX = 3;
        linesCount = linecoord.length / COORDS_PER_VERTEX;
        vertexStride = COORDS_PER_VERTEX * 4;
        GLES20.glUseProgram(mProgram);
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, linesCount);
        GLES20.glDisableVertexAttribArray(positionHandle);


    }
}
