package mobiledev.unb.ca.pinpoint;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class Score extends Activity {
    private static final String WORST_VAL = "worst";
    private static final String BEST_VAL = "best";
    private static final String AVG_VAL = "avg";
    private static final String TOTAL_VAL = "total";
    TextView bv, wv, av, tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score);

        bv = (TextView) findViewById(R.id.nbest);
        wv = (TextView) findViewById(R.id.nworst);
        av = (TextView) findViewById(R.id.navg);
        tv = (TextView) findViewById(R.id.ntotal);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int best = preferences.getInt(BEST_VAL, 0);
        int worst= preferences.getInt(WORST_VAL, 0);
        int avg = preferences.getInt(AVG_VAL, 0);
        int total = preferences.getInt(TOTAL_VAL, 0);

        bv.setText(Integer.toString(best));
        wv.setText(Integer.toString(worst));
        av.setText(Integer.toString(avg));
        tv.setText(Integer.toString(total));
    }
}
