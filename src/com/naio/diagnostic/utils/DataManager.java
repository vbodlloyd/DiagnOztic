package com.naio.diagnostic.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;

public class DataManager {
	private  String points_position_oz;
	private  String metre_parcouru;
	private  String nombre_choux;
	public ConcurrentLinkedQueue<byte[]> fifoImage = new ConcurrentLinkedQueue<byte[]>();
	public ConcurrentLinkedQueue<ArrayList<float[][]>> fifoLines = new ConcurrentLinkedQueue<ArrayList<float[][]>>() ;

	private static DataManager instance;
	
	public static DataManager getInstance(){
		if(instance == null){
			instance = new DataManager();
		}
		return instance;
	}
	
	private DataManager(){
		super();
		points_position_oz = "";
		metre_parcouru = "";
		fifoImage = new ConcurrentLinkedQueue<byte[]>();
		fifoLines = new ConcurrentLinkedQueue<ArrayList<float[][]>>();
	}
	
	public void write_in_file(Context ctx) {
		File gpxfile = new File(ctx.getFilesDir(), Config.FILE_SAVE_GPS);
		Date date = new Date();
		FileWriter writer;
		try {
			writer = new FileWriter(gpxfile, true);
			writer.append(date.toString() + "-"+nombre_choux+"-" + metre_parcouru
					+ "-" + points_position_oz + "\n");
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	
	private String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

	public String getStringFromFile(Context ctx, String filePath) throws Exception {
		File fl = new File(ctx.getFilesDir(), filePath);
		FileInputStream fin = new FileInputStream(fl);
		String ret = convertStreamToString(fin);
		fin.close();
		return ret;
	}
	
	public void addPoints_position_oz(String points_position_oz){
		this.points_position_oz += points_position_oz;
	}
	
	
	/**
	 * @return the points_position_oz
	 */
	public String getPoints_position_oz() {
		return points_position_oz;
	}

	/**
	 * @param points_position_oz the points_position_oz to set
	 */
	public void setPoints_position_oz(String points_position_oz) {
		this.points_position_oz = points_position_oz;
	}

	/**
	 * @return the metre_parcouru
	 */
	public String getMetre_parcouru() {
		return metre_parcouru;
	}

	/**
	 * @param metre_parcouru the metre_parcouru to set
	 */
	public void setMetre_parcouru(String metre_parcouru) {
		this.metre_parcouru = metre_parcouru;
	}
	
	/**
	 * @return the nombre_choux
	 */
	public String getNombre_choux() {
		return nombre_choux;
	}

	/**
	 * @param nombre_choux the nombre_choux to set
	 */
	public void setNombre_choux(String nombre_choux) {
		this.nombre_choux = nombre_choux;
	}
	
	public byte[] getPollFifoImage() {
		for(int i=0; i< fifoImage.size() -1 ; i++){
			fifoImage.poll();
		}
		return fifoImage.peek();
	}
	
	public ArrayList<float[][]> getPollFifoLines() {
		for(int i=0; i< fifoLines.size() -1 ; i++){
			fifoLines.poll();
		}
		return fifoLines.peek();
	}
}
