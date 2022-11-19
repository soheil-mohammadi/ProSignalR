package org.soheil.supersignalr;

import android.content.Context;

public class SuperSignalR {

   private static  Context context;

   public static void init (Context context) {
      SuperSignalR.context = context;
   }

   public static Context getContext() {
      return context;
   }
}
