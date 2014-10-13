/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.gls;

import java.util.Calendar;

public class GlucoseRecord {
	public static final int UNIT_kgpl = 0;
	public static final int UNIT_molpl = 1;

	/** Record sequence number */
	protected int sequenceNumber;
	/** The base time of the measurement */
	protected Calendar time;
	/** Time offset of the record */
	protected int timeOffset;
	/** The glucose concentration. 0 if not present */
	protected float glucoseConcentration;
	/** Concentration unit. One of the following: {@link GlucoseRecord#UNIT_kgpl}, {@link GlucoseRecord#UNIT_molpl} */
	protected int unit;
	/** The type of the record. 0 if not present */
	protected int type;
	/** The sample location. 0 if unknown */
	protected int sampleLocation;
	/** Sensor status annunciation flags. 0 if not present */
	protected int status;

	protected MeasurementContext context;

	public static class MeasurementContext {
		public static final int UNIT_kg = 0;
		public static final int UNIT_l = 1;

		/**
		 * One of the following:<br/>
		 * 0 Not present<br/>
		 * 1 Breakfast<br/>
		 * 2 Lunch<br/>
		 * 3 Dinner<br/>
		 * 4 Snack<br/>
		 * 5 Drink<br/>
		 * 6 Supper<br/>
		 * 7 Brunch
		 */
		protected int carbohydrateId;
		/** Number of kilograms of carbohydrate */
		protected float carbohydrateUnits;
		/**
		 * One of the following:<br/>
		 * 0 Not present<br/>
		 * 1 Preprandial (before meal)<br/>
		 * 2 Postprandial (after meal)<br/>
		 * 3 Fasting<br/>
		 * 4 Casual (snacks, drinks, etc.)<br/>
		 * 5 Bedtime
		 */
		protected int meal;
		/**
		 * One of the following:<br/>
		 * 0 Not present<br/>
		 * 1 Self<br/>
		 * 2 Health Care Professional<br/>
		 * 3 Lab test<br/>
		 * 15 Tester value not available
		 */
		protected int tester;
		/**
		 * One of the following:<br/>
		 * 0 Not present<br/>
		 * 1 Minor health issues<br/>
		 * 2 Major health issues<br/>
		 * 3 During menses<br/>
		 * 4 Under stress<br/>
		 * 5 No health issues<br/>
		 * 15 Tester value not available
		 */
		protected int health;
		/** Exercise duration in seconds. 0 if not present */
		protected int exerciseDurtion;
		/** Exercise intensity in percent. 0 if not present */
		protected int exerciseIntensity;
		/**
		 * One of the following:<br/>
		 * 0 Not present<br/>
		 * 1 Rapid acting insulin<br/>
		 * 2 Short acting insulin<br/>
		 * 3 Intermediate acting insulin<br/>
		 * 4 Long acting insulin<br/>
		 * 5 Pre-mixed insulin
		 */
		protected int medicationId;
		/** Quantity of medication. See {@link #medicationUnit} for the unit. */
		protected float medicationQuantity;
		/** One of the following: {@link GlucoseRecord.MeasurementContext#UNIT_kg}, {@link GlucoseRecord.MeasurementContext#UNIT_l}. */
		protected int medicationUnit;
		/** HbA1c value. 0 if not present */
		protected float HbA1c;
	}
}
