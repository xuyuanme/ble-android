/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.profile;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

public interface BleManagerCallbacks {

	/**
	 * Called when the device has been connected. This does not mean that the application may start communication. A service discovery will be handled automatically after this call. Service discovery
	 * may ends up with calling {@link #onServicesDiscovered()} or {@link #onDeviceNotSupported()} if required services have not been found.
	 */
	public void onDeviceConnected();

	/**
	 * Called when the device has disconnected (when the callback returned {@link BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)} with state DISCONNECTED.
	 */
	public void onDeviceDisconnected();

	/**
	 * Some profiles may use this method to notify user that the link was lost. You must call this method in youe Ble Manager instead of {@link #onDeviceDisconnected()} while you discover
	 * disconnection not initiated by the user.
	 */
	public void onLinklossOccur();

	/**
	 * Called when service discovery has finished and primary services has been found. The device is ready to operate. This method is not called if the primary, mandatory services were not found
	 * during service discovery. For example in the Blood Pressure Monitor, a Blood Pressure service is a primary service and Intermediate Cuff Pressure service is a optional secondary service.
	 * Existence of battery service is not notified by this call.
	 * 
	 * @param optionalServicesFound
	 *            if <code>true</code> the secondary services were also found on the device.
	 */
	public void onServicesDiscovered(final boolean optionalServicesFound);

	/**
	 * Called when battery value has been received from the device
	 * 
	 * @param value
	 *            the battery value in percent
	 */
	public void onBatteryValueReceived(final int value);

	/**
	 * Called when an {@link BluetoothGatt#GATT_INSUFFICIENT_AUTHENTICATION} error occurred and the device bond state is NOT_BONDED
	 */
	public void onBondingRequired();

	/**
	 * Called when the device has been successfully bonded
	 */
	public void onBonded();

	/**
	 * Called when a BLE error has occurred
	 * 
	 * @param message
	 *            the error message
	 * @param errorCode
	 *            the error code
	 */
	public void onError(final String message, final int errorCode);

	/**
	 * Called when service discovery has finished but the main services were not found on the device. This may occur when connecting to bonded device that does not support required services.
	 */
	public void onDeviceNotSupported();
}
