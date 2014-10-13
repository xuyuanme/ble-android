/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.utility;

import no.nordicsemi.android.nrftoolbox.BuildConfig;
import android.util.Log;

public class DebugLogger {
	public static void v(final String tag, final String text) {
		if (BuildConfig.DEBUG)
			Log.v(tag, text);
	}

	public static void d(String tag, String text) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, text);
		}
	}

	public static void i(final String tag, final String text) {
		if (BuildConfig.DEBUG)
			Log.i(tag, text);
	}

	public static void w(String tag, String text) {
		if (BuildConfig.DEBUG) {
			Log.w(tag, text);
		}
	}

	public static void e(final String tag, final String text) {
		if (BuildConfig.DEBUG)
			Log.e(tag, text);
	}

	public static void wtf(String tag, String text) {
		if (BuildConfig.DEBUG) {
			Log.wtf(tag, text);
		}
	}
}
