/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.hrs;

import no.nordicsemi.android.nrftoolbox.profile.BleManagerCallbacks;

public interface HRSManagerCallbacks extends BleManagerCallbacks {

	/**
	 * Called when Heart Rate notifications has been enabled
	 */
	public void onHRNotificationEnabled();

	/**
	 * Called when the sensor position information has been obtained from the sensor
	 * 
	 * @param position
	 *            the sensor position
	 */
	public void onHRSensorPositionFound(String position);

	/**
	 * Called when new Heart Rate value has been obtained from the sensor
	 * 
	 * @param value
	 *            the new value
	 */
	public void onHRValueReceived(int value);
}
