package com.ariana.newspop;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		
		String url = null;
		
		if(getIntent().getStringExtra("ArticleUrl") != null) {
			url = getIntent().getStringExtra("ArticleUrl");

		}
		
		WebView wv = (WebView) findViewById(R.id.webview);
		wv.setWebViewClient(new WebViewClient());
		wv.loadUrl(url);
	}
}
