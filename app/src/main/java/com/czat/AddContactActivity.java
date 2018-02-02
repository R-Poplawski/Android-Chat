package com.czat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class AddContactActivity extends AppCompatActivity {

    private Chat app;
    private EditText userNameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        app = (Chat)this.getApplication();
        userNameTxt = (EditText)findViewById(R.id.userNameTxt);
    }

    public void addContactButtonClick(View v) {
        String username = userNameTxt.getText().toString();
        app.setOnRequestContactResponse(new Callback() {
            @Override
            public void run(JSONObject response) {
                Boolean success;
                try { success = response.getBoolean("success"); } catch (Exception e) { success = false; }
                if (success) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, getResources().getText(R.string.request_sent), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddContactActivity.this, getResources().getText(R.string.error), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                app.setOnRequestContactResponse(null);
            }
        });
        app.requestContact(username);
    }
}
