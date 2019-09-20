package com.udacity.ak.localroots.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.udacity.ak.localroots.R;
import com.udacity.ak.localroots.ui.fragment.FavoritesFragment;
import com.udacity.ak.localroots.ui.fragment.ListFragment;
import com.udacity.ak.localroots.ui.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {
    public static final String SEARCH_EXTRA = "search_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create fragments
        if(savedInstanceState == null) {

            SearchFragment searchFragment = SearchFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.search_container, searchFragment)
                    .commit();

            FavoritesFragment favoritesFragment = FavoritesFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.favorites_container, favoritesFragment)
                    .commit();
        }
    }

}
