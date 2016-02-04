package mobiledev.unb.ca.pinpoint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.Application;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainMenu extends AppCompatActivity {

    Socket sock;
    {
        try {
            sock = IO.socket("http://pinpoint.magnorum.com");
        } catch (URISyntaxException e) {}
    }

    Button playbutton;
    Button settingsbutton;

    private static final String DEBUG = "Connection Response: ";

    private Emitter.Listener matchResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args[0].toString().equals("LFG")) { //change fragment to waiting

                    } else if (args[0].toString().equals("TP")) { //prompt camera

                    } else if (args[0].toString().equals("WFI")) { //go to map

                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        sock.on("mr", matchResponse);
        sock.connect();


        playbutton = (Button) findViewById(R.id.playbutton);
        settingsbutton = (Button) findViewById(R.id.settingsbutton);

        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sock.emit("findmatch", "true");
            }
        });

        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void checkMatch() {

    }
}
