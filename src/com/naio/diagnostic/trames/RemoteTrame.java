package com.naio.diagnostic.trames;

import com.naio.diagnostic.utils.Config;

public class RemoteTrame extends Trame {
	private boolean instantiate;
	private byte leftAnalogX;
	private byte leftAnalogY;
	private byte rightAnalogX;
	private byte rightAnalogY;
	private byte buttons;
	private byte l1l3r1r3;
	private byte l2;
	private byte r2;
	private byte accelX;
	private byte accelY;
	private byte accelZ;
	private byte gyroX;
	private byte gyroY;
	private byte gyroZ;
	
	public RemoteTrame(byte[] data){
		super(data);
		instantiate = true;
		int offset = Config.LENGHT_FULL_HEADER;
		leftAnalogX = data[offset];
		leftAnalogY = data[offset +1];
		rightAnalogX = data[offset +2];
		rightAnalogY = data[offset +3];
		buttons = data[offset +4];
		l1l3r1r3 = data[offset +5];
		l2 = data[offset +6];
		r2 = data[offset +7];
		accelX = data[offset +8];
		accelY = data[offset +9];
		accelZ = data[offset +10];
		gyroX = data[offset +11];
		gyroY = data[offset +12];
		gyroZ = data[offset +13];
		
	}
	
	
}
