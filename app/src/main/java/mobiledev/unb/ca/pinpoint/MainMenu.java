package mobiledev.unb.ca.pinpoint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.Application;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainMenu extends AppCompatActivity {

    Socket sock;
    Button playbutton;
    Button settingsbutton;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final String DEBUG = "Connection Response: ";

    private Emitter.Listener matchResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.v(DEBUG, args[0].toString());
                    if (args[0].toString().equals("LFG")) { //change activity to waiting

                    } else if (args[0].toString().equals("TP")) { //prompt camera
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    } else if (args[0].toString().equals("WFI")) { //go to map
                        Intent intent = new Intent(MainMenu.this, Map.class);
                        startActivity(intent);
                    }
                }
            });
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        sock = ((Pinpoint)this.getApplication()).startConn();
        sock.on("mr", matchResponse);

        playbutton = (Button) findViewById(R.id.playbutton);
        settingsbutton = (Button) findViewById(R.id.settingsbutton);

        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkForConn(sock)) {
                    return;
                }
                playbutton.setEnabled(false);
                sock.emit("findmatch", "true");
            }
        });

        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public boolean checkForConn(Socket sock) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

        } else {
            Context context = getApplicationContext();
            CharSequence text = "ould not connect to Server!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }
        if (sock.connected() == false) {
            Context context = getApplicationContext();
            CharSequence text = "Could not connect to Server!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }
        return true;
    }

    public void checkMatch() {

    }
}
