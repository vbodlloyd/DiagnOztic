package com.naio.diagnostic.utils;

public class Config {

	public static final String HOST = "10.42.0.1";//"192.168.1.111";//"10.42.0.1";//debian/*"192.168.1.111";//joan*///"192.168.1.149";//moi//

	public static final int PORT_GPS = 3334;
	public static final int PORT_LIDAR = 3337;
	public static final int PORT_WATCHDOG = 0002;
	public static final int PORT_CAMERA = 0005;
	public static final int PORT_LOG = 4339;
	public static final int PORT_MOTORS = 3331;
	public static final int PORT_REMOTE = 3338;
	public static final int PORT_ACCELERO = 3334;
	public static final int PORT_MAGNETO = 3337;
	public static final int PORT_GYRO = 3331;
	public static final int PORT_ODO = 3335;
	public static final int PORT_ACTUATOR = 3345;
	public static final int PORT_SCREEN = 0000;
	public static final int PORT_LED = 0001;
	public static final int PORT_KEYPAD = 0003;
	public static final int PORT_SPEAKER = 0004;

	public static final int LENGHT_HEADER = 6;
	public static final int LENGHT_ID = 1;
	public static final int LENGHT_SIZE = 4;
	public static final int LENGHT_FULL_HEADER = 11;
	public static final int LENGHT_CHECKSUM = 4;

	public static final int LENGHT_TRAME_MOTORS = 2;
	public static final int LENGHT_TRAME_LOG = 752*480*3+32;
	public static final int LENGHT_TRAME_WATCHDOG = 32;
	public static final int LENGHT_TRAME_GPS = 43;
	public static final int LENGHT_TRAME_ODO = 4;
	public static final int LENGHT_TRAME_CAMERA = 752*480*3+32;
	public static final int LENGHT_TRAME_LIDAR = 813;
	public static final int LENGHT_TRAME_REMOTE = 14;
	public static final int LENGHT_TRAME_KEYPAD = 1;
	public static final int LENGHT_TRAME_LED = 1;
	public static final int LENGHT_TRAME_SCREEN = 32;
	public static final int LENGHT_TRAME_SPEAKER=6;
	public static final int LENGHT_TRAME_ACCELERO = 6;
	public static final int LENGHT_TRAME_ACTUATOR = 1;
	public static final int LENGHT_TRAME_GYRO = 6;
	public static final int LENGHT_TRAME_MAGNETO = 6;


	public static final int ID_MOTORS = 1;
	public static final int ID_LOG = 2;
	public static final int ID_WATCHDOG = 3;
	public static final int ID_GPS = 4;
	public static final int ID_ODO = 5;
	public static final int ID_CAMERA = 6;
	public static final int ID_LIDAR = 7;
	public static final int ID_REMOTE = 8;
	public static final int ID_ACCELERO = 9;
	public static final int ID_GYRO = 10;
	public static final int ID_MAGNETO = 11;
	public static final int ID_KEYPAD = 12;
	public static final int ID_SCREEN = 13;
	public static final int ID_SPEAKER = 14;
	public static final int ID_ACTUATOR = 15;
	public static final int ID_LED = 16;
	public static final int ID_SMS = 17;

	public static final int BUFFER_SIZE = 2048;
	
	public static final String FILE_SAVE_GPS = "bilan.naio";

	public static final int LINES_SIZE_IN_BYTES = 16;
	public static final int ID_BYTES_FOR_LINES = 2;

}
