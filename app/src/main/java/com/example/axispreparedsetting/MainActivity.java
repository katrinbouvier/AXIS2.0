package com.example.axispreparedsetting;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText mCameraIP;
    EditText mLogin;
    EditText mPassword;
    VideoView mVideoView;
    Button mShowDialogBtn;
    View mView;
    String userLogin;
    String userPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mVideoView = findViewById(R.id.videoView);
        mShowDialogBtn = findViewById(R.id.showDialogBtn);
        mCameraIP = findViewById(R.id.cameraIP);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        mShowDialogBtn.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
              AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
              mView = MainActivity.this.getLayoutInflater().inflate(R.layout.dialog_login, null);
              builder.setTitle("Авторизация")
                      .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              mLogin = mView.findViewById(R.id.login);
                              mPassword = mView.findViewById(R.id.password);

                              userLogin = mLogin.getText().toString();
                              userPassword = mPassword.getText().toString();

                              dialog.cancel();
                              startStream();
                          }
                      });

              builder.setView(mView);
              AlertDialog dialog = builder.create();
              dialog.show();
          }
      }
    );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.nav_prep1) {

            preparedAction("http://192.168.0.100/axis-cgi/mjpg/video.cgi?rotation=90");

        } else if (id == R.id.nav_prep2) {

        } else if (id == R.id.nav_prep3) {

        } else if (id == R.id.nav_prep4) {

        } else if (id == R.id.nav_prep5) {

        } else if (id == R.id.nav_prep6) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void preparedAction(String URL) {
        OkHttpClient client = new OkHttpClient();
        String username = "root";
        String password ="root";
        String credentials = username+":"+password;

        final String basic = "Basic "+ Base64.encodeToString(credentials.getBytes(),
                Base64.NO_WRAP);

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Authorization", basic)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) {
                System.out.println("Response!");
                System.out.println(response);
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            public void onFailure(Call call, IOException e) {
                System.out.println("Failure...");
                System.out.println(e);
            }
        });
    }

    private static OkHttpClient createAuthenticatedClient(final String username,
                                                          final String password) {
        OkHttpClient httpClient=new OkHttpClient.Builder().authenticator(
                new Authenticator() {
                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        String credential = Credentials.basic(username, password);
                        return response.request().newBuilder().header("Authorization", credential).build();

                    }
                }).build();
        return httpClient;
    }

    private static Response doRequest(OkHttpClient client, String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful()) {
            throw new IOException("Unexpected code "+response);
        }
        System.out.println(response.body().string());
        return response;
    }

    public static Response fetch(String url, String username, String password) throws IOException {
        OkHttpClient client = createAuthenticatedClient(username, password);
        return doRequest(client, url);
    }


    public void startStream() {
        String IP = mCameraIP.getText().toString();
        // если камера имеет логин/пароль
        // конструируем адрес видеопотока
        if ((!userLogin.equals("")) && (!userPassword.equals(""))) {
            userLogin += ":";
            userPassword += "@";
        }

        String videoSource = "rtsp://" + userLogin + userPassword + IP + "/axis-media/media.amp";
        mVideoView.setVideoURI(Uri.parse(videoSource));
        mVideoView.setMediaController(new MediaController(getApplicationContext()));
        mVideoView.requestFocus(0);
        mVideoView.start();
    }
}
