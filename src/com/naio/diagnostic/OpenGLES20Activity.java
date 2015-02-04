package com.naio.diagnostic;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class OpenGLES20Activity extends Activity {

    private GLSurfaceView mGLView;
	private MemoryBuffer memoryBuffer;
	private ReadSocketThread readSocketThread;
	private Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		public void run() {
			read_the_queue();
		}
	};
	private TrameDecoder trameDecoder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
        trameDecoder = new TrameDecoder();
        memoryBuffer = new MemoryBuffer();
		readSocketThread = new ReadSocketThread(memoryBuffer,Config.PORT_LIDAR);
		readSocketThread.start();
		handler.postDelayed(runnable, 10);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
    
	private void read_the_queue() {
		LidarTrame lidar = (LidarTrame) trameDecoder.decode(memoryBuffer.getPollFifo());
		if (lidar != null) {
			((MyGLSurfaceView) mGLView).update_with_uint16(lidar.data_uint16());
		}
		handler.postDelayed(runnable, 64);//15FPS
		
	}
}