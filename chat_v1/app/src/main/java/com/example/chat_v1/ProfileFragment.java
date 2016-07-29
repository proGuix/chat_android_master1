package com.example.chat_v1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ServerSocket;

/**
 * Created by guillaume on 05/05/16.
 */
public class ProfileFragment extends Fragment {

    private ImageView pictureView;
    private TextView pseudoView;
    private TextView descriptionView;
    private IMainActivity listenerActivity;

    private BroadcastReceiver profileReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String profileStr = intent.getStringExtra(SocketService.Profile);

            try {
                JSONArray profileArray = new JSONArray(profileStr);
                if(profileArray.length() == 1){
                    JSONObject profile = (JSONObject) profileArray.get(0);
                    String pseudo = profile.get("pseudo").toString();
                    pseudoView.setText(pseudo);
                    String image = profile.get("picture").toString();
                    String description = profile.get("description").toString();
                    Bitmap bmp = null;
                    if(!image.equals("")){
                        bmp = ChatFragment.decodeImage(image);
                        DisplayMetrics displaymetrics = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                        double width = displaymetrics.widthPixels;
                        double fixeWidth = (width * 0.3);
                        int nh = (int)(bmp.getHeight() * (fixeWidth / bmp.getWidth()));
                        pictureView.setImageBitmap(Bitmap.createScaledBitmap(bmp, (int) fixeWidth, nh, false));
                    }
                    if(!description.equals("NULL")){
                        descriptionView.setText(description);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
    public void onResume(){
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SocketService.ACTION_GET_PROFILE);
        getActivity().registerReceiver(profileReceiver, filter);

        getProfile();
    }

    @Override
    public void onPause(){
        super.onPause();

        getActivity().unregisterReceiver(profileReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView;
        rootView = inflater.inflate(R.layout.layout_user_profile, container, false);

        pictureView = (ImageView) rootView.findViewById(R.id.picture_profile);
        pseudoView = (TextView) rootView.findViewById(R.id.user);
        descriptionView = (TextView) rootView.findViewById(R.id.description_profile);

        pseudoView.setText(listenerActivity.getPseudo());

        getProfile();

        return rootView;
    }

    public void getProfile(){
        Intent serviceSocket = new Intent(getActivity(), SocketService.class);
        serviceSocket.setAction(SocketService.ACTION_GET_PROFILE);
        serviceSocket.putExtra(SocketService.Pseudo, listenerActivity.getPseudo());
        getActivity().startService(serviceSocket);
    }
}
