package com.example.lab8_omneimne;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    ListView dataList;

    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataList = findViewById(R.id.data);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1);

        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data = adapter.getItem(position);
                Toast.makeText(getApplicationContext(),  data, Toast.LENGTH_LONG).show();
            }
        });
        dataList.setAdapter(adapter);

        new HttpsGetTask().execute("https://mason.gmu.edu/~white/earthquakes.json");
    }



    private class HttpsGetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);

                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                urlConnection.connect();
                InputStream inputStream;
                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getErrorStream();
                    return inputStream.toString();
                } else {
                    inputStream = urlConnection.getInputStream();
                    return readStream(inputStream);
                }
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            onFinishGetRequest(result);
        }
    }
    private void onFinishGetRequest(String result) {
        //
        //    dataTV.setText(result)Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
        try {
            JSONArray earthquakes = (new JSONArray(result));
            int len = earthquakes.length();
            for (int i = 0;i<len;i++) {
                JSONObject quake = earthquakes.getJSONObject(i);
                String region = quake.getString("region");
                String mag = "";
                String occurred = quake.getString("occurred_at");
                String lat = "";
                String longitude = "";
                adapter.add(region + " (" + lat + "," + longitude + ") with magnitude = " + mag
                        + " on " + occurred);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String readStream(InputStream is) throws IOException {
        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("US-ASCII")));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            total.append(line);
        }
        if (reader != null) {
            reader.close();
        }
        return total.toString();
    }


}