package com.naio.diagnostic.trames;

import com.naio.diagnostic.utils.Config;

public class ChargeBatterieTrame extends Trame {

	private byte[] tension_24v = new byte[2];
	private byte[] tension_12v = new byte[2];
	private byte[] tension_5v = new byte[2];
	private byte[] tension_pile = new byte[2];
	private byte[] tension_33v = new byte[2];

	public ChargeBatterieTrame(byte[] data) {
		super(data);
		int offset = Config.LENGHT_FULL_HEADER;
		tension_24v = new byte[] { data[offset + 1], data[offset] };
		tension_pile = new byte[] { data[offset + 3], data[offset + 2] };
		tension_12v = new byte[] { data[offset + 5], data[offset + 4] };
		tension_33v = new byte[] { data[offset + 7], data[offset + 6] };
		tension_5v = new byte[] { data[offset + 9], data[offset + 8] };
	}
}
