package com.example.chat_v1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by guillaume on 12/04/16.
 */

public class MainActivity extends AppCompatActivity implements IMainActivity {

    private String pseudo;
    private String mot_de_passe;

    private ProfileFragment profileFragment;
    private ChatFragment chatFragment;
    private UsersFragment usersFragment;
    private SettingsFragment settingsFragment;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private ArrayList<Fragment> listFrag;

    private BroadcastReceiver userUnsubscribeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String answer = intent.getStringExtra(SocketService.AnswerUnsubscribe);
            if(answer.equals("true")){
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.be_unsubscribe), Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.fail_unsubscribe), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private BroadcastReceiver pictureProfileReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //chatFragment.setPictureProfile(intent);
            //usersFragment.setPictureProfile(intent);
        }
    };

    private BroadcastReceiver setPseudoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String answer = intent.getStringExtra(SocketService.answerSetPseudo);
            String newPseudo = intent.getStringExtra(SocketService.NewPseudo);
            if(answer.equals("true")){
                pseudo = newPseudo;
            }
        }
    };

    private BroadcastReceiver setPasswordReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String answer = intent.getStringExtra(SocketService.answerSetPassword);
            String newPwd = intent.getStringExtra(SocketService.MotDePasse);
            if(answer.equals("true")){
                mot_de_passe = newPwd;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_main);

        Intent intentLog = getIntent();
        pseudo = intentLog.getStringExtra(SocketService.Pseudo);
        mot_de_passe = intentLog.getStringExtra(SocketService.MotDePasse);

        this.profileFragment = new ProfileFragment();
        this.chatFragment = new ChatFragment();
        this.usersFragment = new UsersFragment();
        this.settingsFragment = new SettingsFragment();

        listFrag = new ArrayList<>();
        listFrag.add(this.profileFragment);
        listFrag.add(this.chatFragment);
        listFrag.add(this.usersFragment);
        listFrag.add(this.settingsFragment);

        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), listFrag);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_perm_identity_black_36dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_chat_black_36dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_people_black_36dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_settings_black_36dp);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        viewPager.setCurrentItem(1, true);
    }

    //Seule utilisation (graphique): quand on clique sur la notification de reception d'un message
    //on montre le fragment correspondant au chat
    @Override
    public void onNewIntent(Intent intent){
        viewPager.setCurrentItem(1, true);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SocketService.ACTION_UNSUBSCRIBE);
        registerReceiver(userUnsubscribeReceiver, filter);

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(SocketService.ACTION_SET_PICTURE_PROFILE);
        registerReceiver(pictureProfileReceiver, filter2);

        IntentFilter filter3 = new IntentFilter();
        filter3.addAction(SocketService.ACTION_SET_PSEUDO);
        registerReceiver(setPseudoReceiver, filter3);

        IntentFilter filter4 = new IntentFilter();
        filter4.addAction(SocketService.ACTION_SET_PASSWORD);
        registerReceiver(setPasswordReceiver, filter4);

        Intent serviceSocket = new Intent(MainActivity.this, SocketService.class);
        serviceSocket.setAction(SocketService.ACTION_SET_STATE_CONNECTION);
        serviceSocket.putExtra(SocketService.Pseudo, pseudo);
        serviceSocket.putExtra(SocketService.StateConnection, 2);
        startService(serviceSocket);
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(userUnsubscribeReceiver);
        unregisterReceiver(pictureProfileReceiver);
        unregisterReceiver(setPseudoReceiver);
        unregisterReceiver(setPasswordReceiver);

        Intent serviceSocket = new Intent(MainActivity.this, SocketService.class);
        serviceSocket.setAction(SocketService.ACTION_SET_STATE_CONNECTION);
        serviceSocket.putExtra(SocketService.Pseudo, pseudo);
        serviceSocket.putExtra(SocketService.StateConnection, 1);
        startService(serviceSocket);
    }

    @Override
    public void onStop() {
        super.onStop();

        Intent serviceSocket = new Intent(MainActivity.this, SocketService.class);
        serviceSocket.setAction(SocketService.ACTION_SET_STATE_CONNECTION);
        serviceSocket.putExtra(SocketService.Pseudo, pseudo);
        serviceSocket.putExtra(SocketService.StateConnection, 0);
        startService(serviceSocket);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent serviceSocket = new Intent(MainActivity.this, SocketService.class);
        serviceSocket.setAction(SocketService.ACTION_SET_STATE_CONNECTION);
        serviceSocket.putExtra(SocketService.Pseudo, pseudo);
        serviceSocket.putExtra(SocketService.StateConnection, 0);
        startService(serviceSocket);

        stopService(new Intent(MainActivity.this, SocketService.class));
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingsFragment.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK && null != data) {
            Bundle extras = data.getExtras();
            Bitmap bm = (Bitmap) extras.get("data");

            int sizeBitmap = bm.getRowBytes() * bm.getHeight();

            double ratio = sizeBitmap * (20. / 5000000.);
            if (ratio >= 1) {
                bm = Bitmap.createScaledBitmap(bm, bm.getWidth() / (int) ratio, bm.getHeight() / (int) ratio, false);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encImage = Base64.encodeToString(b, Base64.DEFAULT);

            sendPictureProfileToServer(encImage);
        }
    }

    public void sendPictureProfileToServer(String encImage){
        Intent serviceSocket = new Intent(this, SocketService.class);
        serviceSocket.setAction(SocketService.ACTION_SET_PICTURE_PROFILE);
        serviceSocket.putExtra(SocketService.Pseudo, pseudo);
        serviceSocket.putExtra(SocketService.PictureProfile, encImage);
        startService(serviceSocket);
    }

    @Override
    public String getMotDePasse() {
        return mot_de_passe;
    }

    @Override
    public String getPseudo() {
        return pseudo;
    }
}