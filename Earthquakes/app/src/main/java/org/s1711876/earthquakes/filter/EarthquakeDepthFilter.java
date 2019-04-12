// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.filter;

// class used to simplify the filter fragment and filter view model
public class EarthquakeDepthFilter {

    public enum DepthComparator {
        EQUAL,
        GREATER_THAN,
        LESS_THAN,
        BETWEEN
    }

    private DepthComparator comparator;
    private int depth1;
    private int depth2;

    public EarthquakeDepthFilter() { }

    public EarthquakeDepthFilter(DepthComparator comparator, int depth1) {
        this.comparator = comparator;
        this.depth1 = depth1;
    }

    public EarthquakeDepthFilter(DepthComparator comparator, int depth1, int depth2) {
        this.comparator = comparator;
        this.depth1 = depth1;
        this.depth2 = depth2;
    }

    public DepthComparator getComparator() { return comparator; }
    public void setComparator(DepthComparator comparator) { this.comparator = comparator; }

    public int getDepth1() { return depth1; }
    public void setDepth1(int depth) { this.depth1 = depth; }


    public int getDepth2() { return depth2; }
    public void setDepth2(int depth) { this.depth2 = depth; }

}
