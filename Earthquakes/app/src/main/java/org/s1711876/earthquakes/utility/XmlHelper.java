// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.utility;

import org.s1711876.earthquakes.database.Earthquake;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class XmlHelper {

    public static List<Earthquake> parseXml(String content) {
        try {

            boolean inItemTag = false;
            String currentTagName = "";
            Earthquake currentItem = null;
            List<Earthquake> itemList = new ArrayList<>();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(content));

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        currentTagName = parser.getName();
                        if (currentTagName.equals("item")) {
                            inItemTag = true;
                            currentItem = new Earthquake();
                            itemList.add(currentItem);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
                            inItemTag = false;
                        }
                        currentTagName = "";
                        break;

                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        if (inItemTag && currentItem != null) {
                            try {
                                switch (currentTagName) {
                                    case "description":
                                        // description contains multiple fields seperated with semicolons so seperate into
                                        // list, sanitize values and set depth, location and magnitude from description
                                        List<String> splitStr = Arrays.asList(text.split(";"));
                                        String location = splitStr.get(1).substring(10);
                                        String depthStr = splitStr.get(3).replace("Depth:","").replace("km","").replaceAll("\\s","");
                                        String magnitudeStr = splitStr.get(4).replace("Magnitude:","").replaceAll("\\s","");
                                        int depth = Integer.parseInt(depthStr);
                                        double magnitude = Double.parseDouble(magnitudeStr);

                                        currentItem.setLocation(location);
                                        currentItem.setDepth(depth);
                                        currentItem.setMagnitude(magnitude);
                                        break;
                                    case "link":
                                        currentItem.setLink(text);
                                        break;
                                    case "pubDate":
                                        SimpleDateFormat dateParser = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                                        Date parsedDate = dateParser.parse(text);
                                        currentItem.setPubDate(parsedDate.getTime()/1000);
                                        break;
                                    case "geo:lat":
                                        currentItem.setLatitude(Double.parseDouble(text));
                                        break;
                                    case "geo:long":
                                        currentItem.setLongitude(Double.parseDouble(text));
                                        break;
                                    default:
                                        break;
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            return itemList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
