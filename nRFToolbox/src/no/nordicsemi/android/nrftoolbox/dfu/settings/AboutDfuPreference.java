package no.nordicsemi.android.nrftoolbox.dfu.settings;

import no.nordicsemi.android.nrftoolbox.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

public class AboutDfuPreference extends Preference {

	public AboutDfuPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AboutDfuPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onClick() {
		final Context context = getContext();
		final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://devzone.nordicsemi.com/documentation/nrf51/6.1.0/s110/html/a00056.html"));
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// is browser installed?
		if (intent.resolveActivity(context.getPackageManager()) != null)
			context.startActivity(intent);
		else {
			Toast.makeText(getContext(), R.string.no_application, Toast.LENGTH_LONG).show();
		}
	}
}
