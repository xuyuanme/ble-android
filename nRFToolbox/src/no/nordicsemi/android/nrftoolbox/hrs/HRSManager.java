/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.hrs;

import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.nrftoolbox.R;
import no.nordicsemi.android.nrftoolbox.profile.BleManager;
import no.nordicsemi.android.nrftoolbox.utility.DebugLogger;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

/**
 * HRSManager class performs BluetoothGatt operations for connection, service discovery, enabling notification and reading characteristics. All operations required to connect to device with BLE HR
 * Service and reading heart rate values are performed here. HRSActivity implements HRSManagerCallbacks in order to receive callbacks of BluetoothGatt operations
 */
public class HRSManager implements BleManager<HRSManagerCallbacks> {
	private final String TAG = "HRSManager";
	private HRSManagerCallbacks mCallbacks;
	private BluetoothGatt mBluetoothGatt;
	private Context mContext;

	public final static UUID HR_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb");
	private static final UUID HR_SENSOR_LOCATION_CHARACTERISTIC_UUID = UUID.fromString("00002A38-0000-1000-8000-00805f9b34fb");

	private static final UUID HR_CHARACTERISTIC_UUID = UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb");
	private static final UUID CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

	private final static UUID BATTERY_SERVICE = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
	private final static UUID BATTERY_LEVEL_CHARACTERISTIC = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");

	private final static String ERROR_CONNECTION_STATE_CHANGE = "Error on connection state change";
	private final static String ERROR_DISCOVERY_SERVICE = "Error on discovering services";
	private final static String ERROR_WRITE_DESCRIPTOR = "Error on writing descriptor";
	private final static String ERROR_READ_CHARACTERISTIC = "Error on reading characteristic";

	private BluetoothGattCharacteristic mHRCharacteristic, mHRLocationCharacteristic, mBatteryCharacteritsic;
	private static final int FIRST_BITMASK = 0x01;

	private static HRSManager managerInstance = null;

	/**
	 * singleton implementation of HRSManager class
	 */
	public static synchronized HRSManager getInstance(Context context) {
		if (managerInstance == null) {
			managerInstance = new HRSManager();
		}
		managerInstance.mContext = context;
		return managerInstance;
	}

	/**
	 * callbacks for activity {HRSActivity} that implements HRSManagerCallbacks interface activity use this method to register itself for receiving callbacks
	 */
	@Override
	public void setGattCallbacks(HRSManagerCallbacks callbacks) {
		mCallbacks = callbacks;
	}

	@Override
	public void connect(Context context, BluetoothDevice device) {
		DebugLogger.d(TAG, "Connecting to device...");
		mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
	}

	/**
	 * Disable HR notification first and then disconnect to HR device
	 */
	@Override
	public void disconnect() {
		DebugLogger.d(TAG, "Disconnecting device...");
		if (mBluetoothGatt != null) {
			mBluetoothGatt.disconnect();
		}
	}

	/**
	 * BluetoothGatt callbacks for connection/disconnection, service discovery, receiving notification, etc
	 */
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (newState == BluetoothProfile.STATE_CONNECTED) {
					DebugLogger.d(TAG, "Device connected");
					mBluetoothGatt.discoverServices();
					//This will send callback to HRSActivity when device get connected
					mCallbacks.onDeviceConnected();
				} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
					DebugLogger.d(TAG, "Device disconnected");
					//This will send callback to HRSActivity when device get disconnected
					mCallbacks.onDeviceDisconnected();
				}
			} else {
				mCallbacks.onError(ERROR_CONNECTION_STATE_CHANGE, status);
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				List<BluetoothGattService> services = gatt.getServices();
				for (BluetoothGattService service : services) {
					if (service.getUuid().equals(HR_SERVICE_UUID)) {
						mHRCharacteristic = service.getCharacteristic(HR_CHARACTERISTIC_UUID);
						mHRLocationCharacteristic = service.getCharacteristic(HR_SENSOR_LOCATION_CHARACTERISTIC_UUID);
					} else if (service.getUuid().equals(BATTERY_SERVICE)) {
						mBatteryCharacteritsic = service.getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC);
					}
				}
				if (mHRCharacteristic != null) {
					//This will send callback to HRSActicity when HR Service is found in device
					mCallbacks.onServicesDiscovered(false);
					readHRSensorLocation();
				} else {
					mCallbacks.onDeviceNotSupported();
					gatt.disconnect();
				}
			} else {
				mCallbacks.onError(ERROR_DISCOVERY_SERVICE, status);
			}
		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (characteristic.getUuid().equals(HR_SENSOR_LOCATION_CHARACTERISTIC_UUID)) {
					final String sensorPosition = getBodySensorPosition(characteristic.getValue()[0]);
					//This will send callback to HRSActicity when HR sensor position on body is found in HR device
					mCallbacks.onHRSensorPositionFound(sensorPosition);

					if (mBatteryCharacteritsic != null) {
						readBatteryLevel();
					} else {
						enableHRNotification();
					}
				}
				if (characteristic.getUuid().equals(BATTERY_LEVEL_CHARACTERISTIC)) {
					int batteryValue = characteristic.getValue()[0];
					//This will send callback to HRSActicity when Battery value is received from HR device
					mCallbacks.onBatteryValueReceived(batteryValue);

					enableHRNotification();
				}
			} else {
				mCallbacks.onError(ERROR_READ_CHARACTERISTIC, status);
			}
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			int hrValue = 0;
			//This will check if HR value is in 8 bits or 16 bits. 
			if (characteristic.getUuid().equals(HR_CHARACTERISTIC_UUID)) {
				if (isHeartRateInUINT16(characteristic.getValue()[0])) {
					hrValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
				} else {
					hrValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
				}
				//This will send callback to HRSActicity when new HR value is received from HR device
				mCallbacks.onHRValueReceived(hrValue);
			}
		}

		@Override
		public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				//This will send callback to HRSActicity when HR notification is enabled
				mCallbacks.onHRNotificationEnabled();
			} else {
				mCallbacks.onError(ERROR_WRITE_DESCRIPTOR, status);
			}
		}
	};

	private void readBatteryLevel() {
		if (mBatteryCharacteritsic != null) {
			mBluetoothGatt.readCharacteristic(mBatteryCharacteritsic);
		}
	}

	private void readHRSensorLocation() {
		if (mHRLocationCharacteristic != null) {
			mBluetoothGatt.readCharacteristic(mHRLocationCharacteristic);
		}
	}

	/**
	 * This method will decode and return Heart rate sensor position on body
	 */
	private String getBodySensorPosition(byte bodySensorPositionValue) {
		String[] locations = mContext.getResources().getStringArray(R.array.hrs_locations);
		if (bodySensorPositionValue > locations.length)
			return mContext.getString(R.string.hrs_location_other);
		return locations[bodySensorPositionValue];
	}

	/**
	 * This method will check if Heart rate value is in 8 bits or 16 bits
	 */
	private boolean isHeartRateInUINT16(byte value) {
		if ((value & FIRST_BITMASK) != 0)
			return true;
		return false;
	}

	/**
	 * Enabling notification on Heart Rate Characteristic
	 */
	private void enableHRNotification() {
		DebugLogger.d(TAG, "Enabling heart rate notifications");
		mBluetoothGatt.setCharacteristicNotification(mHRCharacteristic, true);
		BluetoothGattDescriptor descriptor = mHRCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID);
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		mBluetoothGatt.writeDescriptor(descriptor);
	}

	@Override
	public void closeBluetoothGatt() {
		if (mBluetoothGatt != null) {
			mBluetoothGatt.close();
			mBluetoothGatt = null;
		}
	}

}
