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
import no.nordicsemi.android.nrftoolbox.dfu.DfuService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * When cancel button is pressed during uploading this fragment shows uploading cancel dialog
 */
public class UploadCancelFragment extends DialogFragment {
	private static final String TAG = "UploadCancelFragment";

	private CancelFragmentListener mListener;

	public interface CancelFragmentListener {
		public void onCancelUpload();
	}

	public static UploadCancelFragment getInstance() {
		final UploadCancelFragment fragment = new UploadCancelFragment();
		return fragment;
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (CancelFragmentListener) activity;
		} catch (final ClassCastException e) {
			Log.d(TAG, "The parent Activity must implement CancelFragmentListener interface");
		}
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity()).setTitle(R.string.dfu_confirmation_dialog_title).setMessage(R.string.dfu_upload_dialog_cancel_message).setCancelable(false)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int whichButton) {
						final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
						final Intent pauseAction = new Intent(DfuService.BROADCAST_ACTION);
						pauseAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_ABORT);
						manager.sendBroadcast(pauseAction);

						mListener.onCancelUpload();
					}
				}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.cancel();
					}
				}).create();
	}

	@Override
	public void onCancel(final DialogInterface dialog) {
		final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity());
		final Intent pauseAction = new Intent(DfuService.BROADCAST_ACTION);
		pauseAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_RESUME);
		manager.sendBroadcast(pauseAction);
	}
}
