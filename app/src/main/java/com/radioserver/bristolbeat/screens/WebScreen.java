package com.radioserver.bristolbeat.screens;

import org.san.iphonestyle.CustomScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.radioserver.bristolbeat.R;
import com.radioserver.bristolbeat.helpers.SharedAlgorithm;

/**
 * Custom class for Web Screen
 *
 * @see WebTab which is the container of the screen
 */
public class WebScreen extends CustomScreen implements OnClickListener {

    private WebView webContent;
    private ProgressBar prgLoading;
    private Button btnReload, btnNext, btnPre;

    private String mUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_web, container, false);

        this.initComponents(view);
        this.setListeners();

        this.loadData();

        return view;
    }

    @Override
    protected void initData() {
        Bundle bundle = this.getArguments();
        mUrl = bundle.getString("URL");
    }

    @Override
    protected void initComponents(View container) {
        webContent = (WebView) container.findViewById(R.id.webContent);
        prgLoading = (ProgressBar) container.findViewById(R.id.prgLoading);
        btnReload = (Button) container.findViewById(R.id.btnReload);
        btnNext = (Button) container.findViewById(R.id.btnNext);
        btnPre = (Button) container.findViewById(R.id.btnPre);

        webContent.setBackgroundColor(0x00000000);
        webContent.getSettings().setBuiltInZoomControls(true);
        WebSettings webSettings = webContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                prgLoading.setVisibility(View.GONE);
            }
        });

        SharedAlgorithm.setupAdView((WebView) container.findViewById(R.id.adView), getActivity());
    }

    @Override
    protected void setListeners() {
        btnReload.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPre.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnReload) {
            prgLoading.setVisibility(View.VISIBLE);
            webContent.reload();
        } else if (v.getId() == R.id.btnNext) {
            webContent.goForward();
        } else if (v.getId() == R.id.btnPre) {
            webContent.goBack();
        }
    }

    private void loadData() {
        webContent.loadUrl(mUrl);
    }
}