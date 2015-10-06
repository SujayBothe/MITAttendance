package com.vssb.mitattendance;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by vipluv.shetty on 9/28/2015.
 */
public class RequestTask extends AsyncTask<String, String, String> {
    public static String attendanceUrl;
    public static int flag = 0;
    SharedPreferences.Editor loginInfoPreferencesEditor;
    Context callerContext;
    Intent mainActivityRefreshIntent;
    public AsyncResponse delegate = null;
    public RequestTask(Context context,SharedPreferences.Editor someEditor,AsyncResponse asyncResponse) {
        callerContext = context;
        this.loginInfoPreferencesEditor = someEditor;
        delegate = asyncResponse;
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            Log.i("vssb", "url is " + uri[0]);
            response = httpclient.execute(new HttpPost(uri[0]));
            Log.i("vssb", "got response");
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
                Log.i("vssb", "response is " + responseString);
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (flag == 0) {
            try {
                Log.i("vssb", result);
                if (Integer.parseInt(result.substring(result.indexOf("STATUS") + 8, result.indexOf("STATUS") + 9)) == 0) {
                    //Invalidate login
                    Log.i("vssb", "login failed");
                    Toast.makeText(callerContext, "Login failed: " + result.substring(result.indexOf("MSG") + 5, result.indexOf("/>") - 1), Toast.LENGTH_SHORT).show();
                    //          MainActivity.progressView.setText(result.substring(result.indexOf("MSG") + 5, result.indexOf("/>") - 1));
                    loginInfoPreferencesEditor.putString("username", "");
                    loginInfoPreferencesEditor.putString("password", "");
                    loginInfoPreferencesEditor.commit();
                    mainActivityRefreshIntent = new Intent(callerContext, MainActivity.class);
                    callerContext.startActivity(mainActivityRefreshIntent);
                    ((Activity) callerContext).finish();

                } else {
                    attendanceUrl = "https://e01s00.tcsion.com:443/iONBizServices/iONWebService?servicekey=CM7Td4GhiW3AyBS7uUup0A%3D%3D&s=nVSeYVnuHgHlkxpXkJEf%2Bg%3D%3D&tokenid=" + result.substring(result.indexOf("TOKENID") + 11, result.indexOf("MSG") - 2);
                    Log.i("vssb", attendanceUrl);
                    flag = 1 ;
                    //delegate.processFinish(attendanceUrl);
                    new RequestTask(callerContext, loginInfoPreferencesEditor,delegate).execute(attendanceUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
           // mainActivityRefreshIntent = new Intent(callerContext, MainActivity.class);
           // mainActivityRefreshIntent.putExtra("response",result);
          //  callerContext.startActivity(mainActivityRefreshIntent);
            flag = 0 ;
            delegate.processFinish(result);
           // ((Activity) callerContext).finish();
        }

    }
}
