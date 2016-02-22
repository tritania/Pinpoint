package mobiledev.unb.ca.pinpoint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Map extends Activity {

    private MapView mapView = null;
    private ImageView imgv = null;
    private Marker guess = null;
    private FloatingActionButton btna;
    private FloatingActionButton btns;
    private Socket sock;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        sock = ((Pinpoint)this.getApplication()).startConn();
        sock.on("SIMG", matchResponse);


        mapView = (MapView) findViewById(R.id.mapview);
        imgv = (ImageView) findViewById(R.id.imgv);
        btns = (FloatingActionButton) findViewById(R.id.fabs);

        mapView.setVisibility(View.INVISIBLE);
        imgv.setVisibility(View.INVISIBLE);

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
                if (mapView.getVisibility() == View.GONE) {
                    imgv.setVisibility(View.GONE);
                    mapView.setVisibility(View.VISIBLE);
                } else {
                    imgv.setVisibility(View.VISIBLE);
                    mapView.setVisibility(View.GONE);
                }
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