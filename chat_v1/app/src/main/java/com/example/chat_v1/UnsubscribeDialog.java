package com.example.chat_v1;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
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
public class UnsubscribeDialog extends DialogFragment {

    private String mot_de_passe;

    private TextView textView;
    private EditText mdpText;
    private Button button_ok;
    private Button button_cancel;

    static UnsubscribeDialog newInstance(String mot_de_passe) {
        UnsubscribeDialog f = new UnsubscribeDialog();

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

        getDialog().setTitle(R.string.layout_setting_unsubscribe);

        View v = inflater.inflate(R.layout.layout_unsubscribe, container, false);
        textView = (TextView) v.findViewById(R.id.unsubscribe_text);
        mdpText = (EditText) v.findViewById(R.id.unsubscribe_mdp);
        button_ok = (Button) v.findViewById(R.id.unsubscribe_ok);
        button_cancel = (Button) v.findViewById(R.id.unsubscribe_cancel);

        textView.setText(R.string.unsubscribe_text);

        button_ok.setText(R.string.button_ok);
        button_cancel.setText(R.string.button_cancel);

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdpText.getText().toString().equals(mot_de_passe)) {
                    Intent serviceSocket = new Intent(getActivity(), SocketService.class);
                    serviceSocket.setAction(SocketService.ACTION_UNSUBSCRIBE);
                    serviceSocket.putExtra(SocketService.Pseudo, ((IMainActivity) getActivity()).getPseudo());
                    getActivity().startService(serviceSocket);
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
