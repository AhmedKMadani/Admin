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
import com.nymeria.admin.fragment.EditDataFragment;
import com.nymeria.admin.fragment.EditImageFragment;
import com.nymeria.admin.fragment.EditServiceFragment;
import com.nymeria.admin.helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 3/31/2018.
 */

public class EditActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SQLiteHandler db;
    String CatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_edit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new SQLiteHandler (getApplicationContext ());
        CatId = db.getUserDetails ().get ( "CatID" );

        if(CatId.equals("2")) {
            getSupportActionBar().setTitle(R.string.service_text_edit_hall);

        }else if(CatId.equals("1002")) {
            getSupportActionBar().setTitle(R.string.service_text_edit_artist);

        }else if(CatId.equals("1003")) {
            getSupportActionBar().setTitle(R.string.service_text_edit_hotel);

        }else if(CatId.equals("1004")) {
            getSupportActionBar().setTitle(R.string.service_text_edit_photo);


        }else if(CatId.equals("1005")) {
            getSupportActionBar().setTitle(R.string.service_text_edit_chef);

        }else if(CatId.equals("1006")) {
            getSupportActionBar().setTitle(R.string.service_text_edit_wedding);

        }else if(CatId.equals("1007")) {
            getSupportActionBar().setTitle(R.string.service_text_edit_coiffure);

        }else if(CatId.equals("1007")) {
            getSupportActionBar().setTitle(R.string.service_text_edit_coiffure);

        }


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new EditDataFragment (),getResources ().getString ( R.string.edit_data ) );
        adapter.addFragment(new EditServiceFragment (), getResources ().getString ( R.string.edit_services ));
        adapter.addFragment(new EditImageFragment (), getResources ().getString ( R.string.edit_image ) );
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