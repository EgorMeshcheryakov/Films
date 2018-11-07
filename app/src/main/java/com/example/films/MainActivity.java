package com.example.films;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.example.films.Film;

public class MainActivity extends AppCompatActivity {

    public static String LOG_TAG = "my_log";
    
    public class Result {
        public List<Film> films;

        public Result() {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ParseTask().execute();
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("https://s3-eu-west-1.amazonaws.com/sequeniatesttask/films.json");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    //Log.d(LOG_TAG,"line: " + line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(LOG_TAG,"Response string: " + resultJson);
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            Log.d(LOG_TAG, strJson);

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Result result = gson.fromJson(strJson, Result.class);

            for (int i = 0; i < result.films.size(); i++) {
                Log.d(LOG_TAG, "films_list: " + result.films.get(i).localized_name);
            }

            ListView listView = (ListView)findViewById(R.id.listView);
            result.films = sortAndAddSections(result.films);
            ArrayAdapter<Film> arrayAdapter = new MyAdapter(MainActivity.this, result);
            //ArrayAdapter<String> arrayAdapter  =  new  ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, tmp_films);

            listView.setAdapter(arrayAdapter);

            AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Film selectedFilm = (Film)parent.getItemAtPosition(position);
                    if(!selectedFilm.isSectionHeader) {
                        Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                        intent.putExtra("localized_name", selectedFilm.localized_name);
                        intent.putExtra("image_url", selectedFilm.image_url);
                        intent.putExtra("name", selectedFilm.name);
                        intent.putExtra("year", "год: " + String.valueOf(selectedFilm.year));
                        intent.putExtra("rating", "рейтинг: " + String.valueOf(selectedFilm.rating));
                        intent.putExtra("description", selectedFilm.description);
                        startActivity(intent);
                    }
                }
            };
            listView.setOnItemClickListener(itemListener);
        }
    }

    private List sortAndAddSections(List<Film> filmList)
    {

        List<Film> tempList = new ArrayList<>();
        Collections.sort(filmList,new Comparator<Film>() {
            public int compare(Film o1, Film o2) {
                return String.valueOf(o1.year).compareTo(String.valueOf(o2.year));
            }
        });

        String header = "";
        for(int i = 0; i < filmList.size(); i++)
        {
            if(!(header.equals(String.valueOf(filmList.get(i).year)))) {
                Film sectionCell = new Film(null, null, filmList.get(i).year, 0, null, null);
                sectionCell.isSectionHeader = true;
                tempList.add(sectionCell);
                header = String.valueOf(filmList.get(i).year);
            }
            tempList.add(filmList.get(i));
        }

        return tempList;
    }

    private class MyAdapter extends ArrayAdapter<Film> {

        private LayoutInflater inflater;
        private Result result;

        public MyAdapter(Context context, Result res) {
            super(context, R.layout.rowlayout, res.films);
            this.result = res;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            Film film = result.films.get(position);


            if(film.isSectionHeader)
            {
                view = inflater.inflate(R.layout.rowheader, null);

                view.setClickable(false);

                TextView header = (TextView) view.findViewById(R.id.section_header);
                header.setText(String.valueOf(film.year));
            }
            else
            {
                view = inflater.inflate(R.layout.rowlayout, parent, false);

                TextView nameView = (TextView) view.findViewById(R.id.name);
                TextView localnameView = (TextView) view.findViewById(R.id.local_name);
                TextView rateView = (TextView) view.findViewById(R.id.rate);

                localnameView.setText(film.localized_name);
                nameView.setText(film.name);
                rateView.setText(String.valueOf(film.rating));
            }

            return view;
        }
    }
}
