package com.vssb.mitattendance;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {
    public  static int i =0;
  // public static TextView responseView,progressView;
  //  public static Button refreshButton;
    public String result;
    public  static String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences loginInfoPreferences = this.getSharedPreferences("loginInfoPreferences",MODE_PRIVATE);

        username = loginInfoPreferences.getString("username", "");
        password = loginInfoPreferences.getString("password","");
        if(username.matches("") || password.matches("")) {
            Intent loginInputActivityIntent = new Intent(MainActivity.this,LoginInputActivity.class);
            startActivity(loginInputActivityIntent);
            MainActivity.this.finish();
        }
        else {
            //refreshButton = (Button) findViewById(R.id.refresh_attendance_button);
        /*    refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressView.setText("Refreshing...");
                    new RequestTask(MainActivity.this,loginInfoPreferences.edit()).execute("https://www.tcsion.com/iONBizServices/Authenticate?usrloginid=" + username + "&usrpassword=" + password);
                }
            });*/

         //   responseView = (TextView) findViewById(R.id.response_display_textview);
         //   responseView.setMovementMethod(new ScrollingMovementMethod());
         //   progressView = (TextView) findViewById(R.id.progress_display_text_view);


          //  responseView.setText(loginInfoPreferences.getString("totalPercentage", ""));
         //   progressView.setText("Refreshing...");
            result = null;

           // if(i == 0)
            AsyncResponse asyncResponse = new AsyncResponse() {
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
                        ArrayList<String> subjectWiseAttendance = new ArrayList<String>();
                        for(int j=1; j<rows.length() ;j++) {
                            row = rows.getJSONObject(j).getJSONObject("columns").getJSONArray("col");
                            String subjectName = row.getJSONObject(1).getString("content");
                            subjectName = subjectName.substring(subjectName.indexOf("TA") + 3, subjectName.indexOf("]]"));
                            String totalLec = row.getJSONObject(3).getString("content");
                            totalLec = totalLec.substring(totalLec.indexOf("TA") + 3, totalLec.indexOf("]]"));
                            String lecAttended = row.getJSONObject(4).getString("content");
                            lecAttended = lecAttended.substring(lecAttended.indexOf("TA") + 3, lecAttended.indexOf("]]"));
                            String percentageAttendance = row.getJSONObject(5).getString("content");
                            percentageAttendance = percentageAttendance.substring(percentageAttendance.indexOf("TA") + 3, percentageAttendance.indexOf("%") + 1);
                            subjectWiseAttendance.add(subjectName+"   "+lecAttended+"/"+totalLec+"   "+percentageAttendance);
                        }
                        ArrayAdapter<String> attendAdapter = new ArrayAdapter<String>(
                                MainActivity.this,
                                R.layout.attendance_list,
                                R.id.attendance_text_view,
                                subjectWiseAttendance
                        );
                        ListView listView = (ListView) findViewById(R.id.attendance_list_View);
                        listView.setAdapter(attendAdapter);
                    } catch (JSONException e) {
                        Log.e("JSON exception", e.getMessage());
                        e.printStackTrace();
                    }
                    //    MainActivity.progressView.setText("");

                    //  MainActivity.responseView.setText(attendance);
//                int maxLogSize = 1000;
//                String veryLongString = jsonObj.toString();
//                for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
//                    int start = i * maxLogSize;
//                    int end = (i+1) * maxLogSize;
//                    end = end > veryLongString.length() ? veryLongString.length() : end;
//                    Log.v("vssbjson", veryLongString.substring(start, end));
//                }
                    //new RequestTask().execute(result.substring(result.indexOf("https://"),result.indexOf("/>")-1).replace("&amp;","&").replace(" ",""));

                    //Do anything with response.
                    //
                    //
                    // }
                }
            };
            RequestTask asyncTask =new RequestTask (MainActivity.this,loginInfoPreferences.edit(),asyncResponse);
            asyncTask.execute("https://www.tcsion.com/iONBizServices/Authenticate?usrloginid=" + username + "&usrpassword=" + password);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

