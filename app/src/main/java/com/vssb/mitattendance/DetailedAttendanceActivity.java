package com.vssb.mitattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class DetailedAttendanceActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_attendance);
        JSONObject jsonObj = null;
        JSONArray rows = null;
        JSONArray row = null;
        ArrayList<String> subjectWiseAttendance = new ArrayList<String>();
        ArrayList<Card> cards = new ArrayList<Card>();
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
                subjectWiseAttendance.add(subjectName + "   " + lecAttended + "/" + totalLec + "   " + percentageAttendance);
                Card card = new Card(this,R.layout.custom_layout);
                CardHeader header = new CardHeader(this);
               // CustomHeaderInnerCard header = new CustomHeaderInnerCard(this,subjectName);
                //header1.t1.setText(subjectName);
                header.setTitle(subjectName);
                card.setTitle("\n\tTotal Lectures:" + totalLec + "\n\tAttended:" + lecAttended + "\n\tPercentage:" + percentageAttendance);
                card.addCardHeader(header);
                cards.add(card);
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        /*
        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i = 0; i<subjectWiseAttendance.size(); i++) {
            // Create a Card
            Card card = new Card(this);
            // Create a CardHeader
            CardHeader header = new CardHeader(this);
            // Add Header to card
            header.setTitle(subjectWiseAttendance.get(i));
            card.setTitle(subjectWiseAttendance.get(i));
            card.addCardHeader(header);


            cards.add(card);
        }
        */

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(this, cards);
        CardListView listView = (CardListView) this.findViewById(R.id.myList);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }
        /*
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        mAdapter = new MyAdapter(subjectWiseAttendance);
        recList.setAdapter(mAdapter);*/
        /*
        ArrayAdapter<String> attendAdapter = new ArrayAdapter<String>(
                DetailedAttendanceActivity.this,
                R.layout.attendance_list,
                R.id.attendance_text_view,
                subjectWiseAttendance
        );
        ListView listView = (ListView) findViewById(R.id.attendance_list_View);
        listView.setAdapter(attendAdapter);*/
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
        final SharedPreferences loginInfoPreferences = getApplicationContext().getSharedPreferences("loginInfoPreferences", MODE_PRIVATE);

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_log_out_detail) {
            SharedPreferences.Editor loginInfoPreferencesEditor = loginInfoPreferences.edit();
            loginInfoPreferencesEditor.putString("username", "");
            loginInfoPreferencesEditor.putString("password", "");
            loginInfoPreferencesEditor.commit();
            DetailedAttendanceActivity.this.finish();
            Intent mainActivityRefreshIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivityRefreshIntent);
          //  MainActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
