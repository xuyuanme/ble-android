/*******************************************************************************
 * Copyright (c) 2013 Nordic Semiconductor. All Rights Reserved.
 * 
 * The information contained herein is property of Nordic Semiconductor ASA.
 * Terms and conditions of usage are described in detail in NORDIC SEMICONDUCTOR STANDARD SOFTWARE LICENSE AGREEMENT.
 * Licensees are granted free, non-transferable use of the information. NO WARRANTY of ANY KIND is provided. 
 * This heading must NOT be removed from the file.
 ******************************************************************************/
package no.nordicsemi.android.nrftoolbox.hrs;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Point;

/**
 * This class uses external library AChartEngine to show dynamic real time line graph for HR values
 */
public class LineGraphView {
	//TimeSeries will hold the data in x,y format for single chart  
	private TimeSeries mSeries = new TimeSeries("Heart Rate");
	//XYSeriesRenderer is used to set the properties like chart color, style of each point, etc. of single chart
	private XYSeriesRenderer mRenderer = new XYSeriesRenderer();
	//XYMultipleSeriesDataset will contain all the TimeSeries 
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	//XYMultipleSeriesRenderer will contain all XYSeriesRenderer and it can be used to set the properties of whole Graph
	private XYMultipleSeriesRenderer mMultiRenderer = new XYMultipleSeriesRenderer();

	private static LineGraphView mInstance = null;

	/**
	 * singleton implementation of LineGraphView class
	 */
	public static synchronized LineGraphView getLineGraphView() {
		if (mInstance == null) {
			mInstance = new LineGraphView();
		}
		return mInstance;
	}

	/**
	 * This constructor will set some properties of single chart and some properties of whole graph
	 */
	public LineGraphView() {
		//add single line chart mSeries   
		mDataset.addSeries(mSeries);
		//set line chart color to Black
		mRenderer.setColor(Color.BLACK);
		//set line chart style to square points
		mRenderer.setPointStyle(PointStyle.SQUARE);
		mRenderer.setFillPoints(true);

		final XYMultipleSeriesRenderer renderer = mMultiRenderer;
		//set whole graph background color to transparent color  
		renderer.setBackgroundColor(Color.TRANSPARENT);
		renderer.setMargins(new int[] { 50, 65, 40, 5 }); // top, left, bottom, right
		renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		renderer.setAxesColor(Color.BLACK);
		renderer.setAxisTitleTextSize(24);
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.LTGRAY);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.DKGRAY);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setYLabelsPadding(4.0f);
		renderer.setXLabelsColor(Color.DKGRAY);
		renderer.setLabelsTextSize(20);
		renderer.setLegendTextSize(20);
		//Disable zoom
		renderer.setPanEnabled(false, false);
		renderer.setZoomEnabled(false, false);
		//set title to x-axis and y-axis
		renderer.setXTitle("    Time (seconds)");
		renderer.setYTitle("               BPM");
		renderer.addSeriesRenderer(mRenderer);
	}

	/**
	 * return graph view to activity
	 */
	public GraphicalView getView(Context context) {
		final GraphicalView graphView = ChartFactory.getLineChartView(context, mDataset, mMultiRenderer);
		return graphView;
	}

	/**
	 * add new x,y value to chart
	 */
	public void addValue(Point p) {
		mSeries.add(p.x, p.y);
	}

	/**
	 * clear all previous values of chart
	 */
	public void clearGraph() {
		mSeries.clear();
	}

}
