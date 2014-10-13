/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.dfu.fragment;

import no.nordicsemi.android.nrftoolbox.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class ZipInfoFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_zip_info, null);
		return new AlertDialog.Builder(getActivity()).setView(view).setTitle(R.string.dfu_file_info).setPositiveButton(android.R.string.ok, null).create();
	}
}
