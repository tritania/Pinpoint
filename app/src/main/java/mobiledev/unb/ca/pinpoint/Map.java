package mobiledev.unb.ca.pinpoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class Map extends Activity {

    private MapView mapView = null;
    private ImageView imgv = null;
    private Marker guess, answerm = null;
    private LatLng answer;
    private FloatingActionButton btna;
    private FloatingActionButton btns;
    private Socket sock;
    private boolean mapshown = false;
    private static final String TAG = "MainActivity";


    private Emitter.Listener matchResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    byte[] decodedString = Base64.decode(args[0].toString(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    decodedByte = Bitmap.createScaledBitmap(decodedByte, imgv.getWidth(), imgv.getHeight(), false);
                    imgv.setImageBitmap(decodedByte);
                    imgv.setVisibility(View.VISIBLE);
                }
            });
        }
    };

    private Emitter.Listener matchResponseTwo = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();
                    String[] lcs = data.split(",");
                    answer = new LatLng(Double.parseDouble(lcs[0]), Double.parseDouble(lcs[1]));
                }
            });
        }
    };

    public void showAnswer() {
        IconFactory mIconFactory = IconFactory.getInstance(Map.this);
        Drawable mIconDrawable = ContextCompat.getDrawable(Map.this, R.drawable.places_ic_clear);
        Icon icon = mIconFactory.fromDrawable(mIconDrawable);
        answerm = mapView.addMarker(new MarkerOptions().title("Actual Location").position(answer).icon(icon));

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
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        sock = ((Pinpoint)this.getApplication()).startConn();
        sock.on("SIMG", matchResponse);
        sock.on("SLOC", matchResponseTwo);

        mapView = (MapView) findViewById(R.id.mapview);
        imgv = (ImageView) findViewById(R.id.imgv);
        btns = (FloatingActionButton) findViewById(R.id.fabs);
        btna = (FloatingActionButton) findViewById(R.id.faba);

        mapView.setVisibility(View.INVISIBLE);
        imgv.setVisibility(View.INVISIBLE);
        btna.setVisibility(View.INVISIBLE);

        mapView.setStyleUrl(Style.DARK);
        mapView.setCenterCoordinate(new LatLng(0.00000, 0.0000));
        mapView.setZoomLevel(1);
        mapView.onCreate(savedInstanceState);

        mapView.setOnMapClickListener(new MapView.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (guess == null) {
                    guess = mapView.addMarker(new MarkerOptions().title("Guess").position(point));
                } else {
                    mapView.removeMarker(guess);
                    guess = mapView.addMarker(new MarkerOptions().title("Guess").position(point));
                }
            }
        });

        btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mapshown) {
                    imgv.setVisibility(View.GONE);
                    mapView.setVisibility(View.VISIBLE);
                    btna.setVisibility(View.VISIBLE);
                    mapshown = true;
                } else {
                    imgv.setVisibility(View.VISIBLE);
                    mapView.setVisibility(View.GONE);
                    btna.setVisibility(View.INVISIBLE);
                    mapshown = false;
                }
            }
        });

        btna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
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