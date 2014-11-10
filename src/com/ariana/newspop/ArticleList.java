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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ArticleList extends ListActivity {

	private String json;

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
		// When article is tapped, it will open a new browser
		// which will redirect to the article page

		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
		startActivity(browserIntent);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.article_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
						JSONObject image = (JSONObject) images.get(0);
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
}
