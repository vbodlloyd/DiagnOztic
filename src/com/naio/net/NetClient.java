package com.naio.net;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class NetClient {

    /**
     * Maximum size of buffer
     */
    public static final int BUFFER_SIZE = 2048;
    private Socket socket = null;
    private OutputStream out = null;
    private InputStream in = null;

    private String host = null;
    private String macAddress = null;
    private int port = 7999;


    /**
     * Constructor with Host, Port and MAC Address
     * @param host
     * @param port
     * @param macAddress
     */
    public NetClient(String host, int port, String macAddress) {
        this.host = host;
        this.port = port;
        this.macAddress = macAddress;
    }

    public void connectWithServer() {
        try {
            if (socket == null) {
                socket = new Socket(this.host, this.port);
                out = socket.getOutputStream();
                in = socket.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
	 * @return the out
	 */
	public OutputStream getOut() {
		return out;
	}

	/**
	 * @return the in
	 */
	public InputStream getIn() {
		return in;
	}

	private void disConnectWithServer() {
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    in.close();
                    out.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}