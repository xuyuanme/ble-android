/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.hts;

import no.nordicsemi.android.nrftoolbox.profile.BleManagerCallbacks;

/**
 * Interface {@link HTSManagerCallbacks} must be implemented by {@link HTSActivity} in order to receive callbacks from {@link HTSManager}
 */
public interface HTSManagerCallbacks extends BleManagerCallbacks {

	/**
	 * Called when Health Thermometer value has been received
	 * 
	 * @param value
	 *            the new value
	 */
	public void onHTValueReceived(double value);

}
