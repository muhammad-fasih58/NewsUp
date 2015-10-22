package com.example.newsup;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyAdapter extends ArrayAdapter<MyNews> {
	Context c;
	int layoutFile;
	ArrayList<MyNews> data;

	public MyAdapter(Context context, int resource, ArrayList<MyNews> objects) {

		super(context, resource, objects);
		c = context;
		layoutFile = resource;
		data = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row;
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) c).getLayoutInflater();
			row = inflater.inflate(R.layout.row_layout, parent, false);
		} else {
			row = (View) convertView;
		}

		TextView titleText = (TextView) row.findViewById(R.id.titleText);
		TextView sourceName = (TextView) row.findViewById(R.id.sourceText);
		final String source = data.get(position).getSource();
		final String url = data.get(position).getLinkToNews();
		TextView date = (TextView) row.findViewById(R.id.date);
		TextView description = (TextView) row
				.findViewById(R.id.descriptionText);
		final ImageView newsImage = (ImageView) row.findViewById(R.id.image);
		ImageView sourceImage = (ImageView) row.findViewById(R.id.sourceImage);
		final RelativeLayout rl = (RelativeLayout) row
				.findViewById(R.id.LinearLayout1);
		final int pos = position;
		ImageView shareButton = (ImageView) row
				.findViewById(R.id.shareButton);

		titleText.setText(data.get(position).getTitle());

		sourceName.setVisibility(sourceName.GONE);

		date.setTextColor(Color.GRAY);
		date.setText(data.get(position).getDate());

		description.setText(data.get(position).getDetails());

		Thread thread = new Thread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				try {

					URL url11 = new URL(data.get(pos).getNewsImage());
					Log.e("ImageLink: ", data.get(pos).getNewsImage());
					HttpGet httpRequest = null;
					httpRequest = new HttpGet(url11.toURI());
					HttpClient httpclient = new DefaultHttpClient();
					HttpResponse response = (HttpResponse) httpclient

					.execute(httpRequest);

					HttpEntity entity = response.getEntity();
					BufferedHttpEntity b_entity = new BufferedHttpEntity(entity);
					InputStream input = b_entity.getContent();
					Bitmap bitmap = BitmapFactory.decodeStream(input);
					newsImage.setImageBitmap(bitmap);
					if(bitmap == null)
						newsImage.setVisibility(View.GONE);
				} catch (Exception ex) {

				}
			}
		});
		thread.start();
		
		if (source == "CNN")
			sourceImage.setImageResource(R.drawable.cnn);
		else if (source == "ABC")
			sourceImage.setImageResource(R.drawable.abc);
		else if(source == "" || source == null)
		{
			sourceImage.setVisibility(View.GONE);
		}
		rl.setVisibility(View.GONE);

		android.view.View.OnClickListener listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(c, "News Clicked on position " + pos,
						Toast.LENGTH_SHORT).show();
				// ll.setVisibility(0);

				Intent intent = new Intent(c, NewsView.class);
				intent.putExtra("url", url);
				c.startActivity(intent);
			}
		};

		OnLongClickListener longlistener = new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				rl.setVisibility(View.VISIBLE);
				return true;
			}
		};
		titleText.setOnClickListener(listener);
		date.setOnClickListener(listener);
		description.setOnClickListener(listener);
		sourceImage.setOnClickListener(listener);
		newsImage.setOnClickListener(listener);
		titleText.setOnLongClickListener(longlistener);
		date.setOnLongClickListener(longlistener);
		description.setOnLongClickListener(longlistener);
		newsImage.setOnLongClickListener(longlistener);

		shareButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(c, "Share Button Clicked " + pos,
						Toast.LENGTH_SHORT).show();
				// ll.setVisibility(0);
			}
		});

		ImageView starButton = (ImageView) row
				.findViewById(R.id.starButton);
		starButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatabaseHandler news = new DatabaseHandler(c);
				news.addNews(new MyNews(data.get(pos).getTitle(), data.get(pos)
						.getDetails(), data.get(pos).getSource(), "", data.get(
						pos).getNewsImage(), data.get(pos).getTypeOfNews(),
						data.get(pos).getDate(), data.get(pos).getLinkToNews()));

				Toast.makeText(c,
						"News at position " + pos + " saved locally.",
						Toast.LENGTH_SHORT).show();
				// ll.setVisibility(0);
			}
		});
		return row;
	}
}
