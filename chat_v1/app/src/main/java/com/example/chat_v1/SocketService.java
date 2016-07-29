package com.example.chat_v1;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;


public class SocketService extends Service {

    public static final int Notification_ID = 1;

    public static final String WriteMess = "com.example.chat_v1.WriteMess";
    public static final String TypeMess = "com.example.chat_v1.TypeMess";
    public static final String ACTION_WRITE_MESSAGE = "com.example.chat_v1.action.WriteMess";
    public static final String ReadMess = "com.example.chat_v1.ReadMess";
    public static final String ACTION_READ_MESS = "com.example.chat_v1.action.ReadMess";
    public static final String Pseudo = "com.example.chat_v1.Pseudo";
    public static final String NewPseudo = "com.example.chat_v1.NEwPseudo";
    public static final String MotDePasse = "com.example.chat_v1.MotDePasse";
    public static final String TypeConnexion = "com.example.chat_v1.TypeConnexion";
    public static final String DateMessage = "com.example.chat_v1.dateMessage";
    public static final String ReponseActiveSession = "com.example.chat_v1.ReponseActiveSession";
    public static final String ACTION_ACTIVE_SESSION = "com.example.chat_v1.action.ReponseActiveSession";
    public static final String Conversation = "com.example.chat_v1.conversation";
    public static final String ACTION_GET_CONVERSATION = "com.example.chat_v1.action.getConversation";
    public static final String UsersStateConnection = "com.example.chat_v1.usersStateConnection";
    public static final String ACTION_GET_USERS_STATE_CONNECTION = "com.example.chat_v1.action.getUsersStateConnection";
    public static final String StateConnection = "com.example.chat_v1.StateConnection";
    public static final String ACTION_SET_STATE_CONNECTION = "com.example.chat_v1.action.DesactiveSession";
    public static final String AnswerUnsubscribe = "com.example.chat_v1.answerUnsubscribe";
    public static final String ACTION_UNSUBSCRIBE = "com.example.chat_v1.action.Unsubscribe";
    public static final String PictureProfile = "com.example.chat_v1.pictureProfile";
    public static final String PicturesProfile = "com.example.chat_v1.picturesProfile";
    public static final String ACTION_SET_PICTURE_PROFILE = "com.example.chat_v1.action.SetPictureProfile";
    public static final String Profile = "com.example.chat_v1.Profile";
    public static final String ACTION_GET_PROFILE = "com.example.chat_v1.action.GetProfile";
    public static final String answerSetPseudo = "com.example.chat_v1.answerSetPseudo";
    public static final String ACTION_SET_PSEUDO = "com.example.chat_v1.action.SetPseudo";
    public static final String answerSetPassword = "com.example.chat_v1.answerSetPassword";
    public static final String ACTION_SET_PASSWORD = "com.example.chat_v1.action.SetPassword";
    public static final String Description = "com.example.chat_v1.description";
    public static final String ACTION_SET_DESCRIPTION = "com.example.chat_v1.action.SetDescription";

    private SocketIO socket = null;
    private JSONObject messRecJson;

