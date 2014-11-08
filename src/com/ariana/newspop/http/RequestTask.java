package com.ariana.newspop.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class RequestTask extends AsyncTask<String, String, String> {

	public AsyncResponse delegate = null;
	private String jsonResponse;
	
	@Override
	protected String doInBackground(String... uri) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(uri[0]);
		HttpResponse response;
		try {
			response = httpClient.execute(request);
			
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){

				BufferedReader br = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));

				StringBuffer json = new StringBuffer();
				String line = "";

				while((line = br.readLine()) != null) {
					json.append(line);
				}

				jsonResponse = json.toString();

			} else {
				response.getEntity().getContent().close();
				throw new IOException(response.getStatusLine().getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonResponse;
	}
	
	protected void onPostExecute (String ArticlesJson) {
		delegate.processFinish(ArticlesJson);
	}

}
