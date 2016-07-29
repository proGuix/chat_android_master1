package com.example.chat_v1;

import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by guillaume on 18/04/16.
 */
public class AProposDialog extends DialogFragment {

    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setTitle(R.string.layout_setting_about);

        View v = inflater.inflate(R.layout.layout_a_propos_dialog, container, false);
        textView = (TextView) v.findViewById(R.id.text_a_propos);
        textView.setText(R.string.about_authors);

        return v;
    }
}
