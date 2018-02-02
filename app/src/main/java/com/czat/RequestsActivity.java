package com.czat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

public class RequestsActivity extends AppCompatActivity implements RequestFragment.OnListFragmentInteractionListener {

    private Chat app;
    private RequestFragment requestFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        app = (Chat)this.getApplication();
        requestFragment = ((RequestFragment) getSupportFragmentManager().findFragmentByTag("RequestFragment"));
    }

    @Override
    public void onListFragmentInteraction(final Request request) {
        Log.d("RequestActivity", "onListFragmentInteraction, userId: " + request.getId());
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        app.setOnAcceptRequestResponse(new Callback() {
                            @Override
                            public void run(JSONObject response) {
                                try {
                                    Boolean success = response.getBoolean("success");
                                    if (success) {
                                        RequestList.removeItem(request);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                RequestsActivity.this.finish();
                                            }
                                        });
                                    }
                                }
                                catch (Exception e) { e.printStackTrace(); }
                                app.setOnAcceptRequestResponse(null);
                            }
                        });
                        app.acceptRequest(request.getId());
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        String message = String.format(getResources().getText(R.string.confirm_request).toString(), request.getUsername());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setPositiveButton(getResources().getText(R.string.yes).toString(), dialogClickListener)
                .setNegativeButton(getResources().getText(R.string.no).toString(), dialogClickListener).show();
    }
}
