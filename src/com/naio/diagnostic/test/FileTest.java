package com.naio.diagnostic.test;

import com.naio.diagnostic.activities.HubActivity;
import com.naio.diagnostic.utils.Config;
import com.naio.diagnostic.utils.DataManager;

import android.test.ActivityInstrumentationTestCase2;

public class FileTest extends ActivityInstrumentationTestCase2<HubActivity> {

	private HubActivity mActivity;

	public FileTest() {
		super(HubActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
	}

	public void testWriteRead() {
		String file = "";
		DataManager.getInstance().write_in_file(mActivity);
		try {
			file = DataManager.getInstance().getStringFromFile(mActivity,
					Config.FILE_SAVE_GPS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(file.length() >= 1);
	}

}
