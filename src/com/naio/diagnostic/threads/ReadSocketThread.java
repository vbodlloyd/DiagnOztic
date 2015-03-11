package com.naio.diagnostic.threads;

import java.io.IOException;
import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.MemoryBuffer;
import com.naio.diagnostic.utils.NewMemoryBuffer;
import com.naio.net.NetClient;

public class ReadSocketThread extends Thread {

	private NetClient netClient;
	private boolean stop;
	private final Object lock1 = new Object();
	private final Object lock2 = new Object();
	public ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
	private MemoryBuffer memoryBuffer;
	private int port;
	private NewMemoryBuffer newmemoryBuffer;

	public ReadSocketThread(MemoryBuffer memoryBuffer, int port) {
		this.port = port;
		this.memoryBuffer = memoryBuffer;
		queue = new ConcurrentLinkedQueue<String>();
		this.stop = true;
	}

	public ReadSocketThread(NewMemoryBuffer memoryBuffer, int port) {
		this.port = port;
		this.newmemoryBuffer = memoryBuffer;
		queue = new ConcurrentLinkedQueue<String>();
		this.stop = true;
	}

	public void run() {
		int charsRead = 0;
		byte[] buffer = new byte[Config.BUFFER_SIZE];
		if (port == Config.PORT_LOG)
			netClient = new NetClient(Config.HOST2, port, "0");
		else
			netClient = new NetClient(Config.HOST, port, "0");
		netClient.connectWithServer();
		try {
			while (this.stop) {
				if (netClient.getIn() != null) {
					if ((charsRead = netClient.getIn().read(buffer)) != -1) {
						
						if (memoryBuffer == null) {
							newmemoryBuffer.addToFifo(buffer, charsRead);
						} else {
							memoryBuffer.addToFifo(buffer, charsRead);
						}
						try {
							Thread.sleep(0, 10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						if(charsRead == -1){
							Log.e("states","close");
							netClient.disConnectWithServer();
							stop = false;
						}
					}
				} else {
					//Log.e("socket", "pas de in");
					/*try {
						Thread.sleep(1, 10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param stop
	 *            the stop to set
	 */
	public void setStop(boolean stop) {
		synchronized (lock2) {
			this.stop = stop;
		}

	}
}
