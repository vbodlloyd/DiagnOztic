package com.naio.diagnostic.test;

import com.naio.diagnostic.activities.HubActivity;
import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;

public class TransitionActivityTest extends ActivityInstrumentationTestCase2<HubActivity> {



	private HubActivity mActivity;

	public TransitionActivityTest() {
		super( HubActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
	}
	
	public void testPreconditions() {
		assertNotNull("mActivity is null", mActivity);
	}
	
	public void testTransitionHubToLidar(){
		Solo han = new Solo(getInstrumentation(),mActivity);
		han.clickOnText("Lidar - Gps - Control");
		getInstrumentation().waitForIdleSync();
		assertTrue("Could not find the activity!", han.searchText("Lidar Oz"));	
		han.goBack();
	}
	
	public void testTransitionHubToCameras(){
		Solo han = new Solo(getInstrumentation(),mActivity);
		han.clickOnText("Cameras");
		getInstrumentation().waitForIdleSync();
		assertTrue("Could not find the activity!", han.searchText("CameraActivity"));	
		han.goBack();
	}
	
	public void testTransitionHubToDecision(){
		Solo han = new Solo(getInstrumentation(),mActivity);
		han.clickOnText("Decision IA");
		getInstrumentation().waitForIdleSync();
		assertTrue("Could not find the activity!", han.searchText("DecisionIAActivity"));	
		han.goBack();
	}
	
	public void testTransitionHubToBilan(){
		Solo han = new Solo(getInstrumentation(),mActivity);
		han.clickOnText("Bilan utilisation");
		getInstrumentation().waitForIdleSync();
		assertTrue("Could not find the activity!", han.searchText("BilanUtilisationActivity"));	
		han.goBack();
	}
}
