package analysis;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

/**
 * Maintains all different kinds of charts (and their details) on the chart matrix/board
 */
public enum SimulationCharts {
//    ROUND_SCORE(new XYChartBuilder()
//            .title("Performance")
//            .xAxisTitle("Episode")
//            .yAxisTitle("Score")
//            .width(500)
//            .height(500).theme(Styler.ChartTheme.Matlab).build(), new Series("Score")),
//    HIGH_SCORE(new XYChartBuilder()
//            .title("Generation Summary")
//            .xAxisTitle("Generation")
//            .yAxisTitle("Score")
//            .width(500)
//            .height(500).theme(Styler.ChartTheme.Matlab).build(), new Series("High score"), new Series("Average Score")),
//    EXP_AVG(new XYChartBuilder()
//            .title("Moving Average (window = " + Math.toIntExact(Math.round((1 / (1 - 0.95)))) + ")")
//            .xAxisTitle("Episode")
//            .yAxisTitle("Score")
//            .width(500)
//            .height(500).theme(Styler.ChartTheme.Matlab).build(), new Series("Exp. avg")),
    SPEED(new XYChartBuilder()
            .title("Sim")
            .xAxisTitle("Time")
            .yAxisTitle("Speed(m/s)")
            .width(500)
            .height(500).theme(Styler.ChartTheme.Matlab).build(), new Series("Mutation Prob."));

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
