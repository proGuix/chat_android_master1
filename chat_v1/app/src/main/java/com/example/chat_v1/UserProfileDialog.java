package com.example.chat_v1;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by guillaume on 21/05/16.
 */
public class UserProfileDialog extends DialogFragment {

    private User user;

    private ImageView imageView;
    private TextView nameView;
    private TextView descriptionView;

    static UserProfileDialog newInstance(User user) {
        UserProfileDialog f = new UserProfileDialog();

        Bundle args = new Bundle();
        args.putParcelable("user", user);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle(R.string.user_profile);

        View v = inflater.inflate(R.layout.layout_user_profile_list_users, container, false);
        imageView = (ImageView) v.findViewById(R.id.picture_profile);
        nameView = (TextView) v.findViewById(R.id.user);
        descriptionView = (TextView) v.findViewById(R.id.description_profile);

        Bitmap bmp = user.getPictureProfile();
        if(bmp != null){
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            double width = displaymetrics.widthPixels;
            double fixeWidth = (width * 0.3);
            int nh = (int)(bmp.getHeight() * (fixeWidth / bmp.getWidth()));
            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, (int) fixeWidth, nh, false));
        }

        String name = user.getName();
        nameView.setText(name);

        String description = user.getDescription();
        if(!description.equals("NULL")){
            descriptionView.setText(description);
        }

        return v;
    }
}
