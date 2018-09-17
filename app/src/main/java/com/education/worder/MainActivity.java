package com.education.worder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.education.worder.data.DBWords;
import com.education.worder.data.Word;
import com.education.worder.fragments.AddWordFragment;
import com.education.worder.fragments.MainFragment;
import com.education.worder.fragments.SettingsFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainFragment.OnMainFragmentListener,
        AddWordFragment.OnAddFragmentListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private MainFragment mainFragment = MainFragment.newInstance();
    private AddWordFragment addFragment = AddWordFragment.newInstance();
    private SettingsFragment settingFragment = SettingsFragment.newInstance();
    private AudioService.LocalBinder binder;
    private boolean isAutoTranslate = false;
    private boolean isMultipleInput = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportFragmentManager().beginTransaction().add(R.id.clMain, mainFragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        for(String sPermission : ((App)getApplication()).getPermission()) {
            ContextCompat.checkSelfPermission(this, sPermission);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if(Settings.load().isFirstStart()){
            DBWords helper = new DBWords(this);
            helper.insert("default", Locale.US,Locale.getDefault());
            Settings.load().setFirstStart(false);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        bind();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy(){
        try {
            unbindService(connection);
        } catch (IllegalArgumentException ex) {
            binder.getService().startSpeak();
            ex.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(addFragment.isAdded()){
            getSupportFragmentManager().beginTransaction().replace(R.id.clMain, mainFragment).commit();
            binder.getService().loadData(Settings.load());
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.slider_add_word:{
                onAddWord();
                break;
            }
            case R.id.slider_delete_selected:{
                mainFragment.deleteSelected();
                break;
            }
            case R.id.slider_select_all:{
                mainFragment.selectAll();
                break;
            }
            case R.id.slider_deselect_all:{
                mainFragment.unselectAll();
                break;
            }
            case R.id.slider_setting:{
                viewSettings();
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void bind(){
        if(binder == null) {
            Intent intent = new Intent(this, AudioService.class);
            bindService(intent, connection, BIND_AUTO_CREATE);
        }
    }

    private void viewSettings(){
        getSupportFragmentManager().beginTransaction().replace(R.id.clMain, settingFragment).commit();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (AudioService.LocalBinder) service;
            binder.getService().setOnAudioServiceListener(mainFragment);
            binder.getService().loadData(Settings.load());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onAddWord() {
        getSupportFragmentManager().beginTransaction().replace(R.id.clMain, addFragment).commit();
    }

    @Override
    public void onDeleteWord(Word word) {
        binder.getService().deleteWord(word);
    }

    @Override
    public void onUpdateWord(Word word) {
        binder.getService().updateWord(word);
    }

    @Override
    public void onStartSelected() {
        if(binder != null) {
            binder.getService().startSpeak();
        }
    }


    @Override
    public void onAdded(Word word) {
        if(!isMultipleInput) {
            getSupportFragmentManager().beginTransaction().replace(R.id.clMain, mainFragment).commit();
        }
        binder.getService().addWord(word);
            addFragment.clearInput();
    }

    @Override
    public void onAutoTranslate(boolean is) {
        isAutoTranslate = is;
    }

    @Override
    public void onMultypleInput(boolean is) {
        isMultipleInput = is;
    }
}
