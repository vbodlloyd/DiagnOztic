package com.naio.diagnostic.trames;

import java.nio.ByteBuffer;
import java.util.Date;

import com.naio.diagnostic.utils.Config;

public class AcceleroTrame extends Trame {

	private byte[] accel_x = new byte[2];
	private byte[] accel_y = new byte[2];
	private byte[] accel_z = new byte[2];
	private boolean instantiate;

	public AcceleroTrame(byte[] data) {
		super(data);
		if (data == null) {
			return;
		}
		instantiate = true;
		int offset = Config.LENGHT_FULL_HEADER;
		accel_x = new byte[] { data[offset + 1], data[offset] };
		accel_y = new byte[] { data[offset + 3], data[offset + 2] };
		accel_z = new byte[] { data[offset + 5], data[offset + 4] };

	}

	public String show() {
		if (instantiate) {

			return "x:" + ByteBuffer.wrap(accel_x).getChar(0) + "___y:"
					+ ByteBuffer.wrap(accel_y).getChar(0) + "___z:"
					+ ByteBuffer.wrap(accel_z).getChar(0);
		}
		return null;
	}

}
