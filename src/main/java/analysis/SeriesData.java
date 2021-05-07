package analysis;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Serves as a database for storing chart records
 * Author: bbrighttaer
 * Date: 4/17/2018
 * Time: 11:54 AM
 * Project: AllyFaction
 */
public class SeriesData {
    private final Map<Integer, Double> data = new TreeMap<>();

    public SeriesData() {
    }

    /**
     * Adds a value to the chart data
     * @param t The x axis value
     * @param v The y axis value
     */
    public void record(int t, double v){
        this.data.put(t, v);
    }

    /**
     * Retrieves an unmodifiable version of the chart data
     * @return A map of the (x, y) values of the chart
     */
    public Map<Integer, Double> getData() {
        return Collections.unmodifiableMap(this.data);
    }

    /**
     * Removes all chart data entries
     */
    public void clear(){
        this.data.clear();
    }
}
