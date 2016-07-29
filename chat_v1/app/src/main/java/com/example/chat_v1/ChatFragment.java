package com.example.chat_v1;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ChatFragment extends Fragment implements SensorEventListener {


    public static final int REQUEST_CODE_GALLERY = 1;
    public static final int REQUEST_CODE_CAMERA = 2;

    private IMainActivity listenerActivity;
    private View rootView;
    private MessageAdapter messAdap;
    private ListView listMessageView;
    private EditText chatText;
    private ImageButton btnSend;
    private ImageButton btnOpenGallery;
    private ImageButton btnOpenCamera;
    private String message;
    private String pseudo;
    private String imgDecodableString;
    private View.OnTouchListener onTouchListener;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Boolean antiSpamWizzz;
    private Object lock = new Object();

    private BroadcastReceiver conversationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String conversation = intent.getStringExtra(SocketService.Conversation);
            String pictures_profile = intent.getStringExtra(SocketService.PicturesProfile);
            try {
                JSONArray pictures_profileJson = new JSONArray(pictures_profile);
                JSONObject userJson;
                String user;
                String picture_profile;
                Bitmap picture_profileBMP = null;

                JSONArray conversationJson = new JSONArray(conversation);
                JSONObject messageJson;
                String auteur;
                String message;
                String typeMess;
                String temps;
                for(int i = 0; i < conversationJson.length(); i++){
                    messageJson = (JSONObject) conversationJson.get(i);
                    auteur = messageJson.get("auteur").toString();
                    message = messageJson.get("message").toString();
                    typeMess = messageJson.get("type_message").toString();
                    temps = messageJson.get("temps").toString();
                    for(int j = 0; j < pictures_profileJson.length(); j++){
                        userJson = (JSONObject) pictures_profileJson.get(j);
                        user = userJson.get("pseudo").toString();
                        if(auteur.equals(user)){
                            picture_profile = userJson.get("picture").toString();
                            if(!picture_profile.equals("NULL")){
                                picture_profileBMP = decodeImage(picture_profile);
                            }
                            if(auteur.equals(pseudo)){
                                if(typeMess.equals("texte")){
                                    messAdap.add(new Message(message, null, pseudo, Message.Side.RIGHT, temps, null));
                                }
                                else{
                                    Bitmap bmp = decodeImage(message);
                                    messAdap.add(new Message(null, bmp, pseudo, Message.Side.RIGHT, temps, null));
                                }
                            }
                            else{
                                if(typeMess.equals("texte")){

                                    messAdap.add(new Message(message, null, auteur, Message.Side.LEFT, temps, picture_profileBMP));
                                }
                                else{
                                    Bitmap bmp = decodeImage(message);
                                    messAdap.add(new Message(null, bmp, auteur, Message.Side.LEFT, temps, picture_profileBMP));
                                }
                            }
                            picture_profileBMP = null;
                            break;
                        }
                    }
                }
                scrollToBottom();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setEnabledGraphicElement(true);
        }
    };
    private BroadcastReceiver chatMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String readMess = intent.getStringExtra(SocketService.ReadMess);
            String pseudoIncoming = intent.getStringExtra(SocketService.Pseudo);
            String typeMess = intent.getStringExtra(SocketService.TypeMess);
            String temps = intent.getStringExtra(SocketService.DateMessage);
            String picture_profile = intent.getStringExtra(SocketService.PictureProfile);
            Bitmap picture_profileBMP = null;
            if(!picture_profile.equals("NULL")){
                picture_profileBMP = decodeImage(picture_profile);
            }
            if(typeMess.equals("texte")){
                messAdap.add(new Message(readMess, null, pseudoIncoming, Message.Side.LEFT, temps, picture_profileBMP));
            }
            else{
                messAdap.add(new Message(null, decodeImage(readMess), pseudoIncoming, Message.Side.LEFT, temps, picture_profileBMP));
            }
            scrollToBottom();
        }
    };

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                listenerActivity = (IMainActivity) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement IMainActivity");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.layout_chat, container, false);

        setEnabledGraphicElement(false);

        pseudo = listenerActivity.getPseudo();

        btnOpenGallery = (ImageButton) rootView.findViewById(R.id.button_open_gallery);
        btnOpenCamera = (ImageButton) rootView.findViewById(R.id.button_open_camera);
        btnSend = (ImageButton) rootView.findViewById(R.id.button_send);

        listMessageView = (ListView) rootView.findViewById(R.id.list_view_message);

        messAdap = new MessageAdapter(getActivity(), R.layout.layout_message_right, listMessageView);

        chatText = (EditText) rootView.findViewById(R.id.chat);

        onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                NotificationManager mNotifyMgr = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
                mNotifyMgr.cancel(SocketService.Notification_ID);
                return false;
            }
        };

        listMessageView.setOnTouchListener(onTouchListener);
        chatText.setOnTouchListener(onTouchListener);
        btnOpenGallery.setOnTouchListener(onTouchListener);
        btnOpenCamera.setOnTouchListener(onTouchListener);
        btnSend.setOnTouchListener(onTouchListener);

        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryintent, REQUEST_CODE_GALLERY);
            }
        });

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numCameras = Camera.getNumberOfCameras();
                if (numCameras > 0) {
                    Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraintent, REQUEST_CODE_CAMERA);
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!chatText.getText().toString().equals("")) {
                    message = chatText.getText().toString();
                    String time = getCurrentTime();
                    messAdap.add(new Message(message, null, pseudo, Message.Side.RIGHT, time, null));
                    scrollToBottom();
                    chatText.setText("");
                    sendMessageToServer(message, "texte", time);
                }
            }
        });

        listMessageView.setAdapter(messAdap);

        sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        antiSpamWizzz = true;

        return rootView;
    }

    void sendMessageToServer(String mess, String typeMess, String date){
        Intent serviceSocket = new Intent(getActivity(), SocketService.class);
        serviceSocket.setAction(SocketService.ACTION_WRITE_MESSAGE);
        serviceSocket.putExtra(SocketService.WriteMess, mess);
        serviceSocket.putExtra(SocketService.TypeMess, typeMess);
        serviceSocket.putExtra(SocketService.Pseudo, pseudo);
        serviceSocket.putExtra(SocketService.DateMessage, date);
        getActivity().startService(serviceSocket);
    }

    void getConversation(){
        Intent serviceSocket = new Intent(getActivity(), SocketService.class);
        serviceSocket.setAction(SocketService.ACTION_GET_CONVERSATION);
        getActivity().startService(serviceSocket);
    }

    public void onResume() {
        super.onResume();

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(SocketService.ACTION_GET_CONVERSATION);
        getActivity().registerReceiver(conversationReceiver, filter1);

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(SocketService.ACTION_READ_MESS);
        getActivity().registerReceiver(chatMessageReceiver, filter2);

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);

        setEnabledGraphicElement(false);
        messAdap.removeAll();
        messAdap.notifyDataSetChanged();
        getConversation();
    }

    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(conversationReceiver);
        getActivity().unregisterReceiver(chatMessageReceiver);

        sensorManager.unregisterListener(this, accelerometer);
    }

    private void setEnabledGraphicElement(boolean b){
        rootView.findViewById(R.id.chat).setEnabled(b);
        rootView.findViewById(R.id.button_send).setEnabled(b);
        rootView.findViewById(R.id.button_open_gallery).setEnabled(b);
        rootView.findViewById(R.id.button_open_camera).setEnabled(b);
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();

            String encode = encodeImage(imgDecodableString);
            sendMessageToServer(encode, "image", getCurrentTime());
        }
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK && null != data) {
            Bundle extras = data.getExtras();
            Bitmap bm = (Bitmap) extras.get("data");

            int sizeBitmap = bm.getRowBytes()*bm.getHeight();

            double ratio = sizeBitmap * (20./ 5000000.);
            if(ratio >= 1){
                bm = Bitmap.createScaledBitmap(bm, bm.getWidth()/(int)ratio, bm.getHeight()/(int)ratio, false);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encImage = Base64.encodeToString(b, Base64.DEFAULT);

            sendMessageToServer(encImage, "image", getCurrentTime());
        }
    }

    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);

        int sizeBitmap = bm.getRowBytes()*bm.getHeight();

        double ratio = sizeBitmap * (20./ 5000000.);
        if(ratio >= 1){
            bm = Bitmap.createScaledBitmap(bm, bm.getWidth()/(int)ratio, bm.getHeight()/(int)ratio, false);
        }

        System.out.println("SIZE BITMAP 1 = " + sizeBitmap);

        System.out.println("SIZE BITMAP 2 = " + bm.getRowBytes() * bm.getHeight());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;

    }

    public static Bitmap decodeImage(String data) {
        byte[] b = Base64.decode(data, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        return bmp;
    }

    private void scrollToBottom() {
        //listMessageView.smoothScrollToPosition(messAdap.getCount()-1);
        listMessageView.setSelection(messAdap.getCount() - 1);
    }

    public void setPictureProfile(Intent intent){
        String pseudo = intent.getStringExtra(SocketService.Pseudo);
        String picture_profile = intent.getStringExtra(SocketService.PictureProfile);
        Message mess = messAdap.getItemByPseudo(pseudo);
        if(mess != null){
            Bitmap bmp = decodeImage(picture_profile);
            mess.setPictureProfile(bmp);
        }
    }

    private String getCurrentTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss-SSS");
        String date = df.format(c.getTime());
        return date;
    }

    @Override

    public void onSensorChanged(SensorEvent event) {
        float x, y, z;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            float max = Math.max(Math.max(x, y), z);

            if(max > 20 && antiSpamWizzz){

                AntiSpamWizzz wait = new AntiSpamWizzz();
                wait.execute();
                String time = getCurrentTime();
                String wizzzMess = "WIZZZ :)";
                messAdap.add(new Message(wizzzMess, null, pseudo, Message.Side.RIGHT, time, null));
                scrollToBottom();
                sendMessageToServer(wizzzMess, "texte", time);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    class AntiSpamWizzz extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            synchronized (lock) {
                antiSpamWizzz = false;
                try {
                    lock.wait(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                antiSpamWizzz = true;
            }
            return null;
        }
    }
}
