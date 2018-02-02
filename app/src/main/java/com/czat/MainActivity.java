package com.czat;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements ContactFragment.OnListFragmentInteractionListener {

    private Chat app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (Chat)this.getApplication();

        if (!app.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onListFragmentInteraction(Contact item) {
        MessageFragment messageFragment = (MessageFragment) (getFragmentManager().findFragmentById(R.id.messageFragment));
        if (messageFragment == null) {
            Intent intent = new Intent(this, ConversationActivity.class);
            intent.putExtra("contact", item);
            startActivity(intent);
        }
        else {
            messageFragment.setContact(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_requests:
                Intent requestsIntent = new Intent(this, RequestsActivity.class);
                startActivity(requestsIntent);
                return true;
            case R.id.action_logout:
                app.logout();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
