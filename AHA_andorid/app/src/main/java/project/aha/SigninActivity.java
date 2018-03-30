package project.aha;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

public class SigninActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;


    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



    }





    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return FirstFragment.newInstance();
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return SecondFragment.newInstance();
                case 2: // Fragment # 1 - This will show SecondFragment
                    return ThirdFragment.newInstance();
                default:
                    return null;
            }

//            // getItem is called to instantiate the fragment for the given page.
//            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }


    }


    public static class FirstFragment extends Fragment {
        // Store instance variables


        public static FirstFragment instance;
        private String title;
        private int page = 1;

        // newInstance constructor for creating fragment with arguments
        public static FirstFragment newInstance() {
            if(instance == null) {
                instance = new FirstFragment();
            }

            return instance;
        }

        private FirstFragment(){

        }
        // Store instance variables based on arguments passed
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        // Inflate the view for the fragment based on layout XML
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.signup_fragment_first, container, false);

            return view;
        }
    }



    public static class SecondFragment extends Fragment {
        // Store instance variables


        public static SecondFragment instance;
        private String title;
        private int page = 2;

        // newInstance constructor for creating fragment with arguments
        public static SecondFragment newInstance() {
            if(instance == null) {
                instance = new SecondFragment();
            }

            return instance;
        }

        private SecondFragment(){

        }
        // Store instance variables based on arguments passed
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        // Inflate the view for the fragment based on layout XML
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.signup_fragment_second, container, false);

            return view;
        }
    }



    public static class ThirdFragment extends Fragment {
        // Store instance variables


        public static ThirdFragment instance;
        private String title;
        private int page = 3;

        // newInstance constructor for creating fragment with arguments
        public static ThirdFragment newInstance() {
            if(instance == null) {
                instance = new ThirdFragment();
            }

            return instance;
        }

        private ThirdFragment(){

        }
        // Store instance variables based on arguments passed
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        // Inflate the view for the fragment based on layout XML
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.signup_fragment_third, container, false);
            return view;
        }
    }

}


