package com.czat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText userNameTxt, passwordTxt;
    private Button signInButton, registerButton;
    private Chat app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = (Chat)this.getApplication();
        userNameTxt = (EditText)findViewById(R.id.userNameTxt);
        passwordTxt = (EditText)findViewById(R.id.passwordTxt);
        signInButton = (Button)findViewById(R.id.signInButton);
        registerButton = (Button)findViewById(R.id.registerButton);
    }

    public void signInButtonClick(View v){
        String username = userNameTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        userNameTxt.setEnabled(false);
        passwordTxt.setEnabled(false);
        signInButton.setEnabled(false);
        registerButton.setEnabled(false);
        app.setOnLoginResponse(new Callback() {
            @Override
            public void run(JSONObject response) {
                Boolean success;
                try { success = response.getBoolean("success"); } catch (Exception e) { success = false; }
                if (success) {
                    Log.i("LoginActivity", "Successfully logged in as: " + app.getUsername());
                    try {
                        JSONArray contacts = response.getJSONArray("contacts");
                        JSONArray requests = response.getJSONArray("requests");
                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject obj = contacts.getJSONObject(i);
                            int id = obj.getInt("id");
                            String username = obj.getString("username");
                            ContactList.addItem(new Contact(id, username));
                        }
                        for (int i = 0; i < requests.length(); i++) {
                            JSONObject obj = requests.getJSONObject(i);
                            int id = obj.getInt("id");
                            String username = obj.getString("username");
                            RequestList.addItem(new Request(id, username));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } catch (Exception e) { }
                }
                else {
                    Log.i("LoginActivity", "Login failed");
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userNameTxt.setEnabled(true);
                            passwordTxt.setEnabled(true);
                            signInButton.setEnabled(true);
                            registerButton.setEnabled(true);
                            Toast.makeText(LoginActivity.this, getResources().getText(R.string.incorrect_credentials), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                app.setOnLoginResponse(null);
            }
        });
        app.login(username, password);
    }

    public void registerButtonClick(View v){
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }
}
