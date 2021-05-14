package analysis;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

/**
 * Maintains all different kinds of charts (and their details) on the chart matrix/board
 */
public enum SimulationCharts {
    WAVE_LEVEL(new XYChartBuilder()
            .xAxisTitle("Time(s)")
            .yAxisTitle("Wave(m)")
            .width(500)
            .height(500).theme(Styler.ChartTheme.Matlab).build(), new Series("wave")),
    BUOY(new XYChartBuilder()
            .xAxisTitle("Time(s)")
            .yAxisTitle("Buoyancy(N))")
            .width(500)
            .height(500).theme(Styler.ChartTheme.Matlab).build(), new Series("buoy")),
    FORCE(new XYChartBuilder()
            .xAxisTitle("Time(s)")
            .yAxisTitle("Force(N)")
            .width(500)
            .height(500).theme(Styler.ChartTheme.Matlab).build(), new Series(" thrust"), new Series("damp")),
    SPEED(new XYChartBuilder()
            .xAxisTitle("Time(s)")
            .yAxisTitle("Speed(m/s)")
            .width(500)
            .height(500).theme(Styler.ChartTheme.Matlab).build(), new Series("speed"));

    private final XYChart chart;
    private final Series[] series;

    SimulationCharts(XYChart chart, Series... series) {
        this.chart = chart;
        this.series = series;
    }

    public XYChart getChart() {
        return chart;
    }

    public Series[] getSeries() {
        return series;
    }

    public int getIndex() {
        for (int i = 0; i < SimulationCharts.values().length; i++) {
            if (this.equals(SimulationCharts.values()[i]))
                return i;
        }
        throw new IllegalArgumentException("Index search error");
    }
}
