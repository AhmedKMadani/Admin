package com.nymeria.admin.fragment;


import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nymeria.admin.R;
import com.nymeria.admin.adapter.ServiceAdapter;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.model.service;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by AhmedKamal on 8/23/2017.
 */
public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private List<service> serviceList;
    CollapsingToolbarLayout collapsingToolbar;
    private ImageView imgHeader;
    private SQLiteHandler db;
    String CatId;
   String add_name;
   String edit_name;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(String param) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.service_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        db = new SQLiteHandler(getActivity ());
        CatId = db.getUserDetails ().get ( "CatID" );


        serviceList = new ArrayList<>();
        adapter = new ServiceAdapter(getActivity(), serviceList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareService();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void  prepareService() {
        int[] covers = new int[]{
                R.drawable.app_icon,
                R.drawable.app_icon,
                R.drawable.app_icon,
                R.drawable.app_icon,
                R.drawable.app_icon,
                R.drawable.app_icon,
                R.drawable.app_icon,
                R.drawable.app_icon,
                R.drawable.app_icon,
                R.drawable.app_icon,
                R.drawable.app_icon,
                R.drawable.app_icon,
                R.drawable.app_icon,};


        if(CatId.equals ( "2" )) {
            add_name = getResources ().getString ( R.string.service_text_add_hall );

        }else if(CatId.equals("1002")) {
            add_name = getResources ().getString ( R.string.service_text_add_artist );

        }else if(CatId.equals("1003")) {
            add_name = getResources ().getString ( R.string.service_text_add_hotel);

        }else if(CatId.equals("1004")) {
            add_name = getResources ().getString ( R.string.service_text_add_photo);

        }else if(CatId.equals("1005")) {
            add_name = getResources ().getString ( R.string.service_text_add_chef );

        }else if(CatId.equals("1006")) {
            add_name = getResources ().getString ( R.string.service_text_add_wedding );

        }else if(CatId.equals("1007")) {
            add_name = getResources ().getString ( R.string.service_text_add_coiffure );

        }


        if(CatId.equals("2")) {
            edit_name = getResources ().getString ( R.string.service_text_edit_hall );


        }else if(CatId.equals("1002")) {
            edit_name = getResources ().getString ( R.string.service_text_edit_artist );


        }else if(CatId.equals("1003")) {
            edit_name = getResources ().getString ( R.string.service_text_edit_hotel);


        }else if(CatId.equals("1004")) {
            edit_name = getResources ().getString ( R.string.service_text_edit_photo);


        }else if(CatId.equals("1005")) {
            edit_name = getResources ().getString ( R.string.service_text_edit_chef );


        }else if(CatId.equals("1006")) {
            edit_name = getResources ().getString ( R.string.service_text_edit_wedding );


        }else if(CatId.equals("1007")) {
            edit_name = getResources ().getString ( R.string.service_text_edit_coiffure );

        }

        service  a = new service(add_name, covers[0]);
        serviceList.add(a);

        a = new service(edit_name, covers[1]);
        serviceList.add(a);

        a = new service(getString(R.string.service_text_booking), covers[2]);
        serviceList.add(a);

        a = new service(getString(R.string.service_text_add_booking), covers[3]);
        serviceList.add(a);

        a = new service(getString(R.string.service_text_add_location), covers[4]);
        serviceList.add(a);

        a = new service(getString(R.string.service_text_tarck_booking), covers[5]);
        serviceList.add(a);

        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



}
