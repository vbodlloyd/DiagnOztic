package com.naio.diagnostic.trames;

import java.nio.ByteBuffer;

import com.naio.diagnostic.utils.Config;

public class MagnetoTrame extends Trame {

	private byte[] magneto_x = new byte[2];
	private byte[] magneto_y = new byte[2];
	private byte[] magneto_z = new byte[2];
	private boolean instantiate;

	public MagnetoTrame(byte[] data) {
		super(data);
		if (data == null) {
			return;
		}
		instantiate = true;
		int offset = Config.LENGHT_FULL_HEADER;
		magneto_x = new byte[] { data[offset + 1], data[offset] };
		magneto_y = new byte[] { data[offset + 3], data[offset + 2] };
		magneto_z = new byte[] { data[offset + 5], data[offset + 4] };

	}

	public String show() {
		if (instantiate) {

			return "x:" + ByteBuffer.wrap(magneto_x).getChar(0) + "___y:"
					+ ByteBuffer.wrap(magneto_y).getChar(0) + "___z:"
					+ ByteBuffer.wrap(magneto_z).getChar(0);
		}
		return null;
	}
}
