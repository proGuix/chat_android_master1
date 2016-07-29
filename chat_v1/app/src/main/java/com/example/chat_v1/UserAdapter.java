package com.example.chat_v1;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume on 12/04/16.
 */
public class UserAdapter extends ArrayAdapter<User> {
    private ImageView picture_profileView;
    private TextView userView;
    private TextView connectView;
    private List<User> listUser = new ArrayList<User>();
    private Context context;

    public UserAdapter(Context applicationContext, int layoutUser) {
        super(applicationContext, layoutUser);
        this.context = applicationContext;
        // TODO Auto-generated constructor stub
    }

    public void add(User user) {
        // TODO Auto-generated method stub
        listUser.add(user);
        super.add(user);
    }

    public boolean exist(String pseudo){
        for(User us : listUser){
            if(us.getName().equals(pseudo)){
                return true;
            }
        }
        return false;
    }

    public void removeAll(){
        int size = listUser.size();
        for(int i = 0; i < size; i++){
            listUser.remove(0);
        }
    }

    public int getCount(){
        return this.listUser.size();
    }

    public User getItem(int index){
        return this.listUser.get(index);
    }

    public User getItemByPseudo(String pseudo){
        int size = getCount();
        for(int i = 0; i < size; i++){
            User us = getItem(i);
            if(us.getName().equals(pseudo)){
                return us;
            }
        }
        return null;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final User user = getItem(position);
        v = inflater.inflate(R.layout.layout_item_user, parent, false);

        picture_profileView = (ImageView) v.findViewById(R.id.picture_profile);
        Bitmap picture_profileBMP = user.getPictureProfile();
        if(picture_profileBMP != null){
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            double width = displaymetrics.widthPixels;
            double fixeWidth = (width * 0.1);
            int nh = (int)(picture_profileBMP.getHeight() * (fixeWidth / picture_profileBMP.getWidth()));
            picture_profileView.setImageBitmap(Bitmap.createScaledBitmap(picture_profileBMP, (int) fixeWidth, nh, false));
        }
        else{
            int id = context.getResources().getIdentifier("ic_face_black_36dp", "drawable", context.getPackageName());
            picture_profileView.setImageResource(id);
        }

        userView = (TextView) v.findViewById(R.id.user);
        userView.setText(user.getName());

        userView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = UserProfileDialog.newInstance(user);
                newFragment.show(((Activity) context).getFragmentManager(), "user_profile_dialog");
            }
        });

        connectView = (TextView) v.findViewById(R.id.red_green);
        if (user.getConnection() == User.Connection.NO){
            connectView.setBackgroundResource(R.drawable.red_circle);
        }
        else if(user.getConnection() == User.Connection.YES_BACKGROUND){
            connectView.setBackgroundResource(R.drawable.yellow_circle);
        }
        else{
            connectView.setBackgroundResource(R.drawable.green_circle);
        }

        return v;
    }
}
