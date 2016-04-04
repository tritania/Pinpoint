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
    private TextView hintwait;
    private Socket sock;
    private FloatingActionButton btna;

    private Emitter.Listener matchResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };


    public void showAnswer() {
        IconFactory mIconFactory = IconFactory.getInstance(MapAns.this);
        Drawable mIconDrawable = ContextCompat.getDrawable(MapAns.this, R.drawable.places_ic_clear);
        Icon icon = mIconFactory.fromDrawable(mIconDrawable);
        answerm = mapView.addMarker(new MarkerOptions().title("Actual Location").position(answerm.getPosition()).icon(icon));

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
        CharSequence text = "You Scored: " + Integer.toString(score);
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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