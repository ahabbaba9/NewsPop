package com.ariana.newspop;

import java.sql.Date;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class ArticleList extends ActionBarActivity {

	private String json;
	private ArrayList<Article> articles = new ArrayList<Article>();
	private JSONParser parser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_list);

		// Retrieve the articles JSON that was returned from the API call
		if(getIntent().getStringExtra("ArticlesJson") != null) {
			json = getIntent().getStringExtra("ArticlesJson");
			
		}
		
		getArticlesFromJson(json);

		ListView articlesList = (ListView) findViewById(android.R.id.list);

		ArrayAdapter<Article> adapter = new ArrayAdapter<Article>(this, android.R.layout.simple_list_item_1, articles);
		articlesList.setAdapter(adapter);

		articlesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				Article selectedArticle = (Article)parent.getItemAtPosition(position);
				goToWebPage(view, selectedArticle);
			}

		});
	}
	
	public void getArticlesFromJson(String json) {
		try {
			/* 
			 * Here we will parse the JSON string to retrieve 
			 * specific article data.
			 * Author, title, publish date, etc.
			 */

			// Create json object from json String
			JSONObject jsonObj = (JSONObject) parser.parse(json);

			// Create json array of results
			JSONArray resultSet = (JSONArray) jsonObj.get("results");

			for(int i = 0; i < 10; i++) {
				// Each result value in the json array
				JSONObject obj = (JSONObject) resultSet.get(i);
				// Create new Article object to store json data
				Article article = new Article();
				try {
					JSONArray media = (JSONArray) obj.get("media");

					// Web url
					article.setUrl(((String) obj.get("url")).replace("\\", "" ));
					// Category
					article.setCategory((String) obj.get("section"));
					// Author
					article.setAuthor((String) obj.get("byline"));
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
					
					article.setImageUrl(mediaUrl);

					// Add article to list
					articles.add(article);
				} catch (ClassCastException e) {
					// If media is empty, it will throw a ClassCastException.
					// We will ignore cases where media don't exist.
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void goToWebPage(View view, Article article) {
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
}
