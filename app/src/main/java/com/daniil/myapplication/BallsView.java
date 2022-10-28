package com.daniil.myapplication;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import androidx.annotation.Nullable;

public class BallsView extends GLSurfaceView {
    private GlRenderer m_render;

    public BallsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        m_render = new GlRenderer(getContext());
        setRenderer(m_render);
    }
}
