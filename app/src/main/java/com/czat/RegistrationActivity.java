package com.czat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userNameTxt, passwordTxt, repeatPasswordTxt;
    private Chat app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        app = (Chat)this.getApplication();
        userNameTxt = (EditText)findViewById(R.id.userNameTxt);
        passwordTxt = (EditText)findViewById(R.id.passwordTxt);
        repeatPasswordTxt = (EditText)findViewById(R.id.repeatPasswordTxt);
    }

    public void registerButtonClick(View v) {
        String username = userNameTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        String password2 = repeatPasswordTxt.getText().toString();

        if (username.length() < 4) {
            Toast.makeText(this, getResources().getText(R.string.error_username_short), Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(password2)) {
            Toast.makeText(this, getResources().getText(R.string.passwords_not_matching), Toast.LENGTH_LONG).show();
            return;
        }
        if (password.length() < 4) {
            Toast.makeText(this, getResources().getText(R.string.error_password_short), Toast.LENGTH_LONG).show();
            return;
        }

        final Button registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setEnabled(false);
        userNameTxt.setEnabled(false);
        passwordTxt.setEnabled(false);
        repeatPasswordTxt.setEnabled(false);

        app.setOnRegisterResponse(new Callback() {
            @Override
            public void run(JSONObject response) {
                Boolean success;
                try { success = response.getBoolean("success"); } catch (Exception e) { success = false; }
                if (success) {
                    //String username;
                    //try {
                        //username = response.getString("username");
                        Log.i("RegistrationActivity", "Successfully registered account: " + app.getUsername());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    //}
                    //catch (org.json.JSONException e) { e.printStackTrace(); }
                }
                else {
                    Log.i("RegistrationActivity", "Registration failed");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            userNameTxt.setEnabled(true);
                            passwordTxt.setEnabled(true);
                            repeatPasswordTxt.setEnabled(true);
                            registerButton.setEnabled(true);
                        }
                    });
                }
            }
        });
        app.register(username, password);
    }
}
