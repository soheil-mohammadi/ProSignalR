package org.soheil.supersignalr.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.soheil.supersignalr.MainConnection;
import org.soheil.supersignalr.hub.HubConnection;
import org.soheil.supersignalr.hub.HubConnectionListener;
import org.soheil.supersignalr.hub.HubMessage;
import org.soheil.supersignalr.models.PlansModel;


public class SignalRService extends Service implements HubConnectionListener {

    private static final String TAG = "SignalRService";

    private final String HUB_URL = "http://196.164.2.9:3580/signalr/sessionHub";
    private final String USER_TOKEN = "ItNGMwOS1iYWEyLTEwYmE0MjI4YWE4OSIsImNlcnRzZXJpYWxudW1iZXIiOiJtYWNfYWRkcmVzc19vZl9waG9uZSIsInNlY3VyaXR5U3RhbXAiOiJlMTAxOWNiYy1jMjM2LTQ0ZTEtYjdjYy0zNjMxYTY";

    private HubConnection mHubConnection;
    private final IBinder mBinder = new LocalBinder();

    private HubConnectionListener customListener ;

    private boolean isConnected = false;


    public SignalRService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        startSignalR();
        return result;
    }

    @Override
    public void onDestroy() {
        closeConnection();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        startSignalR();
        return mBinder;
    }

    @Override
    public void onConnected() {

        isConnected = true;

        if(this.customListener != null)
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    SignalRService.this.customListener.onConnected();
                    Log.e(TAG, "onConnected: !!!" );
                }
            });

    }

    @Override
    public void onDisconnected() {

        isConnected = false;

        if(this.customListener != null)
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    SignalRService.this.customListener.onDisconnected();
                    Log.e(TAG, "onDisconnected: !!!" );
                }
            });
    }

    @Override
    public void onMessage(HubMessage message) {
        if(this.customListener != null)
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    SignalRService.this.customListener.onMessage(message);
                    Log.e(TAG, "onMessage: " + message.toString() );
                }
            });
    }

    @Override
    public void onError(Exception exception) {
        if(this.customListener != null)
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    SignalRService.this.customListener.onError(exception);
                    Log.e(TAG, "onError: "  + exception.getMessage() );
                }
            });

    }


    public boolean isConnected() {
        return isConnected;
    }

    public class LocalBinder extends Binder {
        public SignalRService getService() {
            return SignalRService.this;
        }
    }


    public void setCustomListener(HubConnectionListener customListener) {
        this.customListener = customListener;
    }


    public void startSignalR() {

        mHubConnection = new MainConnection(HUB_URL, USER_TOKEN);

        mHubConnection.addListener(SignalRService.this);

        registerEvents ();
        startConnection();
    }


    private void startConnection () {
        try {
            mHubConnection.connect();
        } catch (Exception e) {

        }
    }

    public void closeConnection () {
        if (mHubConnection != null)
            try {
                mHubConnection.disconnect();
            }catch (Exception e) {

            }
    }


    public void sendMessage (String event, Object... parameters) {
        if (mHubConnection != null && isConnected())
            mHubConnection.invoke(event , parameters);
    }

    private void registerEvents () {
        if(mHubConnection == null)
            return;

        registerSimpleEvent ();
    }

    private <T extends Object> T getModelClass (JsonElement[] message , Class<T> classz ) {
        Gson gson = new Gson();
        String jsonBody =  gson.toJson(message[0]);
        return gson.fromJson(jsonBody  , classz);
    }

    private void registerSimpleEvent () {
        mHubConnection.subscribeToEvent("SimpleEvent", msg -> {

            PlansModel plan = getModelClass(msg.getArguments() , PlansModel.class);
            Log.e(TAG, "registerSimpleEvent: " + plan.toString() );

        });
    }
}