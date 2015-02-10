package com.naio.opengl;

import com.naio.diagnostic.opengl.MyRenderer;
import com.naio.diagnostic.trames.LogTrame;

import net.sourceforge.juint.UInt16;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen. This view
 * can also be used to capture touch events, such as a user interacting with
 * drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

	private final MyRenderer mRenderer;

	public MyGLSurfaceView(Context context) {
		super(context);

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);

		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new MyRenderer();
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private float mPreviousX;
	private float mPreviousY;

	public void update_with_uint16(UInt16[] uint16) {
		mRenderer.setPosition(uint16);
		requestRender();
	}

	public void update_with_double(double[][] points) {

		mRenderer.setPointsForLine(points);
		requestRender();
	}

}