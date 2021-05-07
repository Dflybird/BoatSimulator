package analysis;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartOps {
    private static SwingWrapper<XYChart> sw = null;

    public static void displayChartMatrix() {
        List<XYChart> chartList = new ArrayList<>();
        for (SimulationCharts simulationCharts : SimulationCharts.values()) {
            XYChart chart = simulationCharts.getChart();
            chart.getStyler().setToolTipsEnabled(true);
            Series[] seriesNames = simulationCharts.getSeries();
            for (Series seriesName : seriesNames) {
                XYSeries xySeries = chart.addSeries(seriesName.getSeriesName(), new double[]{0}, new double[]{0});
                xySeries.setMarker(SeriesMarkers.CIRCLE);
            }
            chartList.add(chart);
        }
        sw = new SwingWrapper<>(chartList);
        sw.displayChartMatrix("Ally Faction Chart Matrix");
    }

    public static void updateChartData(SimulationCharts simChart) {
        XYChart chart = simChart.getChart();
        //update the data set for all series of the given chart
        Arrays.stream(simChart.getSeries()).forEach(series ->
                SwingUtilities.invokeLater(() -> chart.updateXYSeries(series.getSeriesName(),
                        new ArrayList<>(series.getseriesData().getData().keySet()),
                        new ArrayList<>(series.getseriesData().getData().values()),
                        null)));
        SwingUtilities.invokeLater(() -> sw.repaintChart(simChart.getIndex()));
    }
}
