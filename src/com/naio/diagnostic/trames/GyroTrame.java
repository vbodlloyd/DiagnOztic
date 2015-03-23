package com.naio.diagnostic.trames;

import java.nio.ByteBuffer;

import android.util.Log;

import com.naio.diagnostic.utils.Config;

public class GyroTrame extends Trame {

	private byte[] gyro_z;
	private byte[] gyro_y;
	private byte[] gyro_x;
	private boolean instantiate;

	public GyroTrame(byte[] data) {
		super(data);
		instantiate = true;
		int offset = Config.LENGHT_FULL_HEADER;
		gyro_x = new byte[] { data[offset + 1], data[offset] };
		gyro_y = new byte[] { data[offset + 3], data[offset + 2] };
		gyro_z = new byte[] { data[offset + 5], data[offset + 4] };
	}

	public String show() {
		if (instantiate) {
			return "x:" + String.valueOf(ByteBuffer.wrap(gyro_x).getShort(0)) + "___y:"
					+ String.valueOf(ByteBuffer.wrap(gyro_y).getShort(0)) + "___z:"
					+ String.valueOf(ByteBuffer.wrap(gyro_z).getShort(0));
		}
		return null;
	}

}
