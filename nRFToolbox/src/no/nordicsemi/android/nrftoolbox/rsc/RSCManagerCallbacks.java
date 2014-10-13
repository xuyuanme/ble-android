/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.rsc;

import no.nordicsemi.android.nrftoolbox.profile.BleManagerCallbacks;

public interface RSCManagerCallbacks extends BleManagerCallbacks {
	public static final int NOT_AVAILABLE = -1;
	public static final int ACTIVITY_WALKING = 0;
	public static final int ACTIVITY_RUNNING = 1;

	public void onMeasurementReceived(float speed, int cadence, float distance, float strideLen, int activity);
}
