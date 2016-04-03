package mobiledev.unb.ca.pinpoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class MapAns extends Activity {

    private MapView mapView = null;
    private Marker guess, answerm = null;
    private LatLng gu, an;
    private TextView hintwait;
    private Socket sock;
    private FloatingActionButton btna;
    private static final String WORST_VAL = "worst";
    private static final String BEST_VAL = "best";
    private static final String AVG_VAL = "avg";
    private static final String TOTAL_VAL = "total";

    private Emitter.Listener matchResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();
                    String[] lcs = data.split(",");
                    an = new LatLng(Double.parseDouble(lcs[0]), Double.parseDouble(lcs[1]));
                    gu = new LatLng(Double.parseDouble(lcs[2]), Double.parseDouble(lcs[3]));
                    hintwait.setVisibility(View.GONE);
                    btna.setEnabled(true);
                    showAnswer();
                }
            });
        }
    };


    public void showAnswer() {
        answerm = mapView.addMarker(new MarkerOptions().title("Your Location").position(an));
        guess = mapView.addMarker(new MarkerOptions().title("Guess").position(gu));

        mapView.addPolyline(new PolylineOptions()
                .add(new LatLng[]{answerm.getPosition(), guess.getPosition()})
                .color(Color.parseColor("#3bb2d0"))
                .width(2));

        double scored = ((40075 - (guess.getPosition().distanceTo(answerm.getPosition()) / 1000)) / 40075);
        scored = Math.pow(Math.E, 1 - (1/Math.pow(scored, 8))) * 1000;
        int score = (int) scored;
        /*40075 being the circumference of the earth in KM
            Scoring algorithm works as follows, x being the distance of actual to guess, c being circumference
            convert x to KM
            s = (c - x)/c
            followed by the actual score generation
            e^(1 - (1 / s^8)) * 1000
            using exp to get a higher number the closer you are
            s is raised to the power of 8 to shrink the value the further away you are from correct guess
        */
        Context context = getApplicationContext();
        CharSequence text = "They Scored: " + Integer.toString(score);
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        score = (1000 - score / 3); //why not give them some points too :)
        saveScore(score);
    }

    public void saveScore(int score) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int best = preferences.getInt(BEST_VAL, 0);
        int worst= preferences.getInt(WORST_VAL, 0);
        int avg = preferences.getInt(AVG_VAL, 0);
        int total = preferences.getInt(TOTAL_VAL, 0);

        if (best < score || best == 0) {
            best = score;
        }
        if (worst > score || worst == 0) {
            worst = score;
        }
        total++;
        avg = ((avg * (total - 1)) + score) / total;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(BEST_VAL, best);
        editor.putInt(WORST_VAL, worst);
        editor.putInt(AVG_VAL, avg);
        editor.putInt(TOTAL_VAL, total);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapans);

        sock = ((Pinpoint)this.getApplication()).startConn();
        sock.on("OPS", matchResponse);

        mapView = (MapView) findViewById(R.id.mapview);
        btna = (FloatingActionButton) findViewById(R.id.faba);
        hintwait = (TextView) findViewById(R.id.imagewait);

        btna.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        btna.setEnabled(false);

        mapView.setStyleUrl(Style.DARK);
        mapView.setCenterCoordinate(new LatLng(0.00000, 0.0000));
        mapView.setZoomLevel(1);
        mapView.onCreate(savedInstanceState);

        btna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapAns.this, Score.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause()  {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}