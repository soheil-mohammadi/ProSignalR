package org.soheil.supersignalr.socket;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.soheil.supersignalr.SignalRMessage;
import org.soheil.supersignalr.Util;
import org.soheil.supersignalr.hub.HubMessage;
import org.soheil.supersignalr.id.ConnectionIDListener;
import org.soheil.supersignalr.id.ConnectionIDReceiver;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.net.ssl.SSLSocketFactory;

public class ConnectionSocket {

   private static final String TAG = "ConnectionSocket";
   private static String SPECIAL_SYMBOL = "\u001E";

   private Gson gson ;
   private Uri parsedUri;
   private String authHeader ;
   private String connectionId ;

   private ConnectionIDReceiver connectionIDReceiver;
   private WebSocketClient client;

   private ConnectionSocketListener connectionSocketListener ;
   private boolean isClosedWithException = false;
   private ConnectivityManager.NetworkCallback networkCallback ;

   public ConnectionSocket(Uri parsedUri , String authToken , ConnectionSocketListener connectionSocketListener) {
      this.gson = new Gson();
      this.parsedUri = parsedUri;
      this.authHeader = authToken;
      this.connectionSocketListener = connectionSocketListener;
      this.connectionIDReceiver = new ConnectionIDReceiver(parsedUri , authToken);
   }


   public boolean isConnected() {
      return client.isOpen();
   }


   public void connect() {
      if (client != null && (client.isOpen() || client.isConnecting()))
         return;

      registerNetworkConnectivityChanges();

      if(!Util.builder().isNetworkAvailable()) {
         isClosedWithException = true;
         return;
      }

      Runnable runnable;
      if (connectionId == null) {
         runnable = new Runnable() {
            public void run() {
               getConnectionId();
            }
         };
      } else {
         runnable = new Runnable() {
            public void run() {
               start();
            }
         };
      }
      new Thread(runnable).start();
   }

   private void getConnectionId() {

      this.connectionIDReceiver.fetchID(new ConnectionIDListener() {
         @Override
         public void onGotID(String id) {
            connectionId = id;
            start();
         }

         @Override
         public void onError(Exception e) {
            connectionSocketListener.onError(e);
         }
      });

   }

   private void start () {
      //Log.e(TAG, "start: !!!" );
      Uri.Builder uriBuilder = parsedUri.buildUpon();
      uriBuilder.appendQueryParameter("id", connectionId);
      uriBuilder.scheme(parsedUri.getScheme().replace("http", "ws"));
      Uri uri = uriBuilder.build();
      Map<String, String> headers = new HashMap<>();
      if (authHeader != null && !authHeader.isEmpty()) {
         headers.put("Authorization", authHeader);
      }
      try {
         client = new WebSocketClient(new URI(uri.toString()), new Draft_6455(), headers, ConnectionIDReceiver.TIMEOUT_MS) {
            @Override
            public void onOpen(ServerHandshake handshakeData) {
              // Log.e(TAG, "Opened");
               connectionSocketListener.onOpened();
               send("{\"protocol\":\"json\",\"version\":1}" + SPECIAL_SYMBOL);
            }

            @Override
            public void onMessage(String message) {
               //  Log.e(TAG, message);
               String[] messages = message.split(SPECIAL_SYMBOL);
               for (String m : messages) {
                  SignalRMessage element = gson.fromJson(m, SignalRMessage.class);
                  Integer type = element.getType();
                  if (type != null && type == 1) {

                     HubMessage hubMessage = new HubMessage(element.getInvocationId(), element.getTarget(), element.getArguments());
                     connectionSocketListener.onMessage(hubMessage);
                  }
               }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
            //   Log.e(TAG, String.format("Closed. Code: %s, Reason: %s, Remote: %s", code, reason, remote));

               if(code == 1006) {
//                Log.e(TAG, "Internet iternet connection !! " );
                  isClosedWithException = true;
                  disconnect();
               }else {
                  isClosedWithException = false;
               }

               connectionSocketListener.onDisconnected();
               connectionId = null;
            }

            @Override
            public void onError(Exception ex) {
               //  Log.e(TAG, "Error " + ex.getMessage());
               connectionSocketListener.onError(ex);
            }
         };

         if (parsedUri.getScheme().equals("https")) {
            client.setSocket(SSLSocketFactory.getDefault().createSocket());
         }
      } catch (Exception e) {
         connectionSocketListener.onError(e);
      }
      //   Log.i(TAG, "Connecting...");
      client.connect();
   }

   public void disconnect () {
      Runnable runnable = new Runnable() {
         public void run() {
            if (client != null && !(client.isClosed() || client.isClosing()))
               client.close();

            if(!isClosedWithException) {
               unregisterNetworkConnectivityChanges();
            }
         }
      };
      new Thread(runnable).start();
   }



   public void invoke(String event, Object... parameters) {

      if (client == null || !client.isOpen()) {
         throw new RuntimeException("Not connected");
      }
      final Map<String, Object> map = new HashMap<>();
      map.put("type", 1);
      map.put("invocationId", UUID.randomUUID());
      map.put("target", event);
      map.put("arguments", parameters);
      map.put("nonblocking", false);
      Runnable runnable = new Runnable() {
         public void run() {
            try {
               client.send(gson.toJson(map) + SPECIAL_SYMBOL);
            } catch (Exception e) {
               connectionSocketListener.onError(e);
            }
         }
      };
      new Thread(runnable).start();
   }


   private void registerNetworkConnectivityChanges () {
      if(networkCallback != null)
         return;

      ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
         @Override
         public void onAvailable(Network network) {
            //Log.e(TAG, "onAvailable: !!!");
            if(isClosedWithException) {
               isClosedWithException = false;
               new Handler().postDelayed(new Runnable() {
                  @Override
                  public void run() {
                     //Reconnection is here :)
                     //Log.e(TAG, "Reconnection !!! " );
                     connect();
                  }
               } , 2500);
            }
         }

         @Override
         public void onLost(Network network) {
            // Log.e(TAG, "onLost: !!!" );
         }
      };

      this.networkCallback = networkCallback;
      ConnectivityManager connectivityManager = Util.builder().getConnectivityService();

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         connectivityManager.registerDefaultNetworkCallback(networkCallback);
      } else {
         NetworkRequest request = new NetworkRequest.Builder()
                 .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
         connectivityManager.registerNetworkCallback(request, networkCallback);
      }

   }

   private void unregisterNetworkConnectivityChanges () {

      if(networkCallback != null) {
         ConnectivityManager connectivityManager = Util.builder().getConnectivityService();
         connectivityManager.unregisterNetworkCallback(networkCallback);
      }

   }
}
