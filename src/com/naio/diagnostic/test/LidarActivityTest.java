package com.naio.diagnostic.test;

import com.naio.diagnostic.R;
import com.naio.diagnostic.activities.LidarGPSMotorsActivity;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.LinearLayout;

public class LidarActivityTest extends ActivityInstrumentationTestCase2<LidarGPSMotorsActivity> {



	private LidarGPSMotorsActivity mActivity;
	private LinearLayout ll_dpad;

	public LidarActivityTest() {
		super( LidarGPSMotorsActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		ll_dpad = (LinearLayout) mActivity.findViewById(R.id.dpadview);
	}
	
	public void testPreconditions() {
		assertNotNull("mActivity is null", mActivity);
		assertNotNull("ll_dpad is null", ll_dpad);
	}
	
	public void testButtonPad(){
		Solo han = new Solo(getInstrumentation(),mActivity);
		han.clickOnButton("C");
		getInstrumentation().waitForIdleSync();
		assertTrue("Could not find the activity!", ll_dpad.isShown());	
		han.goBack();
	}
	
	public void testButtonAnalog(){
		Solo han = new Solo(getInstrumentation(),mActivity);
		han.clickOnButton("C");
		getInstrumentation().waitForIdleSync();
		han.clickOnButton("C");
		getInstrumentation().waitForIdleSync();
		assertFalse("Could not find the activity!", ll_dpad.isShown());	
		han.goBack();
	}
}
