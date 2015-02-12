package com.naio.diagnostic.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.util.Log;

/**
 * Garde en mémoire la fin d'une trame non fini et la complete avec la suivante.
 * La classe contient une fifo contennant ces trames.
 * 
 * @author bodereau
 *
 */
public class MemoryBuffer { 
	public ConcurrentLinkedQueue<byte[]> fifo = new ConcurrentLinkedQueue<byte[]>();

	private byte[] memoryBytes = new byte []{};

	public void addToFifo(byte[] bytess, int bytesRead) {
		byte[] bytes =bytess.clone();
		
		int idx = 0;
		int pos = 0;
		int mem = 0;
		int nbrOfNaio = 0;
		int[] posOfNaio = new int[100];
		for (byte bit : bytes) {
			if (bit == 78) {
				mem = 1;
				pos = idx;
			} else if (bit == 65 && mem == 1) {
				mem = 2;
			} else if (bit == 73 && mem == 2) {
				mem = 3;
			} else if (bit == 79 && mem == 3) {
				mem = 4;
				nbrOfNaio += 1;
				posOfNaio[nbrOfNaio - 1] = pos;
			} else {
				mem = 0;
			}
			idx++;
			if(idx == bytesRead){
				break;
			}
		}
		if (memoryBytes.length > 0) { // changement orientation > <

			int position = posOfNaio[0];

			if (position == 0) {
			
				byte[] finalc = new byte[memoryBytes.length + bytesRead];
				System.arraycopy(memoryBytes, 0, finalc, 0, memoryBytes.length);
				System.arraycopy(bytes, 0, finalc, memoryBytes.length,
						bytesRead);
				memoryBytes = finalc;
		
			} else {
	
				byte[] o = Arrays.copyOfRange(bytes, 0, posOfNaio[0]);
				byte[] finalc = new byte[memoryBytes.length + o.length];

				System.arraycopy(memoryBytes, 0, finalc, 0, memoryBytes.length);
				System.arraycopy(o, 0, finalc, memoryBytes.length, o.length);

				fifo.offer(finalc);
				memoryBytes = new byte[]{};
		
			}
		}

		for (int i = 0; i < nbrOfNaio; i++) {
;
			int position = posOfNaio[i];
			byte[] b = Arrays
					.copyOfRange(bytes, position, bytesRead);
			
			if (b.length < Config.LENGHT_FULL_HEADER) {
				memoryBytes = b;

			} else {
				byte[] size = new byte[] { b[10], b[9], b[8], b[7] };
				int sizeInt = ByteBuffer.wrap(size).getInt();
				if (b.length < Config.LENGHT_FULL_HEADER + sizeInt + Config.LENGHT_CHECKSUM) {
					memoryBytes = b;

				} else {
		
					memoryBytes = new byte[]{};
					fifo.offer(b);

				}
			}
		}
		
		
	}

	public byte[] getPollFifo() {
		for(int i=0; i< fifo.size() -1 ; i++){
			fifo.poll();
		}
		return fifo.poll();
	}
}
