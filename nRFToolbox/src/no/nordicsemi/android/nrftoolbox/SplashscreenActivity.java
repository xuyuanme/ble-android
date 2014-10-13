/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashscreenActivity extends Activity {
	/** Splash screen duration time in milliseconds */
	private static final int DELAY = 1000;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);

		// Jump to SensorsActivity after DELAY milliseconds 
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				final Intent intent = new Intent(SplashscreenActivity.this, FeaturesActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				finish();
			}
		}, DELAY);
	}

	@Override
	public void onBackPressed() {
		// do nothing. Protect from exiting the application when splash screen is shown
	}

}
