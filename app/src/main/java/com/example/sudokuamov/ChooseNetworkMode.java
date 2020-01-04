package com.example.sudokuamov;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sudokuamov.activities.helpers.HelperMethods;
import com.example.sudokuamov.sockets.SocketConnector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

public class ChooseNetworkMode extends AppCompatActivity implements View.OnClickListener {

    String mode, userName, userPhoto, userPhotoThumb;
    int difficulty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_network_mode);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(this, "O seu telemóvel não tem conectividade!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Intent intent = getIntent();
        //Single player or two players from menu activity
        mode = intent.getStringExtra("Mode");
        //Intent for difficulty passed from the level activity
        difficulty = intent.getIntExtra("Difficulty", 0);

        userName = intent.getStringExtra("nickName");
        userPhoto = intent.getStringExtra("userPhotoPath");
        userPhotoThumb = intent.getStringExtra("userPhotoThumbPath");

        setButtonActions();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setButtonActions() {
        Button server = findViewById(R.id.Server);
        server.setOnClickListener(this);

        Button client = findViewById(R.id.Client);
        client.setOnClickListener(this);

    }


    private void startGame() {
        final Intent intent = HelperMethods.makeIntentForUserNameAndPhoto(new String[]{userName, userPhoto, userPhotoThumb}, this, GameActivity.class);

        intent.putExtra("Mode", mode);
        intent.putExtra("Difficulty", difficulty);

        startActivity(intent);
    }

    @Override
    protected void onStop() {
        SocketConnector.getInstance().removeObserver(this);
        finish();
        super.onStop();
    }

    @Override
    public void onClick(View view) {

        final SocketConnector socketConnector = SocketConnector.getInstance();


        socketConnector.setBaseDir(getExternalFilesDir(null).toString());

        socketConnector.setObserver(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                startGame();
            }
        });

        switch (view.getId()) {
            case R.id.Server: {

                socketConnector.WaitforClients().start();

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Waiting For Player To Connect!")
                        .setMessage("IP : " + SocketConnector.getLocalIpAddress())
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                socketConnector.closeServerSocket();
                            }
                        })
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                socketConnector.closeServerSocket();
                            }
                        })
                        .create();

                dialog.show();

                break;
            }
            case R.id.Client: {
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Insert Server IP")
                        .setMessage("Insert ip to Connect to Server")
                        .setView(taskEditText)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String value = String.valueOf(taskEditText.getText());
                                socketConnector.ConnectToServer(value).start();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                break;
            }
            default:
                break;
        }

    }
}
