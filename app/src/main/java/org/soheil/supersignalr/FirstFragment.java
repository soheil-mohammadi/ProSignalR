package org.soheil.supersignalr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.snackbar.Snackbar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import org.soheil.supersignalr.databinding.FragmentFirstBinding;
import org.soheil.supersignalr.hub.HubConnectionListener;
import org.soheil.supersignalr.hub.HubMessage;

public class FirstFragment extends Fragment implements HubConnectionListener {

    private FragmentFirstBinding binding;
    private SocketManager socketManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnStartConnectionState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(socketManager != null && socketManager.isConnected()) {
                    socketManager.stop();
                }else {
                    binding.textviewConnectionState.setVisibility(View.VISIBLE);
                    initSocketConnection();
                }

            }
        });


        binding.fabSend.setOnClickListener(view1 -> {
            if(socketManager.isConnected()) {
                socketManager.sendMessage("Test" , "Simple Message");
                Snackbar.make(view1, "Sent Message !", Snackbar.LENGTH_LONG).show();
            }

        });

        binding.fabStop.setOnClickListener(view12 -> socketManager.stop());

    }

    private void initSocketConnection () {
        socketManager = new SocketManager();
        socketManager.start(this);
        binding.btnStartConnectionState.setClickable(false);
    }

    @Override
    public void onConnected() {
        binding.textviewConnectionState.setText("Connected To Socket !");
        binding.btnStartConnectionState.setText("Tap to disconnect");
        binding.btnStartConnectionState.setClickable(true);

    }

    @Override
    public void onDisconnected() {
        binding.textviewConnectionState.setVisibility(View.GONE);
        binding.btnStartConnectionState.setClickable(true);
        Snackbar.make(binding.getRoot(), "Disconnected From Socket !", Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onMessage(HubMessage message) {
        Snackbar.make(binding.getRoot(), "New Message :: " + message.toString(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onError(Exception exception) {
        binding.textviewConnectionState.setVisibility(View.GONE);
        binding.btnStartConnectionState.setClickable(true);
        Snackbar.make(binding.getRoot(), "Error happened From Socket :: " + exception.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}