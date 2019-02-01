package org.danilocarvalho.Projeto;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ProjetoActivity extends Activity {
	
	private static final String TAG = "Projeto::Activity";

    private MenuItem mItemGlasses;
    private MenuItem mItemNose;
    private MenuItem mItemDistort;

    public static float minFaceSize = 0.5f;
    public static boolean glassesOn = false;
    public static boolean noseOn = false;
    public static boolean distort = false;		
    
    public ProjetoActivity() {
    	Log.i(TAG, "Instantiated new " + this.getClass());
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new FaceDetectionView(this));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        mItemGlasses = menu.add("Glasses On/Off");
        mItemNose = menu.add("Nose On/Off");
        mItemDistort = menu.add("Distortion On/Off");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Menu Item selected " + item);
        if (item == mItemGlasses)
            glassesOn = !glassesOn;
        else if (item == mItemNose)
            noseOn = !noseOn;
        else if (item == mItemDistort)
        	distort = !distort;
        return true;
    }
}