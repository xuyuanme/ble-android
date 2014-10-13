/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.dfu.adapter;

import no.nordicsemi.android.nrftoolbox.R;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This adapter displays some file browser applications that can be used to select HEX file. It is used when there is no such app already installed on the device. The hardcoded apps and Google Play
 * URLs are specified in res/values/strings_dfu.xml.
 */
public class FileBrowserAppsAdapter extends BaseAdapter {
	private final LayoutInflater mInflater;
	private final Resources mResources;

	public FileBrowserAppsAdapter(final Context context) {
		mInflater = LayoutInflater.from(context);
		mResources = context.getResources();
	}

	@Override
	public int getCount() {
		return mResources.getStringArray(R.array.dfu_app_file_browser).length;
	}

	@Override
	public Object getItem(int position) {
		return mResources.getStringArray(R.array.dfu_app_file_browser_action)[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = mInflater.inflate(R.layout.app_file_browser_item, parent, false);
		}

		final TextView item = (TextView) view;
		item.setText(mResources.getStringArray(R.array.dfu_app_file_browser)[position]);
		item.getCompoundDrawablesRelative()[0].setLevel(position);
		return view;
	}
}
