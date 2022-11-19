package org.soheil.supersignalr.socket;


import org.soheil.supersignalr.hub.HubMessage;

public interface ConnectionSocketListener {

   void onOpened ();
   void onMessage (HubMessage hubMessage);
   void onDisconnected ();
   void onError (Exception e);
}
