package com.radioserver.bristolbeat.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import com.radioserver.bristolbeat.R;
import com.radioserver.bristolbeat.helpers.SharedAlgorithm;

import org.san.iphonestyle.CustomScreen;

public class SocialScreen extends CustomScreen implements OnClickListener {

    Button btnFacebook, btnTwitter, btnWebsite, btnCall, btnEmail;
    Button btnInstagram, btnCalendar, btnBuilding, btnContests;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_social, container, false);

        this.initComponents(view);
        this.setListeners();

        return view;
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initComponents(View container) {
        btnFacebook = (Button) container.findViewById(R.id.btnFacebook);
        btnTwitter = (Button) container.findViewById(R.id.btnTwitter);
        btnWebsite = (Button) container.findViewById(R.id.btnWebsite);
        btnCall = (Button) container.findViewById(R.id.btnCall);
        btnEmail = (Button) container.findViewById(R.id.btnEmail);
        btnInstagram = (Button) container.findViewById(R.id.btnInstagram);
        btnCalendar = (Button) container.findViewById(R.id.btnCalendar);
        btnBuilding = (Button) container.findViewById(R.id.btnBuilding);
        btnContests = (Button) container.findViewById(R.id.btnContests);

        SharedAlgorithm.setupAdView((WebView) container.findViewById(R.id.adView), getActivity());
    }

    @Override
    protected void setListeners() {
        btnFacebook.setOnClickListener(this);
        btnTwitter.setOnClickListener(this);
        btnWebsite.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnEmail.setOnClickListener(this);
        btnInstagram.setOnClickListener(this);
        btnCalendar.setOnClickListener(this);
        btnBuilding.setOnClickListener(this);
        btnContests.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFacebook:
                goWebsite(getString(R.string.facebook_url));
                break;
            case R.id.btnTwitter:
                goWebsite(getString(R.string.twitter_url));
                break;
            case R.id.btnWebsite:
                goWebsite(getString(R.string.website_url));
                break;
            case R.id.btnCall:
                makeACall(getString(R.string.phone_number));
                break;
            case R.id.btnEmail:
                sendEmail(getString(R.string.email_address));
                break;
            case R.id.btnInstagram:
                goWebsite(getString(R.string.instagram_url));
                break;
            case R.id.btnCalendar:
                goWebsite(getString(R.string.calendar_url));
                break;
            case R.id.btnBuilding:
                goWebsite(getString(R.string.advertisers_url));
                break;
            case R.id.btnContests:
                goWebsite(getString(R.string.contests_url));
                break;
        }
    }

    private void goWebsite(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void makeACall(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);

        callIntent.setData(Uri.parse("tel://" + number));
        this.startActivityForResult(callIntent, 911);
    }

    private void sendEmail(String emailAddress) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", emailAddress, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(R.string.app_name) + " Email");
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    private void sendSMS(String number) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
    }
}