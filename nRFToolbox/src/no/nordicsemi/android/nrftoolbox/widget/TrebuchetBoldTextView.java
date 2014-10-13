/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.widget;

import no.nordicsemi.android.nrftoolbox.R;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TrebuchetBoldTextView extends TextView {

	public TrebuchetBoldTextView(Context context) {
		super(context);

		init();
	}

	public TrebuchetBoldTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public TrebuchetBoldTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	private final void init() {
		if (!isInEditMode()) {
			final Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.font_path));
			setTypeface(typeface);
		}
	}
}
