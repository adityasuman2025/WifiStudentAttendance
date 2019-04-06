package com.example.studentattendance;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class Dashboard extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new CoursesFragment());
    }

//function to load fragments
    private boolean loadFragment(Fragment fragment)
    {
        if(fragment != null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

//on selecting any option on bottom navigation bar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        Fragment fragment = null;
        switch (menuItem.getItemId())
        {
            case R.id.navigation_view:
                fragment = new ViewFragment();
                break;
            case R.id.navigation_courses:
                fragment = new CoursesFragment();
                break;
            case R.id.navigation_settings:
                fragment = new SettingsFragment();
                break;
        }
        return loadFragment(fragment);
    }
}
