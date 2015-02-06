package com.naio.diagnostic.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.naio.opengl.Line;
import com.naio.opengl.Square;
import com.naio.opengl.Triangle;

import net.sourceforge.juint.UInt16;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class must
 * override the OpenGL ES drawing lifecycle methods:
 * <ul>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 * <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = "MyGLRenderer";
	private Triangle mTriangle;
	private Square mSquare;
	private UInt16[] position = new UInt16[180];

	/**
	 * @return the position
	 */
	public UInt16[] getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(UInt16[] position) {
		this.position = position;
	}

	// mMVPMatrix is an abbreviation for "Model View Projection Matrix"
	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];
	private final float[] mRotationMatrix = new float[16];

	private float mAngle;
	private Line mLine;

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {

		// Set the background frame color
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		mTriangle = new Triangle();
		mSquare = new Square();
		mLine = new Line();
		for (int i = 0; i < 180; i++) {
			position[i] = new UInt16(0);
		}
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		float[] scratch = new float[16];

		// Draw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// Set the camera position (View matrix)
		Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
		Matrix.translateM(mMVPMatrix, 0, 0, -1, 0);
		Matrix.scaleM(mMVPMatrix, 0, 0.125f, 0.5f, 1);
		mSquare.draw(mMVPMatrix);
		Matrix.scaleM(mMVPMatrix, 0, 8, 2, 1);
		Matrix.translateM(mMVPMatrix, 0, 0, 0.25f, 0);
		Matrix.scaleM(mMVPMatrix, 0, 0.02f, 0.02f, 0.02f);

		Matrix.rotateM(mMVPMatrix, 0, 181, 0, 0, 1);
		// Draw square

		float[] positionxy[] = new float[180][2];
		for (int i = 0; i < 180; i++) {
			Matrix.rotateM(mMVPMatrix, 0, -1, 0, 0, 1);
			if (position[i].floatValue() < 4000) {
				float dep = (float) (position[i].floatValue() / 1000.0f) * 20.0f;
				Matrix.translateM(mMVPMatrix, 0, dep, 0, 0);
				Matrix.scaleM(mMVPMatrix, 0, 0.5f, 0.5f, 0.5f);
				mSquare.draw(mMVPMatrix);
				Matrix.scaleM(mMVPMatrix, 0, 2, 2, 2);
				Matrix.translateM(mMVPMatrix, 0, -1.0f * dep, 0, 0);
				positionxy[i][0] = (float) (dep * Math.cos((180 - i) * Math.PI
						/ 180));
				positionxy[i][1] = (float) (dep * Math.sin((180 - i) * Math.PI
						/ 180));
			}
		}
		Matrix.rotateM(mMVPMatrix, 0, -1, 0, 0, 1);
		float previousX = 0.0f;
		float previousY = 0.0f;
		for (float[] fl : positionxy) {
			if (fl != null) {
				if (previousX != 0 && previousY != 0 && fl[0] != 0 && fl[1] != 0) {
					Line line = new Line();
					line.SetVerts(previousX, previousY, 0.0f, fl[0], fl[1],
							0.0f);
					line.draw(mMVPMatrix);
				}
				if (fl[0] != 0 && fl[1] != 0) {
					previousX = fl[0];
					previousY = fl[1];
				}
			}

		}

	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		// Adjust the viewport based on geometry changes,
		// such as screen rotation
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;

		// this projection matrix is applied to object coordinates
		// in the onDrawFrame() method
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

	}

	/**
	 * Utility method for compiling a OpenGL shader.
	 * 
	 * <p>
	 * <strong>Note:</strong> When developing shaders, use the checkGlError()
	 * method to debug shader coding errors.
	 * </p>
	 * 
	 * @param type
	 *            - Vertex or fragment shader type.
	 * @param shaderCode
	 *            - String containing the shader code.
	 * @return - Returns an id for the shader.
	 */
	public static int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	/**
	 * Utility method for debugging OpenGL calls. Provide the name of the call
	 * just after making it:
	 * 
	 * <pre>
	 * mColorHandle = GLES20.glGetUniformLocation(mProgram, &quot;vColor&quot;);
	 * MyGLRenderer.checkGlError(&quot;glGetUniformLocation&quot;);
	 * </pre>
	 * 
	 * If the operation is not successful, the check throws an error.
	 * 
	 * @param glOperation
	 *            - Name of the OpenGL call to check.
	 */
	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e(TAG, glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

	/**
	 * Returns the rotation angle of the triangle shape (mTriangle).
	 * 
	 * @return - A float representing the rotation angle.
	 */
	public float getAngle() {
		return mAngle;
	}

	/**
	 * Sets the rotation angle of the triangle shape (mTriangle).
	 */
	public void setAngle(float angle) {
		mAngle = angle;
	}

}