// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// a general utility class with common functions to prevent code being written twice
public class UtilHelper {
    // formats the location string to make location appear better on device by splitting location and sub location
    public static List<String> getFormattedLocationStringList(String locationString) {
        List<String> retList = new ArrayList<>();

        List<String> splitLocation = Arrays.asList(locationString.toLowerCase().split(","));
        for (String splitStr : splitLocation) {
            StringBuilder stringBuilder = new StringBuilder();
            List<String> testArr = Arrays.asList(splitStr.split(""));
            boolean nextCapital = true;
            for (String character: testArr) {
                if(character.equals("") || character.equals(" ") || character.equals("/")) {
                    nextCapital = true;
                    stringBuilder.append(character);
                } else if(nextCapital && !character.equals("")) {
                    nextCapital = false;
                    stringBuilder.append(character.toUpperCase());
                }else{
                    stringBuilder.append(character);
                }
            }
            retList.add(stringBuilder.toString().trim());
        }

        return retList;
    }

    // function used to assign color value to earthquake detail component magnitude and depth display elements
    public static String getHexColorFromNoRange(double value ,double min, double max, boolean greenFirst) {
        if(value>max) value = max;
        if(value<min) value = min;
        double percentage = (value - min) / (max - min);
        double result = Math.round(510*percentage);
        if(greenFirst) result=510-result;
        double primary = 255;
        double remainder = 255;
        if(result>255) {
            primary = 255-(result-255);
        } else {
            remainder = result;
        }
        int r = (int) primary;
        int g = (int) remainder;
        return String.format("#%02x%02x%02x", r, g, 60);
    }
}
