package org.soheil.supersignalr;

import android.app.Application;
import android.content.Context;

public class App extends Application {


   private static App instance ;

   @Override
   public void onCreate() {
      super.onCreate();
      instance = this;
      SuperSignalR.init(this);
   }


   public static App getInstance() {
      return instance;
   }

   public Context getContext () {
      return getApplicationContext();
   }
}
