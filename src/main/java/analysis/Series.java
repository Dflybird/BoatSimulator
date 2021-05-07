package analysis;

/**
 * Represents a single series on a chart
 * Author: bbrighttaer
 * Date: 4/17/2018
 * Time: 12:10 PM
 * Project: AllyFaction
 */
public class Series {
    /**
     * The name of the series
     */
    private final String seriesName;
    private final SeriesData seriesData;

    /**
     * Creates a series for a chart
     * @param seriesName The name of the series
     */
    public Series(String seriesName) {
        this.seriesName = seriesName;
        this.seriesData = new SeriesData();
    }

    /**
     * Retrieves the name of this series
     * @return
     */
    public String getSeriesName() {
        return seriesName;
    }

    /**
     * Retrieves the data of this series
     * @return
     */
    public SeriesData getseriesData() {
        return seriesData;
    }
}
