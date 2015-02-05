package com.naio.diagnostic.utils;

public class Config {

	public static final String HOST = "192.168.1.111";

	public static final int PORT_GPS = 3334;
	public static final int PORT_LIDAR = 7777;//3337
	public static final int PORT_MOTORS = 3331;
	public static final int PORT_REMOTE = 3338;
	public static final int PORT_TEST = 4444;

	public static final int LENGHT_HEADER = 6;
	public static final int LENGHT_ID = 1;
	public static final int LENGHT_SIZE = 4;
	public static final int LENGHT_CHECKSUM = 4;

	public static final int LENGHT_TRAME_GPS = 43;
	public static final int LENGHT_TRAME_LIDAR = 813;
	public static final int LENGHT_TRAME_REMOTE = 14;
	public static final int LENGHT_TRAME_MOTORS = 2;

	public static final int ID_GPS = 4;
	public static final int ID_LIDAR = 7;
	public static final int ID_MOTORS = 1;

	public static final int BUFFER_SIZE = 2048;
}
