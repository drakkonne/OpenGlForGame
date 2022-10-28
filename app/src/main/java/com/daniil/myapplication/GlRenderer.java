package com.daniil.myapplication;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glEnable;

public class GlRenderer implements GLSurfaceView.Renderer {

    public GlRenderer(Context context) {
        m_context = context;
    }

    private final static int POSITION_COUNT = 3;

    private Context m_context;

    private FloatBuffer m_vertexData;
    private int m_colorLocation;
    private int m_positionLocation;
    private int m_programId;
    private int m_viewMLocation;
    private int m_projMLocation;
    private int m_modelMLocation;
    private int m_step = 0;
    boolean m_isDown = false;

    private float[] m_projectionMatrix = new float[16];
    private float[] m_viewMatrix = new float[16];
    // private float[] m_totalMatrix = new float[16];
    private float[] m_modelMatrix = new float[16];


    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        glClearColor(0f, 0f, 0f, 1f);
        glEnable(GL_DEPTH_TEST);
        Matrix.setIdentityM(m_modelMatrix, 0);
        int vertexShaderId = ShaderUtils.createShader(m_context, GL_VERTEX_SHADER, R.raw.vertex);
        int fragmentShaderId = ShaderUtils.createShader(m_context, GL_FRAGMENT_SHADER, R.raw.fragment);
        m_programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(m_programId);
        createViewMatrix();
        prepareData();
        bindData();
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);
        createProjectionMatrix(width, height);
        bindMatrix();
    }

    private void prepareData() {

        float[] vertices = {
                -1, -0.5f, 0.5f,
                1, -0.5f, 0.5f,
                0, 0.5f, 0.5f,

                // ось X
                -3f, 0, 0,
                3f, 0, 0,

                // ось Y
                0, -3f, 0,
                0, 3f, 0,

                // ось Z
                0, 0, -3f,
                0, 0, 3f
        };

        m_vertexData = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        m_vertexData.put(vertices);
    }

    private void bindData() {
        m_positionLocation = glGetAttribLocation(m_programId, "m_position");
        m_vertexData.position(0);
        glVertexAttribPointer(m_positionLocation, POSITION_COUNT, GL_FLOAT, false, 0, m_vertexData);
        glEnableVertexAttribArray(m_positionLocation);
        m_colorLocation = glGetUniformLocation(m_programId, "m_color");
        //m_matrixLocation    = glGetUniformLocation(m_programId, "m_matrix");
        m_viewMLocation = glGetUniformLocation(m_programId, "m_viewMatrix");
        m_projMLocation = glGetUniformLocation(m_programId, "m_projectMatrix");
        m_modelMLocation = glGetUniformLocation(m_programId, "m_modelMatrix");
    }

    private void createProjectionMatrix(int width, int height) {
        float left = -1;
        float right = 1;
        float bottom = -1;
        float top = 1;
        float near = 1;
        float far = 8;
        if (width > height) {
            float ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            float ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }
        Matrix.frustumM(m_projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    private void createViewMatrix() {
        // точка положения камеры
        float eyeX = 2;
        float eyeY = 2;
        float eyeZ = 3;

        // точка направления камеры
        float centerX = 0;
        float centerY = 0;
        float centerZ = 0;

        // up-вектор
        float upX = 0;
        float upY = 1;
        float upZ = 0;

        Matrix.setLookAtM(m_viewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }


    private void bindMatrix() {

        //Matrix.multiplyMM(m_totalMatrix, 0, m_projectionMatrix, 0, m_viewMatrix, 0);
        Matrix.setIdentityM(m_modelMatrix, 0);
        glUniformMatrix4fv(m_modelMLocation, 1, false, m_modelMatrix, 0);
        glUniformMatrix4fv(m_projMLocation, 1, false, m_projectionMatrix, 0);
        glUniformMatrix4fv(m_viewMLocation, 1, false, m_viewMatrix, 0);
        //glUniformMatrix4fv(m_matrixLocation, 1, false, m_totalMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        glLineWidth(20);
        Matrix.setIdentityM(m_modelMatrix, 0);
        glUniformMatrix4fv(m_modelMLocation, 1, false, m_modelMatrix, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glUniform4f(m_colorLocation, 1.0f, 0.0f, 0.0f, 0.0f);
        glDrawArrays(GL_LINES, 3, 2);
        glUniform4f(m_colorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_LINES, 5, 2);
        glUniform4f(m_colorLocation, 1.0f, 1.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 7, 2);
        glUniform4f(m_colorLocation, 1.0f, 0.0f, 1.0f, 1.0f);
        float angle = (float)(SystemClock.uptimeMillis() % 10000) / 10000 * 360;
        Matrix.rotateM(m_modelMatrix, 0, angle*2, 0, 0, -1);
        glUniformMatrix4fv(m_modelMLocation, 1, false, m_modelMatrix, 0);
        glDrawArrays(GL_TRIANGLES, 0, 3);
    }
}
