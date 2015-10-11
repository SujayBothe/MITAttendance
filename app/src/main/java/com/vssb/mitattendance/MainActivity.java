package com.vssb.mitattendance;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class MainActivity extends AppCompatActivity {
    public  static int i =0;
    public static TextView responseView;
    public static Button detailedAttendanceButton;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public String result;
    public  static String username,password;
    public static AsyncResponse asyncResponse;
    android.support.v7.app.ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getSupportActionBar();

        final SharedPreferences loginInfoPreferences = this.getSharedPreferences("loginInfoPreferences",MODE_PRIVATE);

        username = loginInfoPreferences.getString("username", "");
        password = loginInfoPreferences.getString("password","");
        if(username.matches("") || password.matches("")) {
            Intent loginInputActivityIntent = new Intent(MainActivity.this,LoginInputActivity.class);
            startActivity(loginInputActivityIntent);
            MainActivity.this.finish();
        }
        else {

            result = null;

           // if(i == 0)
            asyncResponse = new AsyncResponse() {
                @Override
                public void processFinish(Object output) {

                    result = (String) output;
                    SharedPreferences.Editor loginInfoPreferencesEditor = loginInfoPreferences.edit() ;
                    Log.i("vssb", result);
                    JSONObject jsonObj = null;
                    JSONArray rows = null;
                    JSONArray row = null;
                    JSONArray lastRowCols = null;
                    String attendance = null;
                    try {
                        jsonObj = XML.toJSONObject(result);
                        rows = jsonObj.getJSONObject("widget").getJSONObject("sections").getJSONObject("section").getJSONObject("rows").getJSONArray("row");
                        lastRowCols = rows.getJSONObject(rows.length() - 1).getJSONObject("columns").getJSONArray("col");
                        attendance = lastRowCols.getJSONObject(lastRowCols.length() - 1).getString("content");
                        attendance = attendance.substring(attendance.indexOf("TA") + 3, attendance.indexOf("%") + 1);

                        Log.i("vssb attendance is",  attendance);
                        loginInfoPreferencesEditor.putString("totalPercentage", attendance);
                        loginInfoPreferencesEditor.putString("fullAttendance", jsonObj.toString());
                        loginInfoPreferencesEditor.commit();

                    } catch (JSONException e) {
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                    /*
                    if(Float.parseFloat(attendance.substring(0,2))<75.0){
                        RelativeLayout rl = (RelativeLayout)findViewById(R.id.main_custom_relative_layout);
                        rl.setBackgroundColor(Color.RED);
                    }*/
                   // MainActivity.progressView.setText("");
                    Card card = new Card(getBaseContext(),R.layout.main_custom_layout);
                    card.setTitle("\n\t\t\t\t\t\t\t\t"+attendance);
                    CardHeader header = new CardHeader(getBaseContext());
                    header.setTitle("\t\t\t\t\t\t\t\tTotal Attendance");
                    card.addCardHeader(header);
                    CardViewNative cardView = (CardViewNative) MainActivity.this.findViewById(R.id.carddemo);
                    cardView.setCard(card);
                   // MainActivity.responseView.setText(attendance);
                    if(mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        actionBar.show();
                    }
                }
            };
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                }
            });
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);

                }
            });
            RequestTask asyncTask =new RequestTask (MainActivity.this,loginInfoPreferences.edit(),asyncResponse);
            asyncTask.execute("https://www.tcsion.com/iONBizServices/Authenticate?usrloginid=" + username + "&usrpassword=" + password);

            detailedAttendanceButton = (Button) findViewById(R.id.detailed_attendance_button);
            detailedAttendanceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailedAttendanceIntent = new Intent(MainActivity.this,DetailedAttendanceActivity.class);
                    startActivity(detailedAttendanceIntent);
                }
            });

           // responseView = (TextView) findViewById(R.id.response_display_textview);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final SharedPreferences loginInfoPreferences = this.getSharedPreferences("loginInfoPreferences", MODE_PRIVATE);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    actionBar.hide();
                }
            });
            RequestTask asyncTask =new RequestTask (MainActivity.this,loginInfoPreferences.edit(),asyncResponse);
            asyncTask.execute("https://www.tcsion.com/iONBizServices/Authenticate?usrloginid=" + username + "&usrpassword=" + password);
            return true;
        }
        else if(id == R.id.action_log_out) {
            SharedPreferences.Editor loginInfoPreferencesEditor = loginInfoPreferences.edit();
            loginInfoPreferencesEditor.putString("username", "");
            loginInfoPreferencesEditor.putString("password", "");
            loginInfoPreferencesEditor.commit();
            Intent mainActivityRefreshIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(mainActivityRefreshIntent);
            MainActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

