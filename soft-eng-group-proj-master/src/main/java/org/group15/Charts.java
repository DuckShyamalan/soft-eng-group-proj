package org.group15;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.time.*;
import org.jfree.data.xy.IntervalXYDataset;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class Charts extends JPanel{

	private String title;
	private QueryEngine engine;
	private Second startTime;
	private Second endTime;
	private ArrayList<String> dataTypes;
	private boolean[] filterList;
	private JPanel cPanel;
	private TimeSeriesCollection seriesList;
	private XYLineAndShapeRenderer renderer;
	private Interval granularity;
	private JFreeChart chart;

	public JFreeChart getChart() {
		return this.chart;
	}

	Charts(String title, ArrayList<String> dataTypes, boolean[] filters, Second startTime, Second endTime, QueryEngine engine, Interval granularity){
		this.title = title;
		this.engine = engine;
		this.startTime = startTime;
		this.endTime = endTime;
		this.dataTypes = dataTypes;
		this.filterList = filters;
		this.granularity = granularity;
		this.seriesList = generateData();

		JFreeChart chart = createChart(seriesList);
		this.chart = chart;
		cPanel = new ChartPanel(chart);
		this.add(cPanel);
	}
	
	Charts(String title, Second startTime, Second endTime, boolean[] filterList, QueryEngine engine, String type){
		this.title = title;
		this.engine = engine;
		this.startTime = startTime;
		this.endTime = endTime;
		this.filterList = filterList;
		this.granularity = Interval.Day;
		JFreeChart chart = createHistogram(generateHistoData(type));
		cPanel = new ChartPanel(chart);
		this.add(cPanel);
	}

	private TimeSeriesCollection generateData() { //to be adjusted once data is passed in
		
		if(engine != null) {
			
			TimeSeriesCollection seriesCollection = new TimeSeriesCollection();
			for(String dataType : dataTypes) {
				if(filterList[0]) {
					seriesCollection.addSeries(convertTimes(dataType,null,null,null,null));
				}
				if(filterList[1]) {
					seriesCollection.addSeries(convertTimes(dataType,null,Age.Less25,null,null));
					seriesCollection.addSeries(convertTimes(dataType,null,Age.Less34,null,null));
					seriesCollection.addSeries(convertTimes(dataType,null,Age.Less44,null,null));
					seriesCollection.addSeries(convertTimes(dataType,null,Age.Less54,null,null));
					seriesCollection.addSeries(convertTimes(dataType,null,Age.More54,null,null));
				}
				if(filterList[2]) {
					seriesCollection.addSeries(convertTimes(dataType,Gender.Female,null,null,null));
					seriesCollection.addSeries(convertTimes(dataType,Gender.Male,null,null,null));
				}
				if(filterList[3]) {
					seriesCollection.addSeries(convertTimes(dataType,null,null,Income.Low,null));
					seriesCollection.addSeries(convertTimes(dataType,null,null,Income.Medium,null));
					seriesCollection.addSeries(convertTimes(dataType,null,null,Income.High,null));
				}
				if(filterList[4]) {
					seriesCollection.addSeries(convertTimes(dataType,null,null,null,Context.Blog));
					seriesCollection.addSeries(convertTimes(dataType,null,null,null,Context.Hobbies));
					seriesCollection.addSeries(convertTimes(dataType,null,null,null,Context.Media));
					seriesCollection.addSeries(convertTimes(dataType,null,null,null,Context.News));
					seriesCollection.addSeries(convertTimes(dataType,null,null,null,Context.Shopping));
					seriesCollection.addSeries(convertTimes(dataType,null,null,null,Context.Social));
					seriesCollection.addSeries(convertTimes(dataType,null,null,null,Context.Travel));
				}
			}
			return seriesCollection;
		}
		else {
			return null;
		}
	}
	
	private IntervalXYDataset generateHistoData(String type) {
		
		if(engine != null) {
			HistogramDataset dataset = new HistogramDataset();
		    dataset.setType(HistogramType.FREQUENCY);
		    Queryable<Double> data = engine.getHistogramData(toDateTime(startTime), toDateTime(endTime));
		    ArrayList<Double> dataArrayList = data.toArrayList();
		    double[] dataArray = new double[dataArrayList.size()];
		    for (int i = 0; i < dataArrayList.size(); i++)
		    	dataArray[i] = dataArrayList.get(i);
		    dataset.addSeries("Histogram", dataArray, 10);
		    return dataset;
		}
		else {
			return null;
		}
	}

	private TimeSeries convertTimes(String dataType, Gender gender, Age age, Income income,Context context){
		TimeSeries series;
		if(gender == null && age == null && income == null && context == null) {
			series = new TimeSeries(dataType);
			switch(dataType.toLowerCase()) {
				case "number of impressions":
					for(Pair<DateTime, Integer> item : engine.getImpressionCount(toDateTime(startTime), toDateTime(endTime), granularity)) {
						series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
					}
					break;
				case "number of clicks":
					for(Pair<DateTime, Integer> item : engine.getClickCount(toDateTime(startTime), toDateTime(endTime), granularity)) {
						series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
					}
					break;
				case "number of uniques":
					for(Pair<DateTime, Integer> item : engine.getUniquesCount(toDateTime(startTime), toDateTime(endTime), granularity)) {
						series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
					}
					break;
				case "number of bounces":
					for(Pair<DateTime, Integer> item : engine.getBouncesCount(toDateTime(startTime), toDateTime(endTime), granularity)) {
						series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
					}
					break;
				case "number of conversions":
					for(Pair<DateTime, Integer> item : engine.getConversionCount(toDateTime(startTime), toDateTime(endTime), granularity)) {
						series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
					}
					break;
				case "total cost":
					for(Pair<DateTime, Double> item : engine.getTotalCost(toDateTime(startTime), toDateTime(endTime), granularity)) {
						series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
					}
					break;
				case "ctr":
					for(Pair<DateTime, Double> item : engine.getClickThroughRate(toDateTime(startTime), toDateTime(endTime), granularity)) {
						series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
					}
					break;
				case "cpa":
					for(Pair<DateTime, Double> item : engine.getCostPerAcquisition(toDateTime(startTime), toDateTime(endTime), granularity)) {
						series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
					}
					break;
				case "cpc":
					for(Pair<DateTime, Double> item : engine.getCostPerClick(toDateTime(startTime), toDateTime(endTime), granularity)) {
						series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
					}
					break;
				case "cpm":
					for(Pair<DateTime, Double> item : engine.getCostPerThousandImpressions(toDateTime(startTime), toDateTime(endTime), granularity)) {
						series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
					}
					break;
				case "bounce rate":
					for(Pair<DateTime, Double> item : engine.getBounceRate(toDateTime(startTime), toDateTime(endTime), granularity)) {
						series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
					}
					break;
			}
		}
		else {
			if(gender != null) {
				series = new TimeSeries(dataType + ": " + gender.toString());
			}
			else if(age != null) {
				if(age.toString() == Age.Less25.toString()) {
					series = new TimeSeries(dataType + ": < 25 y/o");
				}
				else if(age.toString() == Age.Less34.toString()) {
					series = new TimeSeries(dataType + ": 25 - 34 y/o");
				}
				else if(age.toString() == Age.Less44.toString()) {
					series = new TimeSeries(dataType + ": 35 - 44 y/o");
				}
				else if(age.toString() == Age.Less54.toString()) {
					series = new TimeSeries(dataType + ": 45 - 54 y/o");
				}
				else if(age.toString() == Age.More54.toString()) {
					series = new TimeSeries(dataType + ": > 54 y/o");
				}
				else {
					series = null;
				}
			}
			else if(income != null) {
				series = new TimeSeries(dataType + ": " + income.toString() + " $$");
			}
			else if(context != null) {
				series = new TimeSeries(dataType + ": " + context.toString());
			}
			else {
				series = new TimeSeries(dataType + ": unidentified filtering");
			}
			
			switch(dataType.toLowerCase()) {
			case "number of impressions":
				for(Pair<DateTime, Integer> item : engine.getImpressionCount(toDateTime(startTime), toDateTime(endTime), granularity, gender, age, income, context)) {
					series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
				}
				break;
			case "number of clicks":
				for(Pair<DateTime, Integer> item : engine.getClickCount(toDateTime(startTime), toDateTime(endTime), granularity, gender, age, income, context)) {
					series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
				}
				break;
			case "number of uniques":
				for(Pair<DateTime, Integer> item : engine.getUniquesCount(toDateTime(startTime), toDateTime(endTime), granularity, gender, age, income, context)) {
					series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
				}
				break;
			case "number of bounces":
				for(Pair<DateTime, Integer> item : engine.getBouncesCount(toDateTime(startTime), toDateTime(endTime), granularity, gender, age, income, context)) {
					series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
				}
				break;
			case "number of conversions":
				for(Pair<DateTime, Integer> item : engine.getConversionCount(toDateTime(startTime), toDateTime(endTime), granularity, gender, age, income, context)) {
					series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
				}
				break;
			case "total cost":
				for(Pair<DateTime, Double> item : engine.getTotalCost(toDateTime(startTime), toDateTime(endTime), granularity, gender, age, income, context)) {
					series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
				}
				break;
			case "ctr":
				for(Pair<DateTime, Double> item : engine.getClickThroughRate(toDateTime(startTime), toDateTime(endTime), granularity, gender, age, income, context)) {
					series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
				}
				break;
			case "cpa":
				for(Pair<DateTime, Double> item : engine.getCostPerAcquisition(toDateTime(startTime), toDateTime(endTime), granularity, gender, age, income, context)) {
					series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
				}
				break;
			case "cpc":
				for(Pair<DateTime, Double> item : engine.getCostPerClick(toDateTime(startTime), toDateTime(endTime), granularity, gender, age, income, context)) {
					series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
				}
				break;
			case "cpm":
				for(Pair<DateTime, Double> item : engine.getCostPerThousandImpressions(toDateTime(startTime), toDateTime(endTime), granularity, gender, age, income, context)) {
					series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
				}
				break;
			case "bounce rate":
				for(Pair<DateTime, Double> item : engine.getBounceRate(toDateTime(startTime), toDateTime(endTime), granularity, gender, age, income, context)) {
					series.add(new TimeSeriesDataItem(toRegTime(item.getKey()), item.getValue()));
				}
				break;
		}
		}
		return series;
	}
	
	public DateTime toDateTime(Second time) {
		return new DateTime(time.getMinute().getHour().getYear(), time.getMinute().getHour().getMonth(), time.getMinute().getHour().getDayOfMonth(), time.getMinute().getHour().getHour(), time.getMinute().getMinute(), time.getSecond());
	}
	
	public RegularTimePeriod toRegTime(DateTime dateTime) {//change with specified interval later
		switch(granularity) {
		case Hour:
			return new Hour(dateTime.getHour(), dateTime.getDay(), dateTime.getMonth(), dateTime.getYear());
		case Day:
			return new Day(dateTime.getDay(), dateTime.getMonth(), dateTime.getYear());
		case Week:
			return new Week(new Date(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay()));
		case Month:
			return new Month(dateTime.getMonth(), dateTime.getYear());
		default:
			return null;
		}
	}

	private JFreeChart createChart(TimeSeriesCollection data) {
		
		JFreeChart chart = ChartFactory.createTimeSeriesChart(this.title, "Time period", "Frequency", data);
		this.renderer = new XYLineAndShapeRenderer();
	    XYPlot plot = chart.getXYPlot();
		
	    plot.getRangeAxis().setAutoRange(true);;
	    
		plot.setRenderer(renderer);
		plot.setBackgroundPaint(Color.white);

		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);

		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);

		return chart;
	}
	
	private JFreeChart createHistogram(IntervalXYDataset data) {
		
		JFreeChart chart = ChartFactory.createHistogram(title, "Total cost", "Frequency", data, PlotOrientation.VERTICAL, false, false, false);
		return chart;
	}
	
	public void toggleLine(String lineName, boolean show) {
		int index = seriesList.indexOf(lineName);
		this.renderer.setSeriesVisible(index, show);
	}
}
