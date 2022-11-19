package org.soheil.supersignalr;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.soheil.supersignalr.databinding.ActivityMainBinding;
import org.soheil.supersignalr.hub.HubConnectionListener;
import org.soheil.supersignalr.hub.HubMessage;

public class MainActivity extends AppCompatActivity implements HubConnectionListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private SocketManager socketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        initSocketConnection();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketManager.sendMessage("Test" , "Simple Message");
                Snackbar.make(view, "Sent Message !", Snackbar.LENGTH_LONG).show();
            }
        });

        binding.fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketManager.stop();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void initSocketConnection () {
        socketManager = new SocketManager();
        socketManager.start(this);
    }

    @Override
    public void onConnected() {
        Snackbar.make(binding.getRoot(), "Connected To Socket !", Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onDisconnected() {
        Snackbar.make(binding.getRoot(), "Disconnected From Socket !", Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onMessage(HubMessage message) {
        Snackbar.make(binding.getRoot(), "New Message :: " + message.toString(), Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void onError(Exception exception) {
        Snackbar.make(binding.getRoot(), "Error happened From Socket :: " + exception.getMessage(), Snackbar.LENGTH_LONG).show();
    }
}