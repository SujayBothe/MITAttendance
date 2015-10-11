package com.vssb.mitattendance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginInputActivity extends AppCompatActivity {
    public  static EditText usernameInputField, passwordInputField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_input);
        getSupportActionBar().hide();
        usernameInputField = (MaterialEditText) findViewById(R.id.username_input_field);
        passwordInputField = (MaterialEditText) findViewById(R.id.password_input_field);
        Button loginSubmitButton = (Button) findViewById(R.id.login_submit_button);
        loginSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences loginInfoPreferences = getApplicationContext().getSharedPreferences("loginInfoPreferences",MODE_PRIVATE);
                SharedPreferences.Editor loginPreferencesEditor = loginInfoPreferences.edit();
                if (usernameInputField.getText().toString().matches("") || passwordInputField.getText().toString().matches("")) {
                    Toast.makeText(getApplicationContext(),"Username or Password field empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    loginPreferencesEditor.putString("username", usernameInputField.getText().toString());
                    loginPreferencesEditor.putString("password", passwordInputField.getText().toString());
                    loginPreferencesEditor.commit();
                    Intent mainActivityIntent = new Intent(LoginInputActivity.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    LoginInputActivity.this.finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_input, menu);
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
