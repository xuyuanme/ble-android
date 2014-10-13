/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.bpm;

import java.util.Calendar;
import java.util.UUID;

import no.nordicsemi.android.nrftoolbox.R;
import no.nordicsemi.android.nrftoolbox.profile.BleManager;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileActivity;
import android.os.Bundle;
import android.widget.TextView;

public class BPMActivity extends BleProfileActivity implements BPMManagerCallbacks {
	@SuppressWarnings("unused")
	private static final String TAG = "BPMActivity";

	private TextView mSystolicView;
	private TextView mSystolicUnitView;
	private TextView mDiastolicView;
	private TextView mDiastolicUnitView;
	private TextView mMeanAPView;
	private TextView mMeanAPUnitView;
	private TextView mPulseView;
	private TextView mTimestampView;

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_feature_bpm);
		setGUI();
	}

	private void setGUI() {
		mSystolicView = (TextView) findViewById(R.id.systolic);
		mSystolicUnitView = (TextView) findViewById(R.id.systolic_unit);
		mDiastolicView = (TextView) findViewById(R.id.diastolic);
		mDiastolicUnitView = (TextView) findViewById(R.id.diastolic_unit);
		mMeanAPView = (TextView) findViewById(R.id.mean_ap);
		mMeanAPUnitView = (TextView) findViewById(R.id.mean_ap_unit);
		mPulseView = (TextView) findViewById(R.id.pulse);
		mTimestampView = (TextView) findViewById(R.id.timestamp);
	}

	@Override
	protected int getDefaultDeviceName() {
		return R.string.bpm_default_name;
	}

	@Override
	protected int getAboutTextId() {
		return R.string.bpm_about_text;
	}

	@Override
	protected UUID getFilterUUID() {
		return BPMManager.BP_SERVICE_UUID;
	}

	@Override
	protected BleManager<BPMManagerCallbacks> initializeManager() {
		final BPMManager manager = BPMManager.getBPMManager();
		manager.setGattCallbacks(this);
		return manager;
	}

	@Override
	protected void setDefaultUI() {
		mSystolicView.setText(R.string.not_available_value);
		mSystolicUnitView.setText(null);
		mDiastolicView.setText(R.string.not_available_value);
		mDiastolicUnitView.setText(null);
		mMeanAPView.setText(R.string.not_available_value);
		mMeanAPUnitView.setText(null);
		mPulseView.setText(R.string.not_available_value);
		mTimestampView.setText(R.string.not_available);
	}

	@Override
	public void onServicesDiscovered(final boolean optionalServicesFound) {
		// this may notify user or show some views
	}

	@Override
	public void onBloodPressureMeasurementIndicationsEnabled() {
		// this may notify user
	}

	@Override
	public void onIntermediateCuffPressureNotificationEnabled() {
		// this may notify user
	}

	@Override
	public void onBloodPressureMeasurmentRead(final float systolic, final float diastolic, final float meanArterialPressure, final int unit) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mSystolicView.setText(Float.toString(systolic));
				mDiastolicView.setText(Float.toString(diastolic));
				mMeanAPView.setText(Float.toString(meanArterialPressure));

				mSystolicUnitView.setText(unit == UNIT_mmHG ? R.string.bpm_unit_mmhg : R.string.bpm_unit_kpa);
				mDiastolicUnitView.setText(unit == UNIT_mmHG ? R.string.bpm_unit_mmhg : R.string.bpm_unit_kpa);
				mMeanAPUnitView.setText(unit == UNIT_mmHG ? R.string.bpm_unit_mmhg : R.string.bpm_unit_kpa);
			}
		});
	}

	@Override
	public void onIntermediateCuffPressureRead(final float cuffPressure, final int unit) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mSystolicView.setText(Float.toString(cuffPressure));
				mDiastolicView.setText(R.string.not_available_value);
				mMeanAPView.setText(R.string.not_available_value);

				mSystolicUnitView.setText(unit == UNIT_mmHG ? R.string.bpm_unit_mmhg : R.string.bpm_unit_kpa);
				mDiastolicUnitView.setText(null);
				mMeanAPUnitView.setText(null);
			}
		});
	}

	@Override
	public void onPulseRateRead(final float pulseRate) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (pulseRate >= 0)
					mPulseView.setText(Float.toString(pulseRate));
				else
					mPulseView.setText(R.string.not_available_value);
			}
		});
	}

	@Override
	public void onTimestampRead(final Calendar calendar) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (calendar != null)
					mTimestampView.setText(getString(R.string.bpm_timestamp, calendar));
				else
					mTimestampView.setText(R.string.not_available);
			}
		});
	}
}
