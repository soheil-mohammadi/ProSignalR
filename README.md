<img src="https://miro.medium.com/max/720/0*ILbItnzDfSZhZwSn.png" alt="SignalR logo" title="SignalR" align="right" height="60" />

# ProSignalR

A Powerful library for Making SignalR Connection Over Android Platform :) <br />
It has got  __Auto Reconnection__ feature as well . <br />
Enjoy it 🔥


:star: Star us on GitHub — it motivates us a lot!

## Table of content

- [Installation](#installation)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Contribution](#contribution-and-issues)
- [Links](#links)

## Installation

Please add this into your build.gradle file (app module) :

```groovy
implementation 'com.enefce.libraries.prosignalr:1.0.0'
```

## Prerequisites

You should have a SignalR server which was powered by asp .net core.


## Getting Started

You can follow all of these next steps from sample app :)

* The first thing that we need to do is to initialize the library so add this line of code to your main application class of project :
```java
 @Override
   public void onCreate() {
      super.onCreate();
      instance = this;
      SuperSignalR.init(this);
   }
   ```

* Now we need to create a service class to manage our socket connection :

```java
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
            this.customListener.onConnected();

        Log.e(TAG, "onConnected: !!!" );
    }

    @Override
    public void onDisconnected() {

        isConnected = false;

        if(this.customListener != null)
            this.customListener.onDisconnected();
        Log.e(TAG, "onDisconnected: !!!" );
    }

    @Override
    public void onMessage(HubMessage message) {
        if(this.customListener != null)
            this.customListener.onMessage(message);
        Log.e(TAG, "onMessage: " + message.toString() );
    }

    @Override
    public void onError(Exception exception) {
        if(this.customListener != null)
            this.customListener.onError(exception);
        Log.e(TAG, "onError: "  + exception.getMessage() );
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


    private void startSignalR() {

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
   ```

* In above class we needed to create two important variables :
```bash 
HUB_URL
```
and 
```bash 
USER_TOKEN
```

You should replace your values with them .


## Contribution and Issues

If you would like to participate in this project please create issue or use [Links](#links) section.


## Links

* [Contact](https://t.me/soheil_4ever)
* [Issue tracker](https://github.com/soheil-mohammadi/ProSignalR/issues)
* [Source code](https://github.com/soheil-mohammadi/ProSignalR)