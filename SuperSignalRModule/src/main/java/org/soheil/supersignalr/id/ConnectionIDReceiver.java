package org.soheil.supersignalr.id;

import android.net.Uri;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.signalr.InputStreamConverter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class ConnectionIDReceiver {

    private static final String TAG = "ConnectionIDReceiver";

    public static final int TIMEOUT_MS = 15000 ;

    private Gson gson ;
    private Uri parsedUri;
    private String authHeader ;

    public ConnectionIDReceiver(Uri parsedUri , String authHeader) {
        this.gson = new Gson();
        this.parsedUri = parsedUri;
        this.authHeader = authHeader;
    }

    public void fetchID (ConnectionIDListener connectionIDListener) {

        Log.e(TAG, "Requesting connection id...");

        if (!(parsedUri.getScheme().equals("http") || parsedUri.getScheme().equals("https")))
            throw new RuntimeException("URL must start with http or https");


        try {
            String negotiateUri = parsedUri.buildUpon().appendPath("negotiate").build().toString();
            HttpURLConnection connection = (HttpURLConnection) new URL(negotiateUri).openConnection();
            if (authHeader != null && !authHeader.isEmpty()) {
                connection.addRequestProperty("Authorization", authHeader);
            }

            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestMethod("POST");
            int responseCode = connection.getResponseCode();

             // Log.e(TAG, "Response Code :: " + responseCode);

            if (responseCode == 200) {
                String result = InputStreamConverter.convert(connection.getInputStream());
                JsonElement jsonElement = gson.fromJson(result, JsonElement.class);
                String connectionId = jsonElement.getAsJsonObject().get("connectionId").getAsString();
                JsonElement availableTransportsElements = jsonElement.getAsJsonObject().get("availableTransports");
                List<JsonElement> availableTransports = Arrays.asList(gson.fromJson(availableTransportsElements, JsonElement[].class));
                boolean webSocketAvailable = false;
                for (JsonElement element : availableTransports) {
                    if (element.getAsJsonObject().get("transport").getAsString().equals("WebSockets")) {
                        webSocketAvailable = true;
                        break;
                    }
                }
                if (!webSocketAvailable) {
                    throw new RuntimeException("The server does not support WebSockets transport");
                }

                connectionIDListener.onGotID(connectionId);
            } else if (responseCode == 401) {
                //   Log.e(TAG, "Error :: 0000");
                throw new RuntimeException("Unauthorized request");
            } else {
            //    Log.e(TAG, "Error :: 1111");
                throw new RuntimeException("Server error");
            }
        } catch (Exception e) {
            connectionIDListener.onError(e);
        }
    }
}
