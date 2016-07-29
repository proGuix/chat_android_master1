package com.example.chat_v1;

/**
 * Created by guillaume on 21/05/16.
 */

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
 * Created by guillaume on 18/04/16.
 */
public class SetPseudoDialog extends DialogFragment {

    private String mot_de_passe;

    private TextView mdpView;
    private TextView pseudoView;
    private EditText mdpText;
    private EditText pseudoText;
    private Button button_ok;
    private Button button_cancel;

    static SetPseudoDialog newInstance(String mot_de_passe) {
        SetPseudoDialog f = new SetPseudoDialog();

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

        getDialog().setTitle(R.string.layout_setting_setPseudo);

        View v = inflater.inflate(R.layout.layout_set_pseudo, container, false);
        mdpView = (TextView) v.findViewById(R.id.mdp_view);
        pseudoView = (TextView) v.findViewById(R.id.new_pseudo_view);
        mdpText = (EditText) v.findViewById(R.id.mdp_text);
        pseudoText = (EditText) v.findViewById(R.id.pseudo_text);
        button_ok = (Button) v.findViewById(R.id.ok);
        button_cancel = (Button) v.findViewById(R.id.cancel);

        mdpView.setText(R.string.enter_your_password);
        pseudoView.setText(R.string.new_pseudo);

        button_ok.setText(R.string.button_ok);
        button_cancel.setText(R.string.button_cancel);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pseudoText.getText().toString().equals("") && mdpText.getText().toString().equals(mot_de_passe)) {
                    Intent serviceSocket = new Intent(getActivity(), SocketService.class);
                    serviceSocket.setAction(SocketService.ACTION_SET_PSEUDO);
                    serviceSocket.putExtra(SocketService.Pseudo, ((IMainActivity) getActivity()).getPseudo());
                    serviceSocket.putExtra(SocketService.NewPseudo, pseudoText.getText().toString());
                    getActivity().startService(serviceSocket);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.inc_pwd_or_pseudo), Toast.LENGTH_SHORT).show();
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
