package com.naio.diagnostic.utils;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.android.gms.maps.model.UrlTileProvider;

public class OnlineTileProvider extends UrlTileProvider {

	private static final String FORMAT;

	static {
		FORMAT = "http://otile1.mqcdn.com/tiles/1.0.0/sat/%s/%d/%d/%d.jpg";
	}

	// ------------------------------------------------------------------------
	// Instance Variables
	// ------------------------------------------------------------------------

	private String mMapIdentifier;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	public OnlineTileProvider(String mapIdentifier) {
		super(256, 256);

		this.mMapIdentifier = mapIdentifier;
	}

	// ------------------------------------------------------------------------
	// Public Methods
	// ------------------------------------------------------------------------

	@Override
	public URL getTileUrl(int x, int y, int z) {
		try {
			return new URL(String.format(FORMAT, this.mMapIdentifier, z, x, y));
		} catch (MalformedURLException e) {
			return null;
		}
	}

}