package com.example.chat_v1;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume on 15/04/16.
 */

public class SettingsAdapter extends ArrayAdapter<String> {
    private TextView settingView;
    private List<String> listSetting = new ArrayList<String>();
    private Context context;

    public SettingsAdapter(Context applicationContext, int layoutItemSetting) {
        // TODO Auto-generated constructor stub
        super(applicationContext, layoutItemSetting);
        this.context = applicationContext;

        Resources res = applicationContext.getResources();

        this.listSetting.add(res.getString(R.string.layout_setting_setPictureProfile));
        this.listSetting.add(res.getString(R.string.layout_setting_setPseudo));
        this.listSetting.add(res.getString(R.string.layout_setting_setPassword));
        this.listSetting.add(res.getString(R.string.layout_setting_setDescription));
        this.listSetting.add(res.getString(R.string.layout_setting_unsubscribe));
        this.listSetting.add(res.getString(R.string.layout_setting_about));
    }

    public void add(String setting) {
        // TODO Auto-generated method stub
        listSetting.add(setting);
        super.add(setting);
    }

    public int getCount(){
        return this.listSetting.size();
    }

    public String getItem(int index){
        return this.listSetting.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String setting = getItem(position);
        v = inflater.inflate(R.layout.layout_item_setting, parent, false);
        settingView = (TextView) v.findViewById(R.id.item_setting);
        settingView.setText(setting.toString());

        if(setting.equals(this.listSetting.get(0))){
            settingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int numCameras = Camera.getNumberOfCameras();
                    if (numCameras > 0) {
                        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        ((Activity)context).startActivityForResult(cameraintent, SettingsFragment.REQUEST_CODE_CAMERA);
                    }
                }
            });
        }

        if(setting.equals(this.listSetting.get(1))){
            settingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = SetPseudoDialog.newInstance(((IMainActivity) getContext()).getMotDePasse());
                    newFragment.show(((Activity) context).getFragmentManager(), "set_pseudo_dialog");
                }
            });
        }

        if(setting.equals(this.listSetting.get(2))){
            settingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = SetPasswordDialog.newInstance(((IMainActivity) getContext()).getMotDePasse());
                    newFragment.show(((Activity) context).getFragmentManager(), "set_password_dialog");
                }
            });
        }

        if(setting.equals(this.listSetting.get(3))){
            settingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = SetDescriptionDialog.newInstance(((IMainActivity) getContext()).getMotDePasse());
                    newFragment.show(((Activity) context).getFragmentManager(), "set_description_dialog");
                }
            });
        }

        if(setting.equals(this.listSetting.get(4))){
            settingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = UnsubscribeDialog.newInstance(((IMainActivity)getContext()).getMotDePasse());
                    newFragment.show(((Activity) context).getFragmentManager(), "unsubscribe_dialog");
                }
            });
        }

        if(setting.equals(this.listSetting.get(5))){
            settingView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = new AProposDialog();
                    newFragment.show(((Activity) context).getFragmentManager(), "a_propos_dialog");
                }
            });
        }

        return v;
    }
}
