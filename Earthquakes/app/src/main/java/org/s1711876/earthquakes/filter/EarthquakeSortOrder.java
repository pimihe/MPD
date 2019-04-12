// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.filter;

// class used to simplify the filter fragment and filter view model
public class EarthquakeSortOrder {

    public enum EarthquakeSortByOption {
        DATE,
        MAGNITUDE,
        DEPTH,
        DISTANCE_FROM,
        DISTANCE_FROM_ME,
        MOST_NORTHERLY,
        MOST_EASTERLY,
    }

    private String distanceFrom;
    private boolean sortByAscending;
    private EarthquakeSortByOption sortOption;

    public EarthquakeSortOrder(String distanceFrom, boolean sortByAscending, EarthquakeSortByOption sortOption) {
        this.distanceFrom = distanceFrom;
        this.sortByAscending = sortByAscending;
        this.sortOption = sortOption;
    }

    public String getDistanceFrom() { return distanceFrom; }
    public void setDistanceFrom(String distanceFrom) { this.distanceFrom = distanceFrom; }

    public boolean isSortByAscending() { return sortByAscending; }
    public void setSortByAscending(boolean sortByAscending) { this.sortByAscending = sortByAscending; }

    public EarthquakeSortByOption getSortOption() { return sortOption; }
    public void setSortOption(EarthquakeSortByOption sortOption) { this.sortOption = sortOption; }
    
}
