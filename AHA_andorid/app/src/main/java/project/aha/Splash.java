package project.aha;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import project.aha.admin_panel.AdminMainActivity;
import project.aha.doctor_panel.DoctorMainActivity;
import project.aha.parent_panel.ParentMainActivity;

public class Splash extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splashscreen);



        SharedPreferences prefs = getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
        final int user_id = prefs.getInt(Constants.PREF_USER_LOGGED_ID, -1);
        final int user_type = prefs.getInt(Constants.PREF_USER_LOGGED_TYPE, -1);


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                if(user_id == -1){ // no user is logged in
                    Intent mainIntent = new Intent(Splash.this,LoginActivity.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                } else{
                    Intent i = null ;
                    switch(user_type){
                        case Constants.ADMIN_TYPE :
                            i = new Intent(Splash.this,AdminMainActivity.class);
                            break;

                        case Constants.DOCTOR_TYPE:
                            i = new Intent(Splash.this,DoctorMainActivity.class);
                            break;

                        case Constants.PARENT_TYPE:
                            i = new Intent(Splash.this,ParentMainActivity.class);
                            break;
                    }
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}