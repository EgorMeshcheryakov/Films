package com.example.films;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        String localized_name = getIntent().getExtras().getString("localized_name");
        String image_url = getIntent().getExtras().getString("image_url");
        String name = getIntent().getExtras().getString("name");
        String year = getIntent().getExtras().getString("year");
        String rating = getIntent().getExtras().getString("rating");
        String description = getIntent().getExtras().getString("description");

        TextView local_nameTextView = (TextView)findViewById(R.id.local_name);
        local_nameTextView.setText(localized_name);

        TextView nameTextView = (TextView)findViewById(R.id.name);
        nameTextView.setText(name);

        TextView yearTextView = (TextView)findViewById(R.id.year);
        yearTextView.setText(year);

        TextView ratingTextView = (TextView)findViewById(R.id.rate);
        ratingTextView.setText(rating);

        TextView descriptionTextView = (TextView)findViewById(R.id.description);
        descriptionTextView.setText(description);

        new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(image_url);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL("http://www.apis.dp.ua/static/img/noimage.jpg").openStream();
                in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error", e.getMessage());
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void onButtonClick(View view)
    {
        this.finish();
    }
}
