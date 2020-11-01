package com.example.ea2;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ServicesHttp_POST extends IntentService {

    private Exception mException = null;
    private HttpURLConnection httpURLConnection;
    private URL mUrl;

    public ServicesHttp_POST() {
        super("ServicesHttp_GET");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            String uri = intent.getExtras().getString("uri");
            JSONObject datosJson = new JSONObject(intent.getExtras().getString(("datosJson")));
            String operation = intent.getExtras().getString("operation");
            String typeRequest = intent.getExtras().getString("typeRequest");
            String tn = intent.getExtras().getString("token");
            ejecutarPost(uri, datosJson, operation, tn);
        } catch (Exception e) {
            Log.e("LOGUEO_SERVICE", "ERROR" + e.toString());
        }

    }

    private StringBuilder converInputStreamToString(InputStreamReader inputStream) throws IOException {
        BufferedReader br = new BufferedReader(inputStream);
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            result.append(line + "\n");
        }
        br.close();
        return result;
    }

    private String POST(String uri, JSONObject datosJson, String tkn) {
        HttpURLConnection urlConnection = null;
        String result = "";
        try {
            URL mUrl = new URL(uri);

            urlConnection = (HttpURLConnection) mUrl.openConnection();
            if (tkn.length() == 0) {
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            } else {
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "Bearer " + tkn);
            }
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("POST");

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.write(datosJson.toString().getBytes("UTF-8"));
            wr.flush();
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();

            if ((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == HttpURLConnection.HTTP_CREATED)) {
                InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                result = converInputStreamToString(inputStream).toString();

            } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                InputStreamReader inputStream = new InputStreamReader(urlConnection.getErrorStream());
                result = converInputStreamToString(inputStream).toString();
            } else {
                result = "NO_OK";
            }
            mException = null;
            wr.close();
            urlConnection.disconnect();

            return result;

        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void ejecutarPost(String uri, JSONObject datosJson, String operation, String tkn) {
        String result = POST(uri, datosJson, tkn);

        if (result == null) {
            return;
        }
        if (result == "NO_OK") {
            return;
        }
        Intent i = new Intent(operation);
        i.putExtra("datosJson", result);
        sendBroadcast(i);
    }
}
