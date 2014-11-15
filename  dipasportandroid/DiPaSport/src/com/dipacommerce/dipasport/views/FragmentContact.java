package com.dipacommerce.dipasport.views;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dipacommerce.dipasport.DiPaSport;
import com.dipacommerce.dipasport.R;
import com.dipacommerce.dipasport.data.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FragmentContact extends DiPaSport<Object, Object> {

    private GoogleMap mMap;

    public FragmentContact() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BuildActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_page, container, false);
        UpdateViews(rootView);

        getFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                // TODO Auto-generated method stub
                int backCount = getFragmentManager().getBackStackEntryCount();
                if (backCount == 0) {
                    BuildActionBar();
                }
            }
        });
        return rootView;
    }

    @Override
    protected void BuildActionBar() {
        if (sCtx != null) {
            Activity activity = ((Activity) sCtx);
            View mCustomTitle = activity.getLayoutInflater().inflate(R.layout.ctrl_common_title, null);
            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                ((TextView) mCustomTitle.findViewById(R.id.title_name)).setText(activity.getString(R.string.str_home_title));

                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(mCustomTitle);
            }
        }
    }

    @Override
    public void UpdateTabChanged(String tabId) {
        Fragment fmg = getFragmentManager().findFragmentByTag("social");
        if (fmg == null) {
            BuildActionBar();
        } else {
            BuildActionBarForSocial();
        }
    }

    @Override
    public void UpdateViews(View rootView) {
        TextView tel = (TextView) rootView.findViewById(R.id.contact_tel);
        if (tel != null) {
            tel.setOnClickListener(mOnTelClick);
        }

        TextView email = (TextView) rootView.findViewById(R.id.contact_email);
        if (email != null) {
            email.setOnClickListener(mOnEmailClick);
        }

        ImageView maps = (ImageView) rootView.findViewById(R.id.contact_maps);
        if (maps != null) {
            maps.setOnClickListener(mOnMapsClick);
        }

        ImageView facebook = (ImageView) rootView.findViewById(R.id.contact_facebook);
        if (facebook != null) {
            facebook.setOnClickListener(mOnFacebookClick);
        }

        ImageView twitter = (ImageView) rootView.findViewById(R.id.contact_twitter);
        if (twitter != null) {
            twitter.setOnClickListener(mOnTwitterClick);
        }

        ImageView youtube = (ImageView) rootView.findViewById(R.id.contact_youtube);
        if (youtube != null) {
            youtube.setOnClickListener(mOnYoutubeClick);
        }

        // Setup Google Map
        FragmentActivity fragmentActivity = (FragmentActivity) sCtx;
        MapFragment supportMapFragment = ((MapFragment) fragmentActivity.getFragmentManager().findFragmentById(R.id.map));
        if (supportMapFragment != null) {
            if (mMap == null) {
                mMap = supportMapFragment.getMap();
                if (mMap != null) {
                    // Interact with map here
                    // 44.971776, 9.855024
                    LatLng address = new LatLng(Constants.GOOGLE_MAP_LAT, Constants.GOOGLE_MAP_LNG);
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(address);
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    marker.title(sCtx.getResources().getString(R.string.str_contact_company));
                    mMap.addMarker(marker);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(address, Constants.GOOGLE_ZOOM_LEVEL));
                }
            }
        }
    }

    private OnClickListener mOnTelClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            final AlertDialog.Builder confirm = new AlertDialog.Builder(sCtx);
            confirm.setMessage(sCtx.getString(R.string.str_home_call_confirm));

            // Call
            confirm.setPositiveButton(sCtx.getString(R.string.str_home_call_confirm_ok), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + getString(R.string.phone_number)));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                }

            });

            // Cancel and back to Home
            confirm.setNegativeButton(sCtx.getString(R.string.str_home_call_confirm_cancel), null);
            confirm.create().show();
        }
    };

    private OnClickListener mOnEmailClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intentSentMail = new Intent(Intent.ACTION_SEND);
            intentSentMail.setType("message/rfc822");
            intentSentMail.putExtra(Intent.EXTRA_EMAIL, new String[] { Constants.EMAIL_INFO });
            startActivity(Intent.createChooser(intentSentMail, "Choose an Email client :"));
        }
    };

    private OnClickListener mOnMapsClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String device_type = sCtx.getResources().getString(R.string.screen_type);
            if (device_type.equals("phone")) {
                FragmentSocialManager social = new FragmentSocialManager();
                Bundle args = new Bundle();
                args.putString("url", Constants.GOOGLE_MAP_URL);
                social.setArguments(args);
                FragmentTransaction fs = getFragmentManager().beginTransaction();
                fs.addToBackStack(null);
                fs.add(R.id.tab_contact, social, "social");
                fs.commit();
            } else {
                Intent maps = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MAPS_URL));
                maps.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                try {
                    startActivity(maps);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    try {
                        Intent unrestrictintent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MAPS_URL));
                        startActivity(unrestrictintent);
                    } catch (ActivityNotFoundException innerEx)
                    {
                        Toast.makeText(sCtx, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }

            }
        }
    };

    private OnClickListener mOnFacebookClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            FragmentSocialManager social = new FragmentSocialManager();
            Bundle args = new Bundle();
            args.putString("url", Constants.FACEBOOK_URL);
            social.setArguments(args);
            FragmentTransaction fs = getFragmentManager().beginTransaction();
            fs.addToBackStack(null);
            fs.add(R.id.tab_contact, social, "social");
            fs.commit();
        }
    };

    private OnClickListener mOnTwitterClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            FragmentSocialManager social = new FragmentSocialManager();
            Bundle args = new Bundle();
            args.putString("url", Constants.TWITTER_URL);
            social.setArguments(args);
            FragmentTransaction fs = getFragmentManager().beginTransaction();
            fs.addToBackStack(null);
            fs.add(R.id.tab_contact, social, "social");
            fs.commit();
        }
    };

    private OnClickListener mOnYoutubeClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            FragmentSocialManager social = new FragmentSocialManager();
            Bundle args = new Bundle();
            args.putString("url", Constants.YOUTUBE_URL);
            social.setArguments(args);
            FragmentTransaction fs = getFragmentManager().beginTransaction();
            fs.addToBackStack(null);
            fs.add(R.id.tab_contact, social, "social");
            fs.commit();
        }
    };

    private void BuildActionBarForSocial() {
        if (sCtx != null) {
            Activity activity = ((Activity) sCtx);
            View mCustomTitle = activity.getLayoutInflater().inflate(R.layout.ctrl_common_title, null);

            ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                ((TextView) mCustomTitle.findViewById(R.id.title_name)).setText("");

                ImageView logo = (ImageView) mCustomTitle.findViewById(R.id.logo);
                if (logo != null) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    logo.setLayoutParams(params);
                }

                TextView back = (TextView) mCustomTitle.findViewById(R.id.back);
                if (back != null) {
                    back.setVisibility(View.VISIBLE);
                    back.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            getFragmentManager().popBackStack();
                        }
                    });
                }

                actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                actionBar.setCustomView(mCustomTitle);

            }
        }
    }
}
