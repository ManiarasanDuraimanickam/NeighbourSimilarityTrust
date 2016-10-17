package com.project.nst.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import com.project.nst.utils.APPConstans;
import com.project.nst.utils.APPConstans.Algorithm;

public class ChartWindow extends ApplicationFrame {


	/**
   * 
   */
	private static final long serialVersionUID = -4010627435658182430L;



	protected ChartWindow() {
		super(Algorithm.ALGORITHM.getType());
		// TODO Auto-generated constructor stub
		JFreeChart lineChart =
				ChartFactory.createLineChart(Algorithm.ALGORITHM.getType(), APPConstans.CHART_JACCARD_XAxis_TXT, APPConstans.CHART_JACCARD_YAxis_TXT,
						createDataset(), PlotOrientation.VERTICAL, true, true, false);

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
		setContentPane(chartPanel);
	}



	private DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(97, "Percentage", "0");
		dataset.addValue(88, "Percentage", "5");
		dataset.addValue(73, "Percentage", "10");
		dataset.addValue(66, "Percentage", "18");
		dataset.addValue(52, "Percentage", "25");
		dataset.addValue(41, "Percentage", "33");
		dataset.addValue(34, "Percentage", "39");
		dataset.addValue(30, "Percentage", "47");
		return dataset;
	}

	public void showWindow(int width, int height) {
		// this.setPreferredSize(new Dimension(width, height));
		// this.pack();
		// this.setVisible(true);
	}

}
