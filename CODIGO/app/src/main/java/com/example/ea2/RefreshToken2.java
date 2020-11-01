package com.example.ea2;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class RefreshToken2 {
    public void refrescarToken(){

        String response = ejecutarRefresh();

        try {
            JSONObject datosJson = new JSONObject(response);
            boolean rta;
            rta = datosJson.getBoolean("success");
            if(rta) {
                Usuario usuario = Usuario.getInstance();
                usuario.setToken(datosJson.getString("token"));
                usuario.setToken_refresh(datosJson.getString("token_refresh"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String ejecutarRefresh() {
        HttpURLConnection urlConnection;
        Usuario usuario = Usuario.getInstance();
        String result = "";
        try {
            URL mUrl = new URL(Configuracion.URI_TOKEN);

            urlConnection = (HttpURLConnection) mUrl.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Authorization", "Bearer " + usuario.getToken_refresh());

            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            wr.write(result.toString().getBytes("UTF-8"));

            wr.flush();

            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if((responseCode == HttpURLConnection.HTTP_OK) || (responseCode == HttpURLConnection.HTTP_CREATED)) {
                InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                result = converInputStreamToString(inputStream).toString();
            } else if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
                InputStreamReader inputStream = new InputStreamReader(urlConnection.getErrorStream());
                result = converInputStreamToString(inputStream).toString();
            } else {
                result = "NO_OK";
            }

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

    private StringBuilder converInputStreamToString(InputStreamReader inputStream) throws IOException {
        BufferedReader br = new BufferedReader(inputStream);
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = br.readLine())!= null){
            result.append(line +"\n");
        }
        br.close();
        return result;
    }
}
