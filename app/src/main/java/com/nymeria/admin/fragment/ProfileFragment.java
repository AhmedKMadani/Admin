package com.nymeria.admin.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.nymeria.admin.R;
import com.nymeria.admin.helper.SQLiteHandler;
import com.nymeria.admin.helper.SessionManager;

/**
 * Created by user on 4/6/2018.
 */

public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
   TextView username;
   TextView phone1;
   TextView phone2;
   TextView email;
   TextView company;
   TextView address;
   TextView stats;
   TextView super_name;
   Button log;
    private MaterialRippleLayout lyt_log;

   private SQLiteHandler db;
   private SessionManager session;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance(String param) {
        ProfileFragment fragment = new ProfileFragment ();
        Bundle args = new Bundle ();
        args.putString ( ARG_PARAM1, param );
        fragment.setArguments ( args );
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate ( R.layout.profile, container, false );

        db = new SQLiteHandler (  getActivity () );
        session = new SessionManager (getActivity ());

        TextView username = (TextView) view.findViewById ( R.id.username );
        TextView phone1 = (TextView) view.findViewById ( R.id.phone1);
        TextView phone2 = (TextView) view.findViewById ( R.id.phone2);
        TextView email = (TextView) view.findViewById ( R.id.email);
        TextView company = (TextView) view.findViewById ( R.id.comp);
        TextView address = (TextView) view.findViewById ( R.id.loc);
        TextView stats = (TextView) view.findViewById ( R.id.stats);
        TextView super_name = (TextView) view.findViewById ( R.id.supername);

        lyt_log = (MaterialRippleLayout) view.findViewById ( R.id.lyt_log );

        username.setText ( String.valueOf ( db.getUserDetails ().get ( "name" ) ) );
        phone1.setText ( String.valueOf ( db.getUserDetails ().get ( "phone1" ) ) );
        phone2.setText ( String.valueOf ( db.getUserDetails ().get ( "phone2" ) ) );
        email.setText ( String.valueOf ( db.getUserDetails ().get ( "email" ) ) );
        company.setText ( String.valueOf ( db.getUserDetails ().get ( "comp" ) ) );
        address.setText ( String.valueOf ( db.getUserDetails ().get ( "adress" ) ) );
        stats.setText ( String.valueOf ( db.getUserDetails ().get ( "stats" ) ) );
        super_name.setText ( String.valueOf ( db.getUserDetails ().get ( "supername" ) ) );


        lyt_log.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                session.setLogin ( false );
                db.deleteUsers ();
                getActivity ().finish ();
            }
        } );


        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
    }
}