package mobiledev.unb.ca.pinpoint;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.view.animation.PathInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Waiting extends Activity {

    TextView wtext;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Socket sock;
    private GoogleApiClient mGoogleApiClient;
    String mCurrentPhotoPath;
    private Location local;
    private LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting);

        wtext = (TextView) findViewById(R.id.waittxt);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        getLocation();

        sock = ((Pinpoint)this.getApplication()).startConn();
        sock.on("mr", matchResponse);
        sock.emit("findmatch", "true");
    }

    private Emitter.Listener matchResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (args[0].toString().equals("LFG")) { //change activity to waiting

                    } else if (args[0].toString().equals("TP")) { //prompt camera
                        TakePhoto();
                    } else if (args[0].toString().equals("WFI")) { //go to map
                        Intent intent = new Intent(Waiting.this, Map.class);
                        startActivity(intent);
                        finish();
                    } else if (args[0].toString().equals("LC")) {
                        Context context = getApplicationContext();
                        CharSequence text = "Player 2 Has Left!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
            });
        }
    };

    public void TakePhoto() {
        Intent camIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camIntent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0){
            try {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                sock.emit("imgd", encodedImage);
                sock.emit("locd", Double.toString(local.getLatitude()) + "," + Double.toString(local.getLongitude()));
                Intent intent = new Intent(Waiting.this, MapAns.class);
                startActivity(intent);
                finish();            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void getLocation() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, null, null);
            local = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
        }
    }
}
