package com.example.projrctmanagement.StartActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.cuberto.liquid_swipe.LiquidPager;
import com.example.projrctmanagement.MainActivity;
import com.example.projrctmanagement.R;
import com.google.android.material.button.MaterialButton;

public class OnBoardingActivity extends AppCompatActivity {

    LiquidPager pager;
    private static final int NUM_PAGE = 3;
    private ViewPager viewPager;
    private ScreenSlidPagerAdapter pagerAdapter;
    MaterialButton gotologinbtn;
//    viewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (restorePrefData()) {

            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class );
            startActivity(mainActivity);
            finish();

        }

        setContentView(R.layout.activity_on_boarding);
        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.fragment_c,null,false);
        MaterialButton btn = view.findViewById(R.id.sharedPref);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: "+"Success");
                Toast.makeText(OnBoardingActivity.this, "success", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private class ScreenSlidPagerAdapter extends FragmentStatePagerAdapter{

        public ScreenSlidPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0: return new FragmentA();
                case 1: return new FragmentB();
                case 2: return new FragmentC();

            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGE;
        }
    }

    private boolean restorePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        return pref.getBoolean("isIntroOpened",false);

    }

    public void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened",true);
        editor.apply();


    }


}