package no.nordicsemi.android.nrftoolbox.csc;

import java.util.UUID;

import no.nordicsemi.android.nrftoolbox.R;
import no.nordicsemi.android.nrftoolbox.csc.settings.SettingsActivity;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileService;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileServiceReadyActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.widget.TextView;

public class CSCActivity extends BleProfileServiceReadyActivity<CSCService.CSCBinder> {
	private TextView mSpeedView;
	private TextView mCadenceView;
	private TextView mDistanceView;
	private TextView mDistanceUnitView;
	private TextView mTotalDistanceView;
	private TextView mGearRatioView;

	@Override
	protected void onCreateView(final Bundle savedInstanceState) {
		setContentView(R.layout.activity_feature_csc);
		setGui();
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

	private void setGui() {
		mSpeedView = (TextView) findViewById(R.id.speed);
		mCadenceView = (TextView) findViewById(R.id.cadence);
		mDistanceView = (TextView) findViewById(R.id.distance);
		mDistanceUnitView = (TextView) findViewById(R.id.distance_unit);
		mTotalDistanceView = (TextView) findViewById(R.id.distance_total);
		mGearRatioView = (TextView) findViewById(R.id.ratio);
	}

	@Override
	protected void setDefaultUI() {
		mSpeedView.setText(R.string.not_available_value);
		mCadenceView.setText(R.string.not_available_value);
		mDistanceView.setText(R.string.not_available_value);
		mDistanceUnitView.setText(R.string.csc_distance_unit_m);
		mTotalDistanceView.setText(R.string.not_available_value);
		mGearRatioView.setText(R.string.not_available_value);
	}

	@Override
	protected int getLoggerProfileTitle() {
		return R.string.csc_feature_title;
	}

	@Override
	protected int getDefaultDeviceName() {
		return R.string.csc_default_name;
	}

	@Override
	protected int getAboutTextId() {
		return R.string.csc_about_text;
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.csc_menu, menu);
		return true;
	}

	@Override
	protected boolean onOptionsItemSelected(final int itemId) {
		switch (itemId) {
		case R.id.action_settings:
			final Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		}
		return true;
	}

	@Override
	protected Class<? extends BleProfileService> getServiceClass() {
		return CSCService.class;
	}

	@Override
	protected UUID getFilterUUID() {
		return CSCManager.CYCLING_SPEED_AND_CADENCE_SERVICE_UUID;
	}

	@Override
	protected void onServiceBinded(final CSCService.CSCBinder binder) {
		// not used
	}

	@Override
	protected void onServiceUnbinded() {
		// not used
	}

	@Override
	public void onServicesDiscovered(final boolean optionalServicesFound) {
		// not used
	}

	private void onMeasurementReceived(final float speed, final float distance, final float totalDistance) {
		mSpeedView.setText(String.format("%.1f", speed));
		if (distance < 1000) { // 1 km in m
			mDistanceView.setText(String.format("%.0f", distance));
			mDistanceUnitView.setText(R.string.csc_distance_unit_m);
		} else {
			mDistanceView.setText(String.format("%.2f", distance / 1000.0f));
			mDistanceUnitView.setText(R.string.csc_distance_unit_km);
		}

		mTotalDistanceView.setText(String.format("%.2f", totalDistance / 1000.0f));
	}

	private void onGearRatioUpdate(final float ratio, final int cadence) {
		mGearRatioView.setText(String.format("%.1f", ratio));
		mCadenceView.setText(String.format("%d", cadence));
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();

			if (CSCService.BROADCAST_WHEEL_DATA.equals(action)) {
				final float speed = intent.getFloatExtra(CSCService.EXTRA_SPEED, 0.0f);
				final float distance = intent.getFloatExtra(CSCService.EXTRA_DISTANCE, CSCManagerCallbacks.NOT_AVAILABLE);
				final float totalDistance = intent.getFloatExtra(CSCService.EXTRA_TOTAL_DISTANCE, CSCManagerCallbacks.NOT_AVAILABLE);
				// Update GUI
				onMeasurementReceived(speed, distance, totalDistance);
			} else if (CSCService.BROADCAST_CRANK_DATA.equals(action)) {
				final float ratio = intent.getFloatExtra(CSCService.EXTRA_GEAR_RATIO, 0);
				final int cadence = intent.getIntExtra(CSCService.EXTRA_CADENCE, 0);
				// Update GUI
				onGearRatioUpdate(ratio, cadence);
			}
		}
	};

	private static IntentFilter makeIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CSCService.BROADCAST_WHEEL_DATA);
		intentFilter.addAction(CSCService.BROADCAST_CRANK_DATA);
		return intentFilter;
	}
}
