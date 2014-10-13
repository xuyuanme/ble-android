/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.hts;

import java.text.DecimalFormat;
import java.util.UUID;

import no.nordicsemi.android.nrftoolbox.R;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileService;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileServiceReadyActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

/**
 * HTSActivity is the main Health Thermometer activity. It implements {@link HTSManagerCallbacks} to receive callbacks from {@link HTSManager} class. The activity supports portrait and landscape
 * orientations.
 */
public class HTSActivity extends BleProfileServiceReadyActivity<HTSService.RSCBinder> {
	@SuppressWarnings("unused")
	private final String TAG = "HTSActivity";

	private TextView mHTSValue;

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_feature_hts);
		setGUI();
	}

	@Override
	protected void onInitialize() {
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, makeIntentFilter());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
	}

	private void setGUI() {
		mHTSValue = (TextView) findViewById(R.id.text_hts_value);
	}

	@Override
	protected void onServiceBinded(final HTSService.RSCBinder binder) {
		// not used
	}

	@Override
	protected void onServiceUnbinded() {
		// not used
	}

	@Override
	protected int getAboutTextId() {
		return R.string.hts_about_text;
	}

	@Override
	protected int getDefaultDeviceName() {
		return R.string.hts_default_name;
	}

	@Override
	protected UUID getFilterUUID() {
		return HTSManager.HT_SERVICE_UUID;
	}

	@Override
	protected Class<? extends BleProfileService> getServiceClass() {
		return HTSService.class;
	}

	@Override
	public void onServicesDiscovered(boolean optionalServicesFound) {
		// this may notify user or show some views
	}

	private void setHTSValueOnView(final double value) {
		DecimalFormat formatedTemp = new DecimalFormat("#0.00");
		mHTSValue.setText(formatedTemp.format(value));
	}

	@Override
	protected void setDefaultUI() {
		mHTSValue.setText(R.string.not_available_value);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();

			if (HTSService.BROADCAST_HTS_MEASUREMENT.equals(action)) {
				final double value = intent.getDoubleExtra(HTSService.EXTRA_TEMPERATURE, 0.0f);
				// Update GUI
				setHTSValueOnView(value);
			}
		}
	};

	private static IntentFilter makeIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(HTSService.BROADCAST_HTS_MEASUREMENT);
		return intentFilter;
	}
}
