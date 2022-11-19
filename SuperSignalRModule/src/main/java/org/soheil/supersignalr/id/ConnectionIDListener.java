package org.soheil.supersignalr.id;

public interface ConnectionIDListener {

   void onGotID (String connectionID);
   void onError (Exception e);
}
