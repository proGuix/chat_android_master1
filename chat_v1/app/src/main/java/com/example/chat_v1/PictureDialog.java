package com.example.chat_v1;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by guillaume on 30/04/16.
 */
public class PictureDialog extends DialogFragment {

    private Bitmap bmp;
    private String temps;
    private Button button_save_picture;

    public static PictureDialog newInstance(Bitmap bmp, String temps) {
        PictureDialog f = new PictureDialog();

        Bundle args = new Bundle();
        args.putParcelable("image", bmp);
        args.putString("temps", temps);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        bmp = args.getParcelable("image");
        temps = args.getString("temps");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View v = inflater.inflate(R.layout.layout_picture_dialog, container, false);

        button_save_picture = (Button) v.findViewById(R.id.button_save_picture);
        button_save_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    String storage = Environment.getExternalStorageDirectory().toString();
                    FileOutputStream out = null;
                    File directory = new File(storage, "DCIM/EasyChat");
                    if(!directory.exists()){
                        directory.mkdirs();
                    }
                    try{
                        File file = new File(directory, temps + ".jpg");
                        if(!file.exists()){
                            file.createNewFile();
                        }
                        out = new FileOutputStream(file);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    } catch(Exception e){
                        e.printStackTrace();
                    } finally{
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
                dismiss();
            }
        });

        return v;
    }
}
