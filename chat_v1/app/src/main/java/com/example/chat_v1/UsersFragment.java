package com.example.chat_v1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guillaume on 12/04/16.
 */
public class UsersFragment extends Fragment {

    private IMainActivity listenerActivity;
    private String pseudo;
    private View rootView;
    private UserAdapter usersAdap;
    private ListView listUserView;
    private BroadcastReceiver usersStateConnectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String usersInformations = intent.getStringExtra(SocketService.UsersStateConnection);
            usersAdap.removeAll();
            try {
                JSONArray usersInformationsJson = new JSONArray(usersInformations);
                JSONObject lineJson;
                String user;
                int connection;
                String picture_profile;
                Bitmap picture_profileBMP = null;
                String description;
                for(int i = 0; i < usersInformationsJson.length(); i++){
                    lineJson = (JSONObject) usersInformationsJson.get(i);
                    user = lineJson.get("pseudo").toString();
                    connection = (int) lineJson.get("connection");
                    picture_profile = lineJson.get("picture").toString();
                    description = lineJson.get("description").toString();
                    if(!picture_profile.equals("NULL")){
                        picture_profileBMP = ChatFragment.decodeImage(picture_profile);
                    }
                    if(!user.equals(pseudo)){
                        if(connection == 0){
                            usersAdap.add(new User(user, User.Connection.NO, picture_profileBMP, description));
                        }
                        else if(connection == 1){
                            usersAdap.add(new User(user, User.Connection.YES_BACKGROUND, picture_profileBMP, description));
                        }
                        else{
                            usersAdap.add(new User(user, User.Connection.YES_FOREGROUND, picture_profileBMP, description));
                        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.layout_list_user, container, false);

        pseudo = listenerActivity.getPseudo();

        listUserView = (ListView) rootView.findViewById(R.id.list_view_user);

        usersAdap = new UserAdapter(getContext(), R.layout.layout_item_user);

        listUserView.setAdapter(usersAdap);

        return rootView;
    }

    void getUsers(){
        Intent serviceSocket = new Intent(getActivity(), SocketService.class);
        serviceSocket.setAction(SocketService.ACTION_GET_USERS_STATE_CONNECTION);
        getActivity().startService(serviceSocket);
    }

    public void onResume() {
        super.onResume();

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(SocketService.ACTION_GET_USERS_STATE_CONNECTION);
        getActivity().registerReceiver(usersStateConnectionReceiver, filter1);

        usersAdap.removeAll();
        usersAdap.notifyDataSetChanged();
        getUsers();
    }

    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(usersStateConnectionReceiver);
    }

    public void setPictureProfile(Intent intent){
        String pseudo = intent.getStringExtra(SocketService.Pseudo);
        String picture_profile = intent.getStringExtra(SocketService.PictureProfile);
        User us = usersAdap.getItemByPseudo(pseudo);
        if(us != null){
            Bitmap bmp = ChatFragment.decodeImage(picture_profile);
            us.setPictureProfile(bmp);
        }
    }
}