    @Override
    public void onCreate() {
        try {
            socket = new SocketIO("http://192.168.43.76:8080/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        socket.connect(new IOCallback() {
            @Override
            public void onMessage(JSONObject json, IOAcknowledge ack) {
            }

            @Override
            public void onMessage(String data, IOAcknowledge ack) {
            }

            @Override
                public void onError(SocketIOException socketIOException) {
                System.out.println("Erreur de connection");
                socketIOException.printStackTrace();
                stopService(new Intent(SocketService.this, SocketService.class));
            }

            @Override
            public void onDisconnect() {
                System.out.println("Connection terminée.");
            }

            @Override
            public void onConnect() {
                System.out.println("Connection établie");
            }

            @Override
            public void on(String event, IOAcknowledge ack, Object... args) {
                System.out.println("Evennement provenant du serveur '" + event + "'");
                Object[] arguments = args;
                messRecJson = (JSONObject) arguments[0];

                if(event.equals("user message")){
                    try {
                        String message = messRecJson.get("message").toString();
                        String pseudo = messRecJson.get("pseudo").toString();
                        String typeMess = messRecJson.get("type_message").toString();
                        String temps = messRecJson.get("temps").toString();
                        String picture_profile = messRecJson.get("picture_profile").toString();

                        sendMessageBroadcast(message, typeMess, pseudo, temps, picture_profile);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.background)
                                .setContentTitle(getResources().getString(R.string.notification_message_from) + " " + pseudo)
                                .setWhen(System.currentTimeMillis())
                                .setVibrate(new long[]{1000, 500, 500, 500, 1000})
                                .setLights(Color.GREEN, 3000, 3000)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setPriority(Notification.PRIORITY_MAX
                                )
                                .setAutoCancel(true);
                        if(typeMess.equals("texte")){
                            mBuilder.setContentText(message);
                        }
                        if(typeMess.equals("image")){
                            mBuilder.setContentText(getResources().getString(R.string.notification_a_picture));
                        }
                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        Notification notif = mBuilder.build();
                        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(Notification_ID, notif);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(event.equals("user log")){
                    try {
                        String answer = messRecJson.get("answer").toString();
                        //System.out.println("ANSWER LOG = " + answer);
                        if(answer.equals("true")){
                            sendReponseActiveSessionBroadcast(1);
                        }
                        else {
                            sendReponseActiveSessionBroadcast(0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(event.equals("user conversation")){
                    try {
                        JSONArray conversation = messRecJson.getJSONArray("conversation");
                        JSONArray pictures_profile = messRecJson.getJSONArray("pictures_profile");
                        //System.out.println("CONVERSATION = " + conversation.toString());
                        sendConversationBroadcast(conversation.toString(), pictures_profile.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(event.equals("users get_state_connection")){
                    try {
                        JSONArray usersStateConnection = messRecJson.getJSONArray("users");
                        //System.out.println("USERS STATE CONNECTION = " + usersStateConnection.toString());
                        sendUsersStateConnectionBroadcast(usersStateConnection.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(event.equals("user unsubscribe")){
                    try {
                        String answer = messRecJson.get("answer").toString();
                        //System.out.println("ANSWER UNSUBSCRIBE = " + answer);
                        sendUserUnsubscribeBroadcast(answer);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(event.equals("user picture_profile")){
                    try {
                        String pseudo = messRecJson.get("pseudo").toString();
                        String  picture_profile = messRecJson.get("picture_profile").toString();
                        //System.out.println("PSEUDO = " + pseudo);
                        //System.out.println("PICTURE_PROFILE = " + picture_profile);
                        sendUserPictureProfile(pseudo, picture_profile);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(event.equals("user get profile")){
                    try {
                        JSONArray profile = messRecJson.getJSONArray("profile");
                        sendUserProfile(profile.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(event.equals("user set pseudo")){
                    try {
                        String newPseudo = messRecJson.get("newPseudo").toString();
                        String answer = messRecJson.get("answer").toString();
                        sendUserNewPseudo(newPseudo, answer);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(event.equals("user set password")){
                    try {
                        String password = messRecJson.get("mdp").toString();
                        String answer = messRecJson.get("answer").toString();
                        sendUserNewPassword(password, answer);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            return 0;
        }
        String typeAction = intent.getAction().toString();
        String pseudo;
        switch(typeAction){
            case ACTION_ACTIVE_SESSION :
                ActiveSessionThread activeSessionThread = new ActiveSessionThread(intent);
                activeSessionThread.execute();
                break;
            case ACTION_WRITE_MESSAGE :
                WriteMessageThread writeMessageThread = new WriteMessageThread(intent);
                writeMessageThread.execute();
                break;
            case ACTION_GET_CONVERSATION :
                GetConversationThread getConversationThread = new GetConversationThread();
                getConversationThread.execute();
                break;
            case ACTION_GET_USERS_STATE_CONNECTION :
                GetUsersStateConnectionThread getUsersStateConnectionThread = new GetUsersStateConnectionThread();
                getUsersStateConnectionThread.execute();
                break;
            case ACTION_SET_STATE_CONNECTION :
                pseudo = intent.getStringExtra(Pseudo);
                int stateConnection = intent.getIntExtra(StateConnection, 0);
                try {
                    JSONObject json = new JSONObject();
                    json.putOpt("pseudo", pseudo);
                    json.putOpt("stateConnection", stateConnection);
                    socket.emit("user set_state_connection", json);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                break;
            case ACTION_UNSUBSCRIBE :
                UnsubscribeThread unsubscribeThread = new UnsubscribeThread(intent);
                unsubscribeThread.execute();
                break;
            case ACTION_SET_PICTURE_PROFILE :
                SetPictureProfileThread setPictureProfileThread = new SetPictureProfileThread(intent);
                setPictureProfileThread.execute();
                break;
            case ACTION_GET_PROFILE :
                GetProfileThread getProfileThread = new GetProfileThread(intent);
                getProfileThread.execute();
                break;
            case ACTION_SET_PSEUDO :
                SetPseudoThread setPseudoThread = new SetPseudoThread(intent);
                setPseudoThread.execute();
                break;
            case ACTION_SET_PASSWORD :
                SetPasswordThread setPasswordThread = new SetPasswordThread(intent);
                setPasswordThread.execute();
                break;
            case ACTION_SET_DESCRIPTION :
                SetDescriptionThread setDescriptionThread = new SetDescriptionThread(intent);
                setDescriptionThread.execute();
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    private void sendMessageBroadcast(String message, String typeMess, String pseudo, String temps, String picture_profile) {
        Intent intent = new Intent();
        intent.setAction(ACTION_READ_MESS);
        intent.putExtra(ReadMess, message);
        intent.putExtra(TypeMess, typeMess);
        intent.putExtra(Pseudo, pseudo);
        intent.putExtra(DateMessage, temps);
        intent.putExtra(PictureProfile, picture_profile);
        sendBroadcast(intent);
    }

    private void sendReponseActiveSessionBroadcast(int reponse) {
        Intent intent = new Intent();
        intent.setAction(ACTION_ACTIVE_SESSION);
        intent.putExtra(ReponseActiveSession, reponse);
        sendBroadcast(intent);
    }

    private void sendConversationBroadcast(String conversation, String pictures_profile){
        Intent intent = new Intent();
        intent.setAction(ACTION_GET_CONVERSATION);
        intent.putExtra(Conversation, conversation);
        intent.putExtra(PicturesProfile, pictures_profile);
        sendBroadcast(intent);
    }

    private void sendUsersStateConnectionBroadcast(String usersStateConnection){
        Intent intent = new Intent();
        intent.setAction(ACTION_GET_USERS_STATE_CONNECTION);
        intent.putExtra(UsersStateConnection, usersStateConnection);
        sendBroadcast(intent);
    }

    private void sendUserUnsubscribeBroadcast(String answer){
        Intent intent = new Intent();
        intent.setAction(ACTION_UNSUBSCRIBE);
        intent.putExtra(AnswerUnsubscribe, answer);
        sendBroadcast(intent);
    }

    private void sendUserPictureProfile(String pseudo, String picture_profile){
        Intent intent = new Intent();
        intent.setAction(ACTION_SET_PICTURE_PROFILE);
        intent.putExtra(Pseudo, pseudo);
        intent.putExtra(PictureProfile, picture_profile);
        sendBroadcast(intent);
    }

    private void sendUserProfile(String profile){
        Intent intent = new Intent();
        intent.setAction(ACTION_GET_PROFILE);
        intent.putExtra(Profile, profile);
        sendBroadcast(intent);
    }

    private void sendUserNewPseudo(String newPseudo, String answer){
        Intent intent = new Intent();
        intent.setAction(ACTION_SET_PSEUDO);
        intent.putExtra(NewPseudo, newPseudo);
        intent.putExtra(answerSetPseudo, answer);
        sendBroadcast(intent);
    }

    private void sendUserNewPassword(String password, String answer){
        Intent intent = new Intent();
        intent.setAction(ACTION_SET_PASSWORD);
        intent.putExtra(MotDePasse, password);
        intent.putExtra(answerSetPassword, answer);
        sendBroadcast(intent);
    }

    private class ActiveSessionThread extends AsyncTask<Void, Void, Void> {
        protected Intent intent;

        public ActiveSessionThread(Intent intent){
            this.intent = intent;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String pseudo = intent.getStringExtra(Pseudo);
            String mdp = intent.getStringExtra(MotDePasse);
            String typeConnexion = intent.getStringExtra(TypeConnexion);
            try {
                JSONObject json = new JSONObject();
                json.putOpt("pseudo", pseudo);
                json.putOpt("mdp", mdp);
                json.putOpt("typeConnexion", typeConnexion);
                socket.emit("user log", json);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private class WriteMessageThread extends AsyncTask<Void, Void, Void> {
        protected Intent intent;

        public WriteMessageThread(Intent intent){
            this.intent = intent;
        }

        @Override                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
        protected Void doInBackground(Void... params) {
            String mess = intent.getStringExtra(WriteMess);
            String pseudo = intent.getStringExtra(Pseudo);
            String typeMess = intent.getStringExtra(TypeMess);
            String temps = intent.getStringExtra(DateMessage);
            try {
                JSONObject json = new JSONObject();
                json.putOpt("message", mess);
                json.putOpt("pseudo", pseudo);
                json.putOpt("type_message", typeMess);
                json.putOpt("temps", temps);
                socket.emit("user message", json);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private class GetConversationThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject json = new JSONObject();
            socket.emit("user conversation", json);
            return null;
        }
    }

    private class GetUsersStateConnectionThread extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject json = new JSONObject();
            socket.emit("users get_state_connection", json);
            return null;
        }
    }

    private class UnsubscribeThread extends AsyncTask<Void, Void, Void> {
        private Intent intent;

        public UnsubscribeThread(Intent intent){
            this.intent = intent;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String pseudo = intent.getStringExtra(Pseudo);
            try {
                JSONObject json = new JSONObject();
                json.putOpt("pseudo", pseudo);
                socket.emit("user unsubscribe", json);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private class SetPictureProfileThread extends AsyncTask<Void, Void, Void> {
        private Intent intent;

        public SetPictureProfileThread(Intent intent){
            this.intent = intent;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String pseudo = intent.getStringExtra(Pseudo);
            String pictureProfile = intent.getStringExtra(PictureProfile);
            try {
                JSONObject json = new JSONObject();
                json.putOpt("pseudo", pseudo);
                json.putOpt("picture_profile", pictureProfile);
                socket.emit("user picture_profile", json);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private class GetProfileThread extends AsyncTask<Void, Void, Void> {
        private Intent intent;

        public GetProfileThread(Intent intent){
            this.intent = intent;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String pseudo = intent.getStringExtra(Pseudo);
            try {
                JSONObject json = new JSONObject();
                json.putOpt("pseudo", pseudo);
                socket.emit("user get profile", json);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private class SetPseudoThread extends AsyncTask<Void, Void, Void> {
        private Intent intent;

        public SetPseudoThread(Intent intent){
            this.intent = intent;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String pseudo = intent.getStringExtra(Pseudo);
            String newPseudo = intent.getStringExtra(NewPseudo);
            try {
                JSONObject json = new JSONObject();
                json.putOpt("pseudo", pseudo);
                json.putOpt("newPseudo", newPseudo);
                socket.emit("user set pseudo", json);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private class SetPasswordThread extends AsyncTask<Void, Void, Void> {
        private Intent intent;

        public SetPasswordThread(Intent intent){
            this.intent = intent;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String pseudo = intent.getStringExtra(Pseudo);
            String password = intent.getStringExtra(MotDePasse);
            try {
                JSONObject json = new JSONObject();
                json.putOpt("pseudo", pseudo);
                json.putOpt("password", password);
                socket.emit("user set password", json);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private class SetDescriptionThread extends AsyncTask<Void, Void, Void> {
        private Intent intent;

        public SetDescriptionThread(Intent intent){
            this.intent = intent;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String pseudo = intent.getStringExtra(Pseudo);
            String description = intent.getStringExtra(Description);
            try {
                JSONObject json = new JSONObject();
                json.putOpt("pseudo", pseudo);
                json.putOpt("description", description);
                socket.emit("user set description", json);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }
}
