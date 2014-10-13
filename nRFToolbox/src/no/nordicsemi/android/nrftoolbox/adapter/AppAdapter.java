/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.adapter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import no.nordicsemi.android.nrftoolbox.R;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends BaseAdapter {
	private static final String CATEGORY = "no.nordicsemi.android.nrftoolbox.LAUNCHER";
	private static final String MCP_PACKAGE = "no.nordicsemi.android.mcp";

	private final Context mContext;
	private final PackageManager mPackageManager;
	private final LayoutInflater mInflater;
	private final List<ResolveInfo> mApplications;

	public AppAdapter(final Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);

		// get nRF installed app plugins from package manager
		final PackageManager pm = mPackageManager = context.getPackageManager();
		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(CATEGORY);

		final List<ResolveInfo> appList = mApplications = pm.queryIntentActivities(intent, 0);
		// TODO remove the following loop after some time, when there will be no more MCP 1.1 at the market.
		for (final ResolveInfo info : appList) {
			if (MCP_PACKAGE.equals(info.activityInfo.packageName)) {
				appList.remove(info);
				break;
			}
		}
		Collections.sort(appList, new ResolveInfo.DisplayNameComparator(pm));
	}

	@Override
	public int getCount() {
		return mApplications.size();
	}

	@Override
	public Object getItem(int position) {
		return mApplications.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = mInflater.inflate(R.layout.feature_icon, parent, false);

			final ViewHolder holder = new ViewHolder();
			holder.view = view;
			holder.icon = (ImageView) view.findViewById(R.id.icon);
			holder.label = (TextView) view.findViewById(R.id.label);
			view.setTag(holder);
		}

		final ResolveInfo info = mApplications.get(position);
		final PackageManager pm = mPackageManager;

		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.icon.setImageDrawable(info.loadIcon(pm));
		holder.label.setText(info.loadLabel(pm).toString().toUpperCase(Locale.US));
		holder.view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent intent = new Intent();
				intent.setComponent(new ComponentName(info.activityInfo.packageName, info.activityInfo.name));
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				mContext.startActivity(intent);
			}
		});

		return view;
	}

	private class ViewHolder {
		private View view;
		private ImageView icon;
		private TextView label;
	}
}
