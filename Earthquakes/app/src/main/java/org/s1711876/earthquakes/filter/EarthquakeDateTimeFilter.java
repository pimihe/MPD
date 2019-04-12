// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.filter;


// class used to simplify the filter fragment and filter view model
public class EarthquakeDateTimeFilter {

    public enum DateTimeComparator {
        EQUAL,
        GREATER_THAN,
        LESS_THAN,
        BETWEEN
    }

    private DateTimeComparator comparator;
    private long dateTime1;
    private long dateTime2;

    public EarthquakeDateTimeFilter() { }

    public EarthquakeDateTimeFilter(DateTimeComparator comparator, long dateTime1) {
        this.comparator = comparator;
        this.dateTime1 = dateTime1;
    }

    public EarthquakeDateTimeFilter(DateTimeComparator comparator, long dateTime1, long dateTime2) {
        this.comparator = comparator;
        this.dateTime1 = dateTime1;
        this.dateTime2 = dateTime2;
    }

    public DateTimeComparator getComparator() { return comparator; }
    public void setComparator(DateTimeComparator comparator) { this.comparator = comparator; }

    public long getDateTime1() { return dateTime1; }
    public void settDateTime1(long dateTime) { this.dateTime1 = dateTime; }


    public long getDateTime2() { return dateTime2; }
    public void settDateTime2(long dateTime) { this.dateTime2 = dateTime; }
}
