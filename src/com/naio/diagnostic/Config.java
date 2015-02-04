package com.naio.diagnostic;

public class Config {

	static final String HOST = "192.168.1.111";

	static final int PORT_GPS = 3334;
	static final int PORT_LIDAR = 7777;//3337
	static final int PORT_MOTORS = 3331;
	static final int PORT_REMOTE = 3338;
	static final int PORT_TEST = 4444;

	static final int LENGHT_HEADER = 6;
	static final int LENGHT_ID = 1;
	static final int LENGHT_SIZE = 4;
	static final int LENGHT_CHECKSUM = 4;

	static final int LENGHT_TRAME_GPS = 43;
	static final int LENGHT_TRAME_LIDAR = 813;
	static final int LENGHT_TRAME_REMOTE = 14;
	static final int LENGHT_TRAME_MOTORS = 2;

	static final int ID_GPS = 4;
	static final int ID_LIDAR = 7;
	static final int ID_MOTORS = 1;

	static final int BUFFER_SIZE = 2048;
}
