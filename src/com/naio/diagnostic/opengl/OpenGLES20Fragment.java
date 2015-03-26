package com.naio.diagnostic.opengl;

import com.naio.diagnostic.R;
import com.naio.diagnostic.threads.ReadSocketThread;
import com.naio.diagnostic.trames.LidarTrame;
import com.naio.diagnostic.trames.TrameDecoder;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.MemoryBuffer;
import com.naio.opengl.MyGLSurfaceView;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Fragment used in LidarGpsMotorsActivity to display the lidar
 * 
 * @author bodereau
 * 
 */
public class OpenGLES20Fragment extends Fragment {

	private GLSurfaceView mGLView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mGLView = new MyGLSurfaceView(this.getActivity());
		return mGLView;
	}

	/**
	 * @return the mGLView
	 */
	public GLSurfaceView getView() {
		return mGLView;
	}

	@Override
	public void onPause() {
		super.onPause();
		// The following call pauses the rendering thread.
		// If your OpenGL application is memory intensive,
		// you should consider de-allocating objects that
		// consume significant memory here.
		mGLView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		// The following call resumes a paused rendering thread.
		// If you de-allocated graphic objects for onPause()
		// this is a good place to re-allocate them.
		mGLView.onResume();
	}

}