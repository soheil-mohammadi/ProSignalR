package org.soheil.supersignalr;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {

   private static Util instance ;

   public Util() {
   }

   public static Util builder () {
      if(instance == null)
         instance = new Util();
      return instance;
   }


   public boolean isNetworkAvailable() {

      ConnectivityManager connectivityManager
              = (ConnectivityManager) SuperSignalR.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
      if(connectivityManager != null) {
         NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
         return activeNetworkInfo != null && activeNetworkInfo.isConnected();
      }

      return false;

   }


   public ConnectivityManager getConnectivityService () {
     return (ConnectivityManager) SuperSignalR.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
   }

}
