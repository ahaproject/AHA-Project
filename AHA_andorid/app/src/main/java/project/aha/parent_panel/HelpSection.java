package project.aha.parent_panel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import project.aha.Constants;
import project.aha.R;

public class HelpSection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_section);
        Constants.showLogo(this);

    }
}
