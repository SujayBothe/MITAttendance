package com.vssb.mitattendance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;

public class DetailedAttendanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_attendance);
        JSONObject jsonObj = null;
        JSONArray rows = null;
        JSONArray row = null;
        ArrayList<String> subjectWiseAttendance = new ArrayList<String>();
        try {
            jsonObj = new JSONObject(this.getSharedPreferences("loginInfoPreferences",MODE_PRIVATE).getString("fullAttendance",""));

            rows = jsonObj.getJSONObject("widget").getJSONObject("sections").getJSONObject("section").getJSONObject("rows").getJSONArray("row");


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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> attendAdapter = new ArrayAdapter<String>(
                DetailedAttendanceActivity.this,
                R.layout.attendance_list,
                R.id.attendance_text_view,
                subjectWiseAttendance
        );
        ListView listView = (ListView) findViewById(R.id.attendance_list_View);
        listView.setAdapter(attendAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_attendance, menu);
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
