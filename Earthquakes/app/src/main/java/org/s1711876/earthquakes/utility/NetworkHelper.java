// Name                 Pierce hepburn
// Student ID           S1711876
// Programme of Study   Computing

package org.s1711876.earthquakes.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkHelper {

    // returns true if device has network support or false if no support
    public static boolean getNetworkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService((Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        try {
            return(networkInfo != null && networkInfo.isConnectedOrConnecting());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // gets string from url
    public static String getUrl(String addrsStr) throws IOException {
        InputStream inStream = null;
        try {
            URL url = new URL(addrsStr);

            // create connection object from url and connect
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // check result status code
            int resCode = conn.getResponseCode();
            if (resCode != 200) return null;

            inStream = conn.getInputStream();

            byte[] buf = new byte[512]; // get 512 bytes at a time
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            BufferedOutputStream outStream = null;
            try {
                int length;
                outStream = new BufferedOutputStream(byteArray);
                while ((length = inStream.read(buf)) > 0) outStream.write(buf, 0, length);//  while input stream has data
                outStream.flush();
                return byteArray.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (outStream != null) outStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) inStream.close();
        }
        return null;
    }
}
