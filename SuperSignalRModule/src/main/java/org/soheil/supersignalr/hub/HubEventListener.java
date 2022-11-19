package org.soheil.supersignalr.hub;

public interface HubEventListener {
    void onEventMessage(HubMessage message);
}
