package mobiledev.unb.ca.pinpoint;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by tritania on 2/4/16.
 */
public class Pinpoint extends Application {

    Socket sock;
    {
        try {
            sock = IO.socket("http://pinpoint.magnorum.com");
        } catch (URISyntaxException e) {}
    }

    public Socket startConn() {
        sock.connect();
        return sock;
    }

    public Socket getConn() {
        return sock;
    }
}
