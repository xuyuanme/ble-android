/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.gls;

import no.nordicsemi.android.nrftoolbox.profile.BleManagerCallbacks;

public interface GlucoseManagerCallbacks extends BleManagerCallbacks {
	public static final int UNIT_mmHG = 0;
	public static final int UNIT_kPa = 1;

	public void onGlucoseMeasurementNotificationEnabled();

	public void onGlucoseMeasurementContextNotificationEnabled();

	public void onRecordAccessControlPointIndicationsEnabled();

	public void onOperationStarted();

	public void onOperationCompleted();

	public void onOperationFailed();

	public void onOperationAborted();

	public void onOperationNotSupported();

	public void onDatasetChanged();

	public void onNumberOfRecordsRequested(final int value);
}
