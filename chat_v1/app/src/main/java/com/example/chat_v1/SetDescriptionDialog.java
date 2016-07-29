package com.example.chat_v1;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by guillaume on 21/05/16.
 */
public class SetDescriptionDialog extends DialogFragment {
    private String mot_de_passe;

    private TextView mdpView;
    private TextView descriptionView;
    private EditText mdpText;
    private EditText descriptionText;
    private Button button_ok;
    private Button button_cancel;

    static SetDescriptionDialog newInstance(String mot_de_passe) {
        SetDescriptionDialog f = new SetDescriptionDialog();

        Bundle args = new Bundle();
        args.putString("mdp", mot_de_passe);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mot_de_passe = getArguments().getString("mdp");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle(R.string.layout_setting_setDescription);

        View v = inflater.inflate(R.layout.layout_set_description, container, false);
        mdpView = (TextView) v.findViewById(R.id.mdp_view);
        descriptionView = (TextView) v.findViewById(R.id.new_description_view);
        mdpText = (EditText) v.findViewById(R.id.mdp_text);
        descriptionText = (EditText) v.findViewById(R.id.description_text);
        button_ok = (Button) v.findViewById(R.id.ok);
        button_cancel = (Button) v.findViewById(R.id.cancel);

        mdpView.setText(R.string.enter_your_password);
        descriptionView.setText(R.string.new_description);

        button_ok.setText(R.string.button_ok);
        button_cancel.setText(R.string.button_cancel);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdpText.getText().toString().equals(mot_de_passe)) {
                    Intent serviceSocket = new Intent(getActivity(), SocketService.class);
                    serviceSocket.setAction(SocketService.ACTION_SET_DESCRIPTION);
                    serviceSocket.putExtra(SocketService.Pseudo, ((IMainActivity) getActivity()).getPseudo());
                    serviceSocket.putExtra(SocketService.Description, descriptionText.getText().toString());
                    getActivity().startService(serviceSocket);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show();
                }
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return v;
    }
}
