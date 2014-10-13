/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA. Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.scanner;

import android.bluetooth.BluetoothDevice;

public class ExtendedBluetoothDevice {
	public BluetoothDevice device;
	/** The name is not parsed by some Android devices, f.e. Sony Xperia Z1 with Android 4.3 (C6903). It needs to be parsed manually. */
	public String name;
	public int rssi;
	public boolean isBonded;

	public ExtendedBluetoothDevice(BluetoothDevice device, String name, int rssi, boolean isBonded) {
		this.device = device;
		this.name = name;
		this.rssi = rssi;
		this.isBonded = isBonded;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ExtendedBluetoothDevice) {
			final ExtendedBluetoothDevice that = (ExtendedBluetoothDevice) o;
			return device.getAddress().equals(that.device.getAddress());
		}
		return super.equals(o);
	}

	/**
	 * Class used as a temporary comparator to find the device in the List of {@link ExtendedBluetoothDevice}s. This must be done this way, because List#indexOf and List#contains use the parameter's
	 * equals method, not the object's from list. See {@link DeviceListAdapter#updateRssiOfBondedDevice(String, int)} for example
	 */
	public static class AddressComparator {
		public String address;

		@Override
		public boolean equals(Object o) {
			if (o instanceof ExtendedBluetoothDevice) {
				final ExtendedBluetoothDevice that = (ExtendedBluetoothDevice) o;
				return address.equals(that.device.getAddress());
			}
			return super.equals(o);
		}
	}
}
