package com.example.venues.search.venuessearch;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class HTTPGetAsyncTask extends AsyncTask<URL, Void, String> {

    private final VenuePresenter mPresenter;

    public HTTPGetAsyncTask(VenuePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL url = urls[0];
        HttpsURLConnection urlConnection = null;

        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            InputStream in;
            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                in = new BufferedInputStream(urlConnection.getInputStream());
            } else {
                in = new BufferedInputStream(urlConnection.getErrorStream());
            }
            InputStreamReader is = new InputStreamReader(in);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while (read != null) {
                sb.append(read);
                read = br.readLine();
            }
            Log.v("AsyncTask", "resp:" + sb);
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    protected void onPostExecute(String response) {
        mPresenter.onHTTPQueryFinished(response);
    }
}
