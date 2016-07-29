package com.example.chat_v1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * Created by guillaume on 15/04/16.
 */
public class SettingsFragment extends Fragment{

    public static final int REQUEST_CODE_CAMERA = 3;

    private View rootView;
    private SettingsAdapter settingAdap;
    private ListView listSettingView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.layout_settings, container, false);

        listSettingView = (ListView) rootView.findViewById(R.id.list_view_setting);

        settingAdap = new SettingsAdapter(getContext(), R.layout.layout_item_setting);

        listSettingView.setAdapter(settingAdap);

        return rootView;
    }
}
