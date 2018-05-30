package com.nymeria.admin.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nymeria.admin.R;
import com.nymeria.admin.fragment.AddDataFragment;
import com.nymeria.admin.fragment.AddImageFragment;
import com.nymeria.admin.fragment.AddServiceFragment;
import com.nymeria.admin.helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 3/31/2018.
 */

public class AddingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SQLiteHandler db;
    String CatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_adding);

        db = new SQLiteHandler (getApplicationContext ());
        CatId = db.getUserDetails ().get ( "CatID" );


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(CatId.equals("2")) {
            getSupportActionBar().setTitle(R.string.service_text_add_hall);

        }else if(CatId.equals("1002")) {
            getSupportActionBar().setTitle(R.string.service_text_add_artist);

        }else if(CatId.equals("1003")) {
            getSupportActionBar().setTitle(R.string.service_text_add_hotel);

        }else if(CatId.equals("1004")) {
            getSupportActionBar().setTitle(R.string.service_text_add_photo);


        }else if(CatId.equals("1005")) {
            getSupportActionBar().setTitle(R.string.service_text_add_chef);

        }else if(CatId.equals("1006")) {
            getSupportActionBar().setTitle(R.string.service_text_add_wedding);

        }else if(CatId.equals("1007")) {
            getSupportActionBar().setTitle(R.string.service_text_add_coiffure);

        }else if(CatId.equals("1007")) {
            getSupportActionBar().setTitle(R.string.service_text_add_coiffure);

        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddDataFragment (),getResources ().getString ( R.string.add_data ) );
        adapter.addFragment(new AddServiceFragment (), getResources ().getString ( R.string.add_services ));
        adapter.addFragment(new AddImageFragment (), getResources ().getString ( R.string.add_image ) );
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<> ();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed ();
        finish ();
    }
}