package com.naio.diagnostic.trames;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import net.sourceforge.juint.UInt8;

import android.util.Log;

import com.naio.diagnostic.utils.Config;

public class LogTrame extends Trame {

	private byte[] message;
	private byte type;
	private byte[] ligne_a_x = new byte[8];
	private byte[] ligne_a_y = new byte[8];
	private byte[] ligne_b_x = new byte[8];
	private byte[] ligne_b_y = new byte[8];

	public LogTrame(byte[] data) {
		super(data);
		int offset = data[Config.LENGHT_HEADER + Config.LENGHT_ID];
		/*setSize(new byte[] { data[offset + 3], data[offset + 2],
				data[offset + 1], data[offset] });*/
		type = data[Config.LENGHT_FULL_HEADER];
		Log.e("type", "something here");
		if (type == 1) {
			offset = Config.LENGHT_FULL_HEADER + 1;
			ligne_a_x = new byte[] { data[offset + 7], data[offset + 6],
					data[offset + 5], data[offset + 4], data[offset + 3],
					data[offset + 2], data[offset + 1], data[offset] };
			ligne_a_y = new byte[] { data[offset + 15], data[offset + 14],
					data[offset + 13], data[offset + 12], data[offset + 11],
					data[offset + 10], data[offset + 9], data[offset + 8] };
			ligne_b_x = new byte[] { data[offset + 23], data[offset + 22],
					data[offset + 21], data[offset + 20], data[offset + 19],
					data[offset + 18], data[offset + 17], data[offset + 16] };
			ligne_b_y = new byte[] { data[offset + 31], data[offset + 30],
					data[offset + 29], data[offset + 28], data[offset + 27],
					data[offset + 26], data[offset + 25], data[offset + 24] };
		}
		if (type == 2) {
			Log.e("type", "good type ");
			try {
				FileOutputStream imageOutFile = new FileOutputStream(
						"/sdcard/DCIM/imagerecu.png");
				imageOutFile.write(Arrays.copyOfRange(data,
						Config.LENGHT_FULL_HEADER + 1, data.length
								- Config.LENGHT_CHECKSUM));
				imageOutFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public double[] getPointA() {
		double[] points = new double[2];
		points[0] = ByteBuffer.wrap(ligne_a_x).getDouble(0);
		points[1] = ByteBuffer.wrap(ligne_a_y).getDouble(0);
		return points;
	}

	public double[] getPointB() {
		double[] points = new double[2];
		points[0] = ByteBuffer.wrap(ligne_b_x).getDouble(0);
		points[1] = ByteBuffer.wrap(ligne_b_y).getDouble(0);
		return points;
	}

	public double[][] getPoints() {
		double[][] points = new double[2][2];
		points[0] = getPointB();
		points[1] = getPointA();
		return points;
	}

}
