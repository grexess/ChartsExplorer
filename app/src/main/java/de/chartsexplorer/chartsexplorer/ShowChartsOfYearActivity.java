package de.chartsexplorer.chartsexplorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ShowChartsOfYearActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top100_list);

        Intent i = getIntent();
        String selectedYear = i.getStringExtra("year");

        setTitle("Top100 " + selectedYear);

        try {
            JSONArray yearData = new JSONArray(i.getStringExtra("yearData"));

            setTitle("Top " + yearData.length() +  " " + selectedYear);

            final ArrayList<Song> songList = new ArrayList<Song>();
            for (int j = 0; j < yearData.length(); ++j) {

                Song song= new Song();
                song.setInterpret(yearData.getJSONObject(j).getString("interpret"));
                song.setPosition(yearData.getJSONObject(j).getString("pos"));
                song.setTitle(yearData.getJSONObject(j).getString("title"));
                songList.add(song);
            }

            ListView songsOfYearList = (ListView) findViewById(R.id.top100List);
             songsOfYearList.setAdapter(new CustomSongAdapter(this, songList));

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), "selectedYear: " + selectedYear, Toast.LENGTH_SHORT).show();
    }
}
