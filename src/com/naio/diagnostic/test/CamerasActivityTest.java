package com.naio.diagnostic.test;

import com.naio.diagnostic.activities.CameraActivity;

import android.test.ActivityInstrumentationTestCase2;

public class CamerasActivityTest extends ActivityInstrumentationTestCase2<CameraActivity> {

	private CameraActivity mActivity;

	public CamerasActivityTest() {
		super( CameraActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		getInstrumentation().waitForIdleSync();
	}
	
	public void testPreconditions() {
		assertNotNull("mActivity is null", mActivity);
	}

}
