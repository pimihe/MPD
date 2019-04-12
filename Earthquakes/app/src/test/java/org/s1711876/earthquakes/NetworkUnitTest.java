// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes;

// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

import org.junit.Test;

import static org.junit.Assert.*;
import static org.s1711876.earthquakes.utility.NetworkHelper.getUrl;

public class NetworkUnitTest {
    @Test
    public void networkXmlReq() {
        String testStr;
        try {
            testStr = getUrl("http://quakes.bgs.ac.uk/feeds/MhSeismology.xml");
        } catch (Exception e) {
            testStr = null;
            e.printStackTrace();
        }
        boolean res = false;
        if (!testStr.equals("") && testStr != null) res = true;

        assertTrue(res);
    }

    @Test
    public void networkFailReq() {
        String testStr;
        try {
            testStr = getUrl("http://127.0.0.1:344/");
        } catch (Exception e) {
            testStr = null;
            e.printStackTrace();
        }
        System.out.println(testStr);
        boolean res = false;
        if (testStr == null) res = true;
        assertTrue(res);
    }

    // test case will not work without node api used in testing
    @Test
    public void networkEmptyReq() {
        String testStr;
        try {
            testStr = getUrl("http://127.0.0.1:8080/fourofour");
        } catch (Exception e) {
            testStr = null;
            e.printStackTrace();
        }
        System.out.println(testStr);
        boolean res = false;
        if (testStr == null) res = true;
        assertTrue(res);
    }

    // test case will not work without node api used in testing
    @Test
    public void networkBlankStrReq() {
        String testStr;
        try {
            testStr = getUrl("http://127.0.0.1:8080/blankstr");
        } catch (Exception e) {
            testStr = null;
            e.printStackTrace();
        }
        System.out.println(testStr);
        boolean res = false;
        if (testStr.equals("")) res = true;
        assertTrue(res);
    }

}