package com.naio.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class CircleMesh extends Mesh {
	private int points = 100;
	private float vertices[] = { 0.0f, 0.0f, 0.0f };
	private FloatBuffer vertBuff;

	public CircleMesh() {
		vertices = new float[(points + 1) * 3];
		for (int i = 3; i < (points + 1) * 3; i += 3) {
			double rad = (i * 360 / points * 3) * (3.14 / 180);
			vertices[i] = (float) Math.cos(rad);
			vertices[i + 1] = (float) Math.sin(rad);
			vertices[i + 2] = 0;
		}
		ByteBuffer bBuff = ByteBuffer.allocateDirect(vertices.length * 4);
		bBuff.order(ByteOrder.nativeOrder());
		vertBuff = bBuff.asFloatBuffer();
		vertBuff.put(vertices);
		vertBuff.position(0);

	}

	public void draw(GL10 gl) {
		gl.glPushMatrix();
		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CCW);
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		gl.glTranslatef(0, 0, 0.5f);
		// gl.glScalef(size, size, 1.0f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertBuff);
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		if (mColorBuffer != null) {
			// Enable the color array buffer to be used during rendering.
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
		}

		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, points / 2);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glPopMatrix();
	}

}
