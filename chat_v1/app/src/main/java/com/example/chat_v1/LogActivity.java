package com.example.chat_v1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


public class LogActivity extends AppCompatActivity {

    private Button boutonConnexion = null;
    private String pseudo;
    private String mdp;
    private String typeConnexion = "connexion";
    private RadioButton button_login;
    private RadioButton button_subscribe;
    private BroadcastReceiver activeSessionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int reponseActiveSession = intent.getIntExtra(SocketService.ReponseActiveSession, 0);
            if(reponseActiveSession == 1){
                Intent intentChat = new Intent(getApplicationContext(), MainActivity.class);
                intentChat.putExtra(SocketService.Pseudo, pseudo);
                intentChat.putExtra(SocketService.MotDePasse, mdp);
                startActivity(intentChat);
            }
            else{
                setEnabledGraphicElement(true);
                if(typeConnexion.equals("connexion")){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.incorrect_pseudo_or_password), Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.already_exist_pseudo), Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_log);

        this.button_login = ((RadioButton)findViewById(R.id.se_connecter));
        this.button_subscribe = ((RadioButton)findViewById(R.id.inscription));

        boutonConnexion = (Button) findViewById(R.id.boutonConnexion);
        boutonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pseudo = ((EditText) findViewById(R.id.pseudoLog)).getText().toString();
                mdp = ((EditText) findViewById(R.id.mdpLog)).getText().toString();
                System.out.println("Activité : " + pseudo + " " + mdp);

                if (!pseudo.equals("") && !mdp.equals("")) {
                    setEnabledGraphicElement(false);
                    Intent serviceSocket = new Intent(LogActivity.this, SocketService.class);
                    serviceSocket.setAction(SocketService.ACTION_ACTIVE_SESSION);
                    serviceSocket.putExtra(SocketService.Pseudo, pseudo);
                    serviceSocket.putExtra(SocketService.MotDePasse, mdp);
                    serviceSocket.putExtra(SocketService.TypeConnexion, typeConnexion);
                    startService(serviceSocket);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.need_fields_pseudo_password), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    public void onResume() {
        super.onResume();

        setEnabledGraphicElement(true);

        IntentFilter filter = new IntentFilter();
        filter.addAction(SocketService.ACTION_ACTIVE_SESSION);
        registerReceiver(activeSessionReceiver, filter);
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(activeSessionReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(LogActivity.this, SocketService.class));
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.se_connecter:
                if(checked)
                    typeConnexion = "connexion";
                    break;
            case R.id.inscription:
                if(checked)
                    typeConnexion = "inscription";
                    break;
        }
    }

    private void setEnabledGraphicElement(boolean b){
        findViewById(R.id.pseudoLog).setEnabled(b);
        findViewById(R.id.mdpLog).setEnabled(b);
        findViewById(R.id.se_connecter).setEnabled(b);
        findViewById(R.id.inscription).setEnabled(b);
    }
}
