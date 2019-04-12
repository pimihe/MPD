package org.s1711876.earthquakes;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.s1711876.earthquakes.utility.UtilHelper.getFormattedLocationStringList;

public class LocationStringFunctionUnitTest {
        @Test
        public void locationNoSubLoc() {
            List<String> testStrLst;
            try {
                testStrLst = getFormattedLocationStringList("SOUTHERN NORTH SEA");
            } catch (Exception e) {
                testStrLst = null;
                e.printStackTrace();
            }
            assertTrue(testStrLst.size() == 1);
        }

        @Test
        public void locationWithSubLoc() {
            List<String> testStrLst;
            try {
                testStrLst = getFormattedLocationStringList("BEATTOCK,D AND G");
            } catch (Exception e) {
                testStrLst = null;
                e.printStackTrace();
            }
            assertTrue(testStrLst.size() == 2);
        }

        @Test
        public void locationWithBlankLoc() {
            List<String> testStrLst;
            try {
                testStrLst = getFormattedLocationStringList("");
            } catch (Exception e) {
                testStrLst = null;
                e.printStackTrace();
            }
            boolean res = false;
            assertTrue(testStrLst.isEmpty());
        }

        @Test
        public void locationWithMultiSub() {
            List<String> testStrLst;
            try {
                testStrLst = getFormattedLocationStringList("BEATTOCK,D AND G,WWW");
            } catch (Exception e) {
                testStrLst = null;
                e.printStackTrace();
            }
            assertTrue(testStrLst.size() == 3);
        }
}
