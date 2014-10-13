package no.nordicsemi.android.nrftoolbox.dfu;

import no.nordicsemi.android.nrftoolbox.scanner.ScannerFragment;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

/**
 * The activity is started only by a remote connected computer using ADB. It shows a list of DFU-supported devices in range and allows user to select target device. The HEX file will be uploaded to
 * selected device using {@link DfuService}.
 */
public class DfuInitiatorActivity extends Activity implements ScannerFragment.OnDeviceSelectedListener {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// The activity must be started with a path to the HEX file
		final Intent intent = getIntent();
		if (!intent.hasExtra(DfuService.EXTRA_FILE_PATH))
			finish();

		if (savedInstanceState == null) {
			final ScannerFragment fragment = ScannerFragment.getInstance(this, DfuService.DFU_SERVICE_UUID, true);
			fragment.show(getFragmentManager(), null);
		}
	}

	@Override
	public void onDeviceSelected(final BluetoothDevice device, final String name) {
		final Intent intent = getIntent();
		final String overwritenName = intent.getStringExtra(DfuService.EXTRA_DEVICE_NAME);
		final String path = intent.getStringExtra(DfuService.EXTRA_FILE_PATH);
		final String address = device.getAddress();
		final int type = intent.getIntExtra(DfuService.EXTRA_FILE_TYPE, DfuService.TYPE_AUTO);
		final String finalName = overwritenName == null ? name : overwritenName;

		// Start DFU service with data provided in the intent
		final Intent service = new Intent(this, DfuService.class);
		service.putExtra(DfuService.EXTRA_DEVICE_ADDRESS, address);
		service.putExtra(DfuService.EXTRA_DEVICE_NAME, finalName);
		if (intent.hasExtra(DfuService.EXTRA_FILE_TYPE))
			service.putExtra(DfuService.EXTRA_FILE_TYPE, type);
		service.putExtra(DfuService.EXTRA_FILE_PATH, path);
		startService(service);
		finish();
	}

	@Override
	public void onDialogCanceled() {
		finish();
	}
}
