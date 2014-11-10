package com.ariana.newspop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Created custom adapter to format list items.
 * Article image is set as the list background, with the relevant article information
 * overlayed: Author, category, title, and publish date
 */

public class ArticleAdapter extends ArrayAdapter<Article> {

	private final Context context;
	private final ArrayList<Article> articles;
	
	public ArticleAdapter(Context context, ArrayList<Article> arrayList) {
		super(context, R.layout.row, arrayList);
		this.context = context;
		this.articles = arrayList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.row, parent, false);
		
		ImageView articleImage = (ImageView) rowView.findViewById(R.id.articleImage);
		TextView author = (TextView) rowView.findViewById(R.id.author);
		TextView title = (TextView) rowView.findViewById(R.id.title);
		TextView publishDate = (TextView) rowView.findViewById(R.id.publishDate);
		TextView category = (TextView) rowView.findViewById(R.id.category);
		
		Article article = articles.get(position);
		
		articleImage.setImageDrawable(article.getImage());
		
		author.setText(article.getAuthor());
		
		title.setText(article.getTitle());
		
		category.setText(article.getCategory());
		
		// Reformatting the date to be human readable
		// Pattern ex: Aug 31, 2000
		Date date = article.getPublishDate();

		String pattern = "MMM d, yyyy";
		
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		
		String newDate = format.format(date);
		
		publishDate.setText(newDate);
		
		return rowView;
	}
}

 