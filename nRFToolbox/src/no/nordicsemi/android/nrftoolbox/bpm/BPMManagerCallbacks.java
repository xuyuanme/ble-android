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

import no.nordicsemi.android.nrftoolbox.profile.BleManagerCallbacks;

public interface BPMManagerCallbacks extends BleManagerCallbacks {
	public static final int UNIT_mmHG = 0;
	public static final int UNIT_kPa = 1;

	/**
	 * Called when the Blood Pressure Measurement characteristic indication has been enabled
	 */
	public void onBloodPressureMeasurementIndicationsEnabled();

	/**
	 * Called when the Intermediate Cuff Pressure characteristic notification has been enabled
	 */
	public void onIntermediateCuffPressureNotificationEnabled();

	/**
	 * Called when new BPM value has been obtained from the sensor
	 * 
	 * @param systolic
	 * @param diastolic
	 * @param meanArterialPressure
	 * @param unit
	 *            one of the following {@link #UNIT_kPa} or {@link #UNIT_mmHG}
	 */
	public void onBloodPressureMeasurmentRead(final float systolic, final float diastolic, final float meanArterialPressure, final int unit);

	/**
	 * Called when new ICP value has been obtained from the device
	 * 
	 * @param cuffPressure
	 * @param unit
	 *            one of the following {@link #UNIT_kPa} or {@link #UNIT_mmHG}
	 */
	public void onIntermediateCuffPressureRead(final float cuffPressure, final int unit);

	/**
	 * Called when new pulse rate value has been obtained from the device. If there was no pulse rate in the packet the parameter will be equal -1.0f
	 * 
	 * @param pulseRate
	 *            pulse rate or -1.0f
	 */
	public void onPulseRateRead(final float pulseRate);

	/**
	 * Called when the timestamp value has been read from the device. If there was no timestamp information the parameter will be <code>null</code>
	 * 
	 * @param calendar
	 *            the timestamp or <code>null</code>
	 */
	public void onTimestampRead(final Calendar calendar);
}
