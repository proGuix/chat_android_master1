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
public class SetPasswordDialog extends DialogFragment {

    private String mot_de_passe;

    private TextView mdpOldView;
    private TextView mdpNewView;
    private EditText mdpOldText;
    private EditText mdpNewText;
    private Button button_ok;
    private Button button_cancel;

    static SetPasswordDialog newInstance(String mot_de_passe) {
        SetPasswordDialog f = new SetPasswordDialog();

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

        getDialog().setTitle(R.string.layout_setting_setPassword);

        View v = inflater.inflate(R.layout.layout_set_password, container, false);
        mdpOldView = (TextView) v.findViewById(R.id.old_password_view);
        mdpNewView = (TextView) v.findViewById(R.id.new_password_view);
        mdpOldText = (EditText) v.findViewById(R.id.old_password_text);
        mdpNewText = (EditText) v.findViewById(R.id.new_password_text);
        button_ok = (Button) v.findViewById(R.id.ok);
        button_cancel = (Button) v.findViewById(R.id.cancel);

        mdpOldView.setText(R.string.old_password);
        mdpNewView.setText(R.string.new_password);

        button_ok.setText(R.string.button_ok);
        button_cancel.setText(R.string.button_cancel);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdpOldText.getText().toString().equals(mot_de_passe) && !mdpNewText.getText().toString().equals("")) {
                    Intent serviceSocket = new Intent(getActivity(), SocketService.class);
                    serviceSocket.setAction(SocketService.ACTION_SET_PASSWORD);
                    serviceSocket.putExtra(SocketService.Pseudo, ((IMainActivity) getActivity()).getPseudo());
                    serviceSocket.putExtra(SocketService.MotDePasse, mdpNewText.getText().toString());
                    getActivity().startService(serviceSocket);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.incor_old_pwd_or_new_pwd), Toast.LENGTH_SHORT).show();
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