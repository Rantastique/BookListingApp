package com.example.android.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sr on 09.07.17.
 */

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


    private QueryUtils() {
    }

    //query the GoogleBooks dataset and return a list of Book objects

    public static List<Book> fetchBookData(String requestUrl) {
        //create url
        URL url = createUrl(requestUrl);

        //perform http request to concerning URL, receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Book> books = extractFeatureFromJson(jsonResponse);

        return books;
    }

    //creates a new url from the given String
    private static URL createUrl(String titleSearch) {
        URL url = null;
        try {
            url = new URL("https://www.googleapis.com/books/v1/volumes?q=intitle:" + titleSearch + "&maxResults=15");
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem creating the URL", e);
        }
        return url;
    }

    //make http request to the url and return a String as response
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //convert the input stream to a string which contains the JSONresponse from the server
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //return a list of Book objects based on the parsing results
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        //creates an empty ArrayList to store the Book objects in
        List<Book> books = new ArrayList<Book>();

        //try parsing the JSONrespose String
        try {
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < bookArray.length(); i++) {

                JSONObject currentBook = bookArray.getJSONObject(i);

                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                String title = volumeInfo.getString("title");

                String author;
                if (volumeInfo.has("authors")) {
                    author = volumeInfo.getJSONArray("authors").get(0).toString();
                }
                else {
                    author = "Unknown author";
                }

                String url = volumeInfo.getString("infoLink");

                Book book = new Book(title, author, url);

                books.add(book);
            }


        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        //return a list with the Book objects
        return books;
    }

}