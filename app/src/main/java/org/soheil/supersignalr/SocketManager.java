package org.soheil.supersignalr;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.soheil.supersignalr.hub.HubConnectionListener;
import org.soheil.supersignalr.services.SignalRService;

public class SocketManager {

    private static final String TAG = "SocketManager";

    private SignalRService mService;
    private boolean mBound = false;

    private HubConnectionListener listener;

    public SocketManager() {

    }

    public void start (HubConnectionListener listener) {
        this.listener = listener;

        if(mService != null) {
            startSignalR();
        }else {
            Intent intent = new Intent();
            intent.setClass(App.getInstance().getContext(), SignalRService.class);
            App.getInstance().getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }

    }

    public void stop () {
        // Log.e(TAG, "stop: !!!" );
        App.getInstance().getContext().unbindService(this.mConnection);
        if(mService != null)
            mService.closeConnection();
    }

    public void sendMessage (String event, Object... parameters) {
        if(mService != null)
            mService.sendMessage(event , parameters);
    }

    public boolean isConnected () {
        return mService.isConnected();
    }


    private void startSignalR () {
        mService.setCustomListener(listener);
        mService.startSignalR();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            mService = binder.getService();
            startSignalR();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}





