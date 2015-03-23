package com.naio.diagnostic.test;

import java.util.Date;

import com.naio.diagnostic.activities.HubActivity;
import com.naio.diagnostic.trames.GPSTrame;
import com.naio.diagnostic.trames.GyroTrame;
import com.naio.diagnostic.trames.OdoTrame;

import android.test.ActivityInstrumentationTestCase2;

public class TrameTest extends ActivityInstrumentationTestCase2<HubActivity> {

	private static final byte[] NAIO01 = new byte[] { 0x4E, 0x41, 0x49, 0x4F,
			0x30, 0x31 };
	
	// id + size+ payload + checksum for each
	private static final byte[] PAYLOAD_ODO = new byte[] { 0x5, 0x4, 0x0, 0x0,
			0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x0, 0x0, 0x0 };
	private static final byte[] PAYLOAD_GYRO = new byte[] { 0xA, 0x6, 0x0, 0x0,
			0x0, 0x1, 0x0, 0x2, 0x0, 0x3, 0x0, 0x0, 0x0, 0x0, 0x0 };
	private static final byte[] PAYLOAD_GPS = new byte[] { 0x4, 0xB, 0x2, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x5, 0x5, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x2, 0x2, 0x0, 0x0, 0x3, 0x3, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };

	@SuppressWarnings("deprecation")
	public TrameTest() {
		super("com.naio.diagnostic.activities", HubActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testOdoTrame() {
		byte[] odoTrameByte = new byte[NAIO01.length + PAYLOAD_ODO.length];
		System.arraycopy(NAIO01, 0, odoTrameByte, 0, NAIO01.length);
		System.arraycopy(PAYLOAD_ODO, 0, odoTrameByte, NAIO01.length,
				PAYLOAD_ODO.length);
		OdoTrame odoTrame = new OdoTrame(odoTrameByte);
		assertEquals(odoTrame.show(), "front right:" + "1" + " rear right:"
				+ "0" + " rear left:" + "1" + " front left:" + "0");
	}

	public void testGyroTrame() {
		byte[] odoTrameByte = new byte[NAIO01.length + PAYLOAD_GYRO.length];
		System.arraycopy(NAIO01, 0, odoTrameByte, 0, NAIO01.length);
		System.arraycopy(PAYLOAD_GYRO, 0, odoTrameByte, NAIO01.length,
				PAYLOAD_GYRO.length);
		GyroTrame gyroTrame = new GyroTrame(odoTrameByte);
		assertEquals(gyroTrame.show(), "x:" + "1" + "___y:" + "2" + "___z:"
				+ "3");
	}

	public void testGPSTrame() {
		byte[] odoTrameByte = new byte[NAIO01.length + PAYLOAD_GPS.length];
		System.arraycopy(NAIO01, 0, odoTrameByte, 0, NAIO01.length);
		System.arraycopy(PAYLOAD_GPS, 0, odoTrameByte, NAIO01.length,
				PAYLOAD_GPS.length);
		GPSTrame gpsTrame = new GPSTrame(odoTrameByte);
		Date date = new Date((long) (0));
		assertEquals(gpsTrame.show(), "" + date.toString() + "___lat:"
				+ "6.35E-321" + "___lon:"
				+ "4.188310413025727E-309" + "__alt:"
				+ "0.0" + "__unit:" + "0"
				+ "___nbrSat:" + "0" + "___quality:" +"0"
				+ "___groundSpeed:"
				+ "0.0");
	}

}
