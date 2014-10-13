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

import no.nordicsemi.android.nrftoolbox.profile.BleManager;
import no.nordicsemi.android.nrftoolbox.utility.DebugLogger;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BPMManager implements BleManager<BPMManagerCallbacks> {
	private final String TAG = "BPMManager";

	private BPMManagerCallbacks mCallbacks;
	private BluetoothGatt mBluetoothGatt;
	private Context mContext;

	public final static UUID BP_SERVICE_UUID = UUID.fromString("00001810-0000-1000-8000-00805f9b34fb");
	public final static UUID BATTERY_SERVICE = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
	/** Blood Pressure Measurement characteristic */
	private static final UUID BPM_CHARACTERISTIC_UUID = UUID.fromString("00002A35-0000-1000-8000-00805f9b34fb");
	/** Intermediate Cuff Pressure characteristic */
	private static final UUID ICP_CHARACTERISTIC_UUID = UUID.fromString("00002A36-0000-1000-8000-00805f9b34fb");
	/** Battery Level characteristic */
	private final static UUID BATTERY_LEVEL_CHARACTERISTIC = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
	/** Client configuration descriptor that will allow us to enable notifications and indications */
	private static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

	private final static String ERROR_CONNECTION_STATE_CHANGE = "Error on connection state change";
	private final static String ERROR_DISCOVERY_SERVICE = "Error on discovering services";
	private final static String ERROR_WRITE_DESCRIPTOR = "Error on writing descriptor";
	private final static String ERROR_AUTH_ERROR_WHILE_BONDED = "Phone has lost bonding information";

	private BluetoothGattCharacteristic mBPMCharacteristic, mICPCharacteristic, mBatteryCharacteristic;

	private static BPMManager managerInstance = null;

	/**
	 * Returns the singleton implementation of BPMManager
	 */
	public static synchronized BPMManager getBPMManager() {
		if (managerInstance == null) {
			managerInstance = new BPMManager();
		}
		return managerInstance;
	}

	/**
	 * Callbacks for activity {@link BPMActivity} that implements {@link BPMManagerCallbacks} interface activity use this method to register itself for receiving callbacks
	 */
	@Override
	public void setGattCallbacks(final BPMManagerCallbacks callbacks) {
		mCallbacks = callbacks;
	}

	@Override
	public void connect(final Context context, final BluetoothDevice device) {
		mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
		mContext = context;
	}

	@Override
	public void disconnect() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
		}
	}

	private BroadcastReceiver mBondingBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			final int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
			final int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);

			DebugLogger.d(TAG, "Bond state changed for: " + device.getAddress() + " new state: " + bondState + " previous: " + previousBondState);

			// skip other devices
			if (!device.getAddress().equals(mBluetoothGatt.getDevice().getAddress()))
				return;

			if (bondState == BluetoothDevice.BOND_BONDED) {
				// We've read Battery Level, now let'so enable ICP notifications or BPM indications 
				if (mICPCharacteristic != null)
					enableIntermediateCuffPressureNotification(mBluetoothGatt);
				else
					enableBloodPressureMeasurementIndication(mBluetoothGatt);

				mContext.unregisterReceiver(this);
				mCallbacks.onBonded();
			}
		}
	};

	/**
	 * BluetoothGatt callbacks for connection/disconnection, service discovery, receiving notification, etc
	 */
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (newState == BluetoothProfile.STATE_CONNECTED) {
					mCallbacks.onDeviceConnected();
					// start discovering services
					gatt.discoverServices();
				} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
					mCallbacks.onDeviceDisconnected();
					gatt.close();
				}
			} else {
				mCallbacks.onError(ERROR_CONNECTION_STATE_CHANGE, status);
			}
		}

		@Override
		public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				for (BluetoothGattService service : gatt.getServices()) {
					if (BP_SERVICE_UUID.equals(service.getUuid())) {
						mBPMCharacteristic = service.getCharacteristic(BPM_CHARACTERISTIC_UUID);
						mICPCharacteristic = service.getCharacteristic(ICP_CHARACTERISTIC_UUID);
					} else if (BATTERY_SERVICE.equals(service.getUuid())) {
						mBatteryCharacteristic = service.getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC);
					}
				}
				// Validate the device
				if (mBPMCharacteristic == null) {
					mCallbacks.onDeviceNotSupported();
					gatt.disconnect();
					return;
				}
				mCallbacks.onServicesDiscovered(mICPCharacteristic != null);

				// We have discovered services, let's start notifications and indications, one by one: battery, icp (if exists), bpm
				if (mBatteryCharacteristic != null) {
					readBatteryLevel(gatt);
				} else if (mICPCharacteristic != null) {
					enableIntermediateCuffPressureNotification(gatt);
				} else {
					enableBloodPressureMeasurementIndication(gatt);
				}
			} else {
				DebugLogger.e(TAG, "onServicesDiscovered error " + status);
				mCallbacks.onError(ERROR_DISCOVERY_SERVICE, status);
			}
		}

		@Override
		public void onCharacteristicRead(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (BATTERY_LEVEL_CHARACTERISTIC.equals(characteristic.getUuid())) {
					final int batteryValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
					mCallbacks.onBatteryValueReceived(batteryValue);

					// We've read Battery Level, now let'so enable ICP notifications or BPM indications 
					if (mICPCharacteristic != null)
						enableIntermediateCuffPressureNotification(gatt);
					else
						enableBloodPressureMeasurementIndication(gatt);
				}
			} else {
				mCallbacks.onError(ERROR_DISCOVERY_SERVICE, status);
			}
		}

		@Override
		public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
			// ICP or BPM characteristic returned value

			// first byte - flags
			int offset = 0;
			final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);
			// See BPMManagerCallbacks.UNIT_* for unit options
			final int unit = flags & 0x01;
			final boolean timestampPresent = (flags & 0x02) > 0;
			final boolean pulseRatePresent = (flags & 0x04) > 0;

			if (BPM_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
				// following bytes - systolic, diastolic and mean arterial pressure 
				final float systolic = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
				final float diastolic = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 2);
				final float meanArterialPressure = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 4);
				offset += 6;
				mCallbacks.onBloodPressureMeasurmentRead(systolic, diastolic, meanArterialPressure, unit);
			} else if (ICP_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
				// following bytes - cuff pressure. Diastolic and MAP are unused 
				final float cuffPressure = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
				offset += 6;
				mCallbacks.onIntermediateCuffPressureRead(cuffPressure, unit);
			}

			// parse timestamp if present
			if (timestampPresent) {
				final Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset));
				calendar.set(Calendar.MONTH, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2));
				calendar.set(Calendar.DAY_OF_MONTH, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 3));
				calendar.set(Calendar.HOUR_OF_DAY, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 4));
				calendar.set(Calendar.MINUTE, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 5));
				calendar.set(Calendar.SECOND, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 6));
				offset += 7;
				mCallbacks.onTimestampRead(calendar);
			} else
				mCallbacks.onTimestampRead(null);

			// parse pulse rate if present
			if (pulseRatePresent) {
				final float pulseRate = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
				offset += 2;
				mCallbacks.onPulseRateRead(pulseRate);
			} else
				mCallbacks.onPulseRateRead(-1.0f);
		}

		@Override
		public void onDescriptorWrite(final BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (BPM_CHARACTERISTIC_UUID.equals(descriptor.getCharacteristic().getUuid()))
					mCallbacks.onBloodPressureMeasurementIndicationsEnabled();

				if (ICP_CHARACTERISTIC_UUID.equals(descriptor.getCharacteristic().getUuid())) {
					mCallbacks.onIntermediateCuffPressureNotificationEnabled();
					enableBloodPressureMeasurementIndication(gatt);
				}
			} else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION) {
				if (gatt.getDevice().getBondState() == BluetoothDevice.BOND_NONE) {
					mCallbacks.onBondingRequired();

					final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
					mContext.registerReceiver(mBondingBroadcastReceiver, filter);
				} else {
					DebugLogger.w(TAG, ERROR_AUTH_ERROR_WHILE_BONDED);
					mCallbacks.onError(ERROR_AUTH_ERROR_WHILE_BONDED, status);
				}
			} else {
				mCallbacks.onError(ERROR_WRITE_DESCRIPTOR, status);
			}
		}

		/**
		 * Reads battery level on the device
		 */
		private void readBatteryLevel(final BluetoothGatt gatt) {
			DebugLogger.d(TAG, "readBatteryLevel()");
			gatt.readCharacteristic(mBatteryCharacteristic);
		}
	};

	/**
	 * Enabling notification on Intermediate Cuff Pressure Characteristic
	 */
	private void enableIntermediateCuffPressureNotification(final BluetoothGatt gatt) {
		DebugLogger.d(TAG, "enableIntermediateCuffPressureNotification()");
		gatt.setCharacteristicNotification(mICPCharacteristic, true);
		final BluetoothGattDescriptor descriptor = mICPCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		gatt.writeDescriptor(descriptor);
	}

	/**
	 * Enabling indications on Blood Pressure Measurement Characteristic
	 */
	private void enableBloodPressureMeasurementIndication(final BluetoothGatt gatt) {
		DebugLogger.d(TAG, "enableBloodPressureMeasurementIndication()");
		gatt.setCharacteristicNotification(mBPMCharacteristic, true);
		final BluetoothGattDescriptor descriptor = mBPMCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
		gatt.writeDescriptor(descriptor);
	}

	@Override
	public void closeBluetoothGatt() {
		try {
			mContext.unregisterReceiver(mBondingBroadcastReceiver);
		} catch (Exception e) {
			// the receiver must have been not registered or unregistered before
		}

		if (mBluetoothGatt != null) {
			mBluetoothGatt.close();
			mBPMCharacteristic = null;
			mBatteryCharacteristic = null;
			mBluetoothGatt = null;
		}
	}
}
