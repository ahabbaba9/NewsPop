package com.ariana.newspop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.ariana.newspop.http.AsyncResponse;
import com.ariana.newspop.http.RequestTask;

public class SplashScreen extends ActionBarActivity implements AsyncResponse {

	public String jsonResponse;
	private RequestTask asyncTask = new RequestTask();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		asyncTask.delegate = this;
		
		String apiKey = "d8b8cf516b306681ac2f020882cf225e:4:70149274";
		String resourceType = "mostviewed";
		String timePeriod = "30"; //days

		String url = "http://api.nytimes.com/svc/mostpopular/v2/" + resourceType + 
				"/all-sections/" + timePeriod + "?api-key=" + apiKey;
		
		asyncTask.execute(url);
		
	}

	public void processFinish(String output) {
		this.jsonResponse = output;
		
		Intent i = new Intent(SplashScreen.this, ArticleList.class);
		i.putExtra("ArticlesJson", jsonResponse);
		startActivity(i);

		// close this activity
		finish();
	}
}
