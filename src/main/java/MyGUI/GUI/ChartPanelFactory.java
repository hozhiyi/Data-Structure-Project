package MyGUI.GUI;

import MyGUI.datastruct.*;
import MyGUI.algorithms.*;
import MyGUI.delivery.*;
import MyGUI.GUI.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to create Chart Panel to illustrate the Tour
 */
public class ChartPanelFactory {
    private static final String DEPOT_ICON_PATH = "./assets/img/depot.png",
                                HOUSE_ICON_PATH = "./assets/img/customer-house.png",
                                CHART_BACKGROUND_IMG_PATH = "./assets/img/delivery-map-3.png";
    private static final Graph<Integer, Double> graph = GraphFactory.createGraph();

    public static ChartPanel createChart(Tour<Integer, Double> tour) {
        // Create dataset
        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        XYSeries scatterData = createScatterDataSeries();
        XYSeries lineData = createLineDataSeries(tour, scatterData);
        seriesCollection.addSeries(scatterData);
        seriesCollection.addSeries(lineData);

        // Create chart
        JFreeChart chart = ChartFactory.createXYLineChart("", "", "", seriesCollection, PlotOrientation.VERTICAL,
                                                        false, true, false);

        // Apply formatting to Chart
        formattingChart(chart);

        // Plot the house and depot icon annotations on Chart
        plotIconAnnotation(chart.getXYPlot(), scatterData);

        ChartPanel chartPanel = new ChartPanel(chart);

        // Disable zoom by mouse drag
        chartPanel.setMouseZoomable(false);

        // Enable zoom by mouse wheel
        chartPanel.setZoomInFactor(0.95);
        chartPanel.setZoomOutFactor(1.05);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.addMouseWheelListener(e -> {
            if (e.getWheelRotation() > 0) {
                chartPanel.zoomOutBoth(0.05, 0.05);
            }
            else if (e.getWheelRotation() < 0) {
                chartPanel.zoomInBoth(1.05, 1.05);
            }
        });

        return chartPanel;
    }

    // Format the chart
    private static void formattingChart(JFreeChart chart) {
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // "0" is the scatter plot for Customer - hide lines, show dots
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);

        // "1" is the line plot for Route - show lines, hide dots
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, false);

        // Tooltips for scatter plot
        StandardXYToolTipGenerator standardXYToolTipGenerator = new StandardXYToolTipGenerator() {
            @Override
            public String generateToolTip(XYDataset dataset, int series, int item) {
                StringBuilder sb = new StringBuilder();
                int x = (int) dataset.getXValue(series, item);
                int y = (int) dataset.getYValue(series, item);
                sb.append("<html><body><font size=\"5\">")
                        .append("ID = ").append(item).append("<br>")
                        .append("X = ").append(x).append("<br>")
                        .append("Y = ").append(y).append("<br>")
                        .append("</font></body></html>");
                return sb.toString();
            }
        };
        renderer.setSeriesToolTipGenerator(0, standardXYToolTipGenerator);

        // Set line style (dashed) and line thickness
        Stroke lineStroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {1.0f, 1.0f}, 0.0f);
        renderer.setSeriesStroke(1, lineStroke);

        // Change dot size of scatter plot
        int size = 20;
        double a = size / 2.0;
        renderer.setSeriesShape(0, new Ellipse2D.Double(-a, -a, size, size));

        plot.setRenderer(renderer);

        // Make the Plot transparent
        Color transparent = new Color(0xFF, 0xFF, 0xFF, 0);
        plot.setBackgroundPaint(transparent);
        plot.setBackgroundImageAlpha(0.0f);

        // Set Background Image for Chart
        try {
            Image mapBackground = ImageIO.read(new File(CHART_BACKGROUND_IMG_PATH));
            chart.setBackgroundImage(mapBackground);
            chart.setBackgroundImageAlpha(0.9f);   // Transparency
        }
        catch (IOException e) {
            System.out.println("Cannot find background map image");
        }

        // Make the plot Pannable (Draggable)
        plot.setRangePannable(true);
        plot.setDomainPannable(true);

        // X and Y axis does not start at 0
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        xAxis.setAutoRangeIncludesZero(false);
        yAxis.setAutoRangeIncludesZero(false);

        // Hide gridlines
        chart.getXYPlot().setDomainGridlinesVisible(false);
        chart.getXYPlot().setRangeGridlinesVisible(false);

        // Hide x and y axis
        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setVisible(false);

        // Hide border of plot
        plot.setOutlineVisible(false);
    }

    // Generate data for scatter plot of customers
    private static XYSeries createScatterDataSeries() {
        XYSeries scatterData = new XYSeries("Customer", false);
        ArrayList<Customer<Integer, Double>> customers = graph.getAllCustomerObjects();
        for (Customer<Integer, Double> customer : customers) {
            scatterData.add(customer.getX(), customer.getY());
        }
        return scatterData;
    }

    // Generate data for line plot of routes
    private static XYSeries createLineDataSeries(Tour<Integer, Double> tour, XYSeries scatterData) {
        XYSeries lineData = new XYSeries("Route", false);
        ArrayList<Vehicle<Integer, Double>> vehicles = tour.getVehicles();
        for (Vehicle<Integer, Double> vehicle : vehicles) {
            ArrayList<Integer> route = vehicle.getRoute();
            for (int customerID : route) {
                XYDataItem point = scatterData.getDataItem(customerID);
                lineData.add(point);
            }
        }
        return lineData;
    }

    // Plot icon annotations (depot, house, ...) on the chart
    private static void plotIconAnnotation(XYPlot plot, XYSeries scatterSeries) {
        // Depot icon annotations
        XYDataItem depot = scatterSeries.getDataItem(0);
        XYAnnotation xyAnnotation = createAnnotation(DEPOT_ICON_PATH, depot.getXValue(), depot.getYValue());
        plot.addAnnotation(xyAnnotation);

        // Customer house icon annotations
        for (int i = 1; i < scatterSeries.getItemCount(); i++) {
            XYDataItem house = scatterSeries.getDataItem(i);
            xyAnnotation = createAnnotation(HOUSE_ICON_PATH, house.getXValue(), house.getYValue());
            plot.addAnnotation(xyAnnotation);
        }
    }

    // Create annotations at desired coordinate using image
    private static XYAnnotation createAnnotation(String iconPath, double x, double y) {
        int width = 40;
        int height = 40;
        Image scaledImage = new ImageIcon(iconPath).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon imageIcon = new ImageIcon(scaledImage);
        return new XYImageAnnotation(x, y, imageIcon.getImage());
    }

}
