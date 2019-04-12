// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.filter;

// class used to simplify the filter fragment and filter view model
public class EarthquakeMagnitudeFilter {

    public enum MagnitudeComparator {
        EQUAL,
        GREATER_THAN,
        LESS_THAN,
        BETWEEN
    }

    private MagnitudeComparator comparator;
    private double magnitude1;
    private double magnitude2;

    public EarthquakeMagnitudeFilter() { }

    public EarthquakeMagnitudeFilter(MagnitudeComparator comparator, double magnitude1) {
        this.comparator = comparator;
        this.magnitude1 = magnitude1;
    }

    public EarthquakeMagnitudeFilter(MagnitudeComparator comparator, double magnitude1, double magnitude2) {
        this.comparator = comparator;
        this.magnitude1 = magnitude1;
        this.magnitude2 = magnitude2;
    }

    public MagnitudeComparator getComparator() { return comparator; }
    public void setComparator(MagnitudeComparator comparator) { this.comparator = comparator; }

    public double getMagnitude1() { return magnitude1; }
    public void settMagnitude1(double magnitude) { this.magnitude1 = magnitude; }


    public double getMagnitude2() { return magnitude2; }
    public void settMagnitude2(double magnitude) { this.magnitude2 = magnitude; }
}
