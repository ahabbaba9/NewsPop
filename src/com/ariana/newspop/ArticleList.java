package com.ariana.newspop;

import java.io.InputStream;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ariana.newspop.http.AsyncResponse;
import com.ariana.newspop.http.RequestTask;

public class ArticleList extends ListActivity implements AsyncResponse {

	private String json;
	private Menu myMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_list);

		// Retrieve the articles JSON that was returned from the API call
		if(getIntent().getStringExtra("ArticlesJson") != null) {
			json = getIntent().getStringExtra("ArticlesJson");

		}

		ListView articlesList = (ListView) findViewById(android.R.id.list);

		// Create List adapter to display articles in list form
		ArticleAdapter adapter = new ArticleAdapter(this, new ArrayList<Article>());

		// Here, we set the list adapter for the asynchronous task
		setListAdapter(adapter);

		articlesList.setAdapter(adapter);

		articlesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				Article selectedArticle = (Article)parent.getItemAtPosition(position);
				goToWebPage(view, selectedArticle);
			}

		});

		// Retrieve articles asynchronously & update ListView
		new ArticleRetrievalTask().execute(new String[] {json});

	}

	public void goToWebPage(View view, Article article) {
		// When article is tapped, it will open a new WebView
		// to display the article page

		Intent i = new Intent(ArticleList.this, WebViewActivity.class);
		i.putExtra("ArticleUrl", article.getUrl());
		startActivity(i);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// This adds the refresh button to the action bar menu
		getMenuInflater().inflate(R.menu.article_list, menu);
		myMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// When we select the refresh button, we want to caoll the NYT API
		// & reload the list of articles
		
		int id = item.getItemId();
		
		if (id == R.id.action_refresh) {
			setRefreshActionButtonState(true);
			RequestTask asyncTask = new RequestTask();
			asyncTask.delegate = this;
			
			String apiKey = "d8b8cf516b306681ac2f020882cf225e:4:70149274";
			String resourceType = "mostviewed";
			String timePeriod = "30"; //days

			String url = "http://api.nytimes.com/svc/mostpopular/v2/" + resourceType + 
					"/all-sections/" + timePeriod + "?api-key=" + apiKey;
			
			asyncTask.execute(url);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setRefreshActionButtonState(final boolean refreshing) {
		// Here we will animate the progress bar to indicate that a refresh is occurring
	    if (myMenu != null) {
	        final MenuItem refreshItem = myMenu
	            .findItem(R.id.action_refresh);
	        if (refreshItem != null) {
	            if (refreshing) {
	                refreshItem.setActionView(R.layout.progress_cirle);
	            } else {
	                refreshItem.setActionView(null);
	            }
	        }
	    }
	}

	/*********************************************************
	 * Asynchronous task that handles updating the ListView with 
	 * relevant article information
	 *********************************************************/

	public class ArticleRetrievalTask extends AsyncTask<String, Article, Void> {

		// Parser to parse the JSON String
		private JSONParser parser = new JSONParser();

		protected Void doInBackground(String... json) {
			try {
				/* 
				 * Here we will parse the JSON string to retrieve 
				 * specific article data.
				 * Author, title, publish date, category, article URL and article image
				 */

				// Create JSON object from JSON String
				JSONObject jsonObj = (JSONObject) parser.parse(json[0]);

				// Array of results from Articles JSON
				JSONArray resultSet = (JSONArray) jsonObj.get("results");

				for(int i = 0; i < 10; i++) {

					// Grab the first 10 results from the result array
					JSONObject obj = (JSONObject) resultSet.get(i);

					// Create new Article object to store JSON data
					Article article = new Article();
					try {
						JSONArray media = (JSONArray) obj.get("media");

						// Web URL
						article.setUrl(((String) obj.get("url")).replace("\\", "" ));
						// Category
						article.setCategory((String) obj.get("section"));

						// Author
						String byline = (String) obj.get("byline");
						byline = byline.toLowerCase(Locale.ENGLISH);
						int beginning = byline.indexOf("by ");
						String author = null;
						if(beginning != -1) {
							author = byline.substring(beginning + 3);
							author = author.toUpperCase(Locale.ENGLISH);
						}

						article.setAuthor(author);
						
						// Title
						article.setTitle((String) obj.get("title"));

						// Transform published date String to Date format
						String date = (String) obj.get("published_date");
						article.setPublishDate(Date.valueOf(date));

						JSONObject mediaMeta = (JSONObject) media.get(0);
						JSONArray images = (JSONArray) mediaMeta.get("media-metadata");

						// Article image
						JSONObject image = (JSONObject) images.get(7);
						String mediaUrl = (String) image.get("url");

						// Retrieve image from URL
						Drawable webImage = loadImageFromWeb(mediaUrl);
						article.setImage(webImage);

						publishProgress(article);
					} catch (ClassCastException e) {
						// If media is empty, it will throw a ClassCastException.
						// We will ignore cases where media don't exist.
					}
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected Drawable loadImageFromWeb(String url) {
			// Retrieve images asynchronously from the image URL
			try {
				InputStream is = (InputStream) new URL(url).getContent();
				Drawable d = Drawable.createFromStream(is, "article image");
				return d;
			} catch (Exception e) {
				return null;
			}
		}

		protected void onProgressUpdate(Article... items) {
			((ArticleAdapter)getListAdapter()).add(items[0]);
			((ArticleAdapter)getListAdapter()).notifyDataSetChanged();
		}

	}

	@Override
	public void processFinish(String output) {
		// Retrieve articles asynchronously & update ListView
		new ArticleRetrievalTask().execute(new String[] {json});
		Intent intent = getIntent();
		finish();
	    startActivity(intent);
	    overridePendingTransition(0,0);
	}
}
