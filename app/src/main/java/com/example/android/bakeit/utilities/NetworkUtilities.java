/*
* Copyright (C) 2017 John Magdalinos
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.bakeit.utilities;

import android.content.ContentValues;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Helper class with a list of methods used to get assets from the network
 */

public class NetworkUtilities {

    /** String URL of the json file on the network */
    public static final String JSON_STRING_URL = "https://d17h27t6h515a5.cloudfront" +
            ".net/topher/2017/May/59121517_baking/baking.json";

    /**
     * Method returning a String with the JSON response from the server
     */
    public static ArrayList<ArrayList<ContentValues>> getData() {
        // Get the URL
        URL jsonURL = null;

        try {
            jsonURL = new URL(JSON_STRING_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Make the HTTP request to return the JSON response
        String jsonResponse = null;

        try {
            jsonResponse = makeHTTPRequest(jsonURL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList<ContentValues>> data = null;
        try {
            data = JSONUtilities.getData(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    /** Method that makes the HTTP request and returns a JSON response string */
    private static String makeHTTPRequest(URL url) throws IOException {
        // Create the url connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        // Set the request method, connection timeout and read timeout
        urlConnection.setRequestMethod("GET");
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(15000);

        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error response code: " + urlConnection.getResponseCode());
        }

        // Read the input stream and then disconnect
        try {
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
