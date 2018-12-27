package de.chartsexplorer.chartsexplorer;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ShowSongDetailsActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int playbackPosition = 0;
    private String audioPath;

    @Override
    public void onBackPressed() {
        mediaPlayer.release();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_details);

        Intent i = getIntent();
        String selectedYear = i.getStringExtra("year");
        String position = i.getStringExtra("song");

        try {
            JSONObject song = new JSONObject(i.getStringExtra("song"));
            setTitle(selectedYear + " - Position " + song.getString("pos"));

            TextView title = (TextView) this.findViewById(R.id.songTitle);
            title.setText(song.getString("title"));
            TextView interpret = (TextView) this.findViewById(R.id.songInterpret);
            interpret.setText(song.getString("interpret"));

            this.getImagesFromiTunes(song);

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        final ImageView playSong = (ImageView) findViewById(R.id.songPlay);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playSong.setImageResource(android.R.drawable.ic_media_play);
            }
        });

        playSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    playAudio();
                 /*   playLocalAudio();
                    playLocalAudio_UsingDescriptor();*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getImagesFromiTunes(final JSONObject song) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://itunes.apple.com/search?term=" + URLEncoder.encode(song.getString("interpret")) + "%20" + URLEncoder.encode(song.getString("title")) + "&limit=25");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    InputStreamReader input = new InputStreamReader(conn.getInputStream(), "UTF8");
                    BufferedReader reader = new BufferedReader(input, 2000);
                    StringBuffer sb = new StringBuffer();
                    String line = reader.readLine();
                    while (line != null) {
                        sb.append(line);
                        line = reader.readLine();
                    }

                    JSONObject response = new JSONObject(sb.toString());
                    JSONArray result = response.getJSONArray("results");

                    String coverImg = null;
                    String artistViewImage = null;
                    audioPath = null;
                    //loop over results
                    JSONObject jObject;
                    for (int i = 0; i < result.length(); i++) {
                        jObject = result.getJSONObject(i);
                        if (jObject.getString("artistName").equalsIgnoreCase(song.getString("interpret"))) {
                            coverImg = result.getJSONObject(i).getString("artworkUrl100");

                            Document doc = Jsoup.connect(result.getJSONObject(i).getString("artistViewUrl")).get();
                            artistViewImage = doc.select(".we-artist-header img").get(0).attr("src");
                            audioPath = result.getJSONObject(i).getString("previewUrl");
                            break;
                        }
                    }

                    if (coverImg == null) {
                        coverImg = result.getJSONObject(0).getString("artworkUrl100");
                        audioPath = result.getJSONObject(0).getString("previewUrl");
                    }

// show The Image in a ImageView
                    new DownloadImageTask((ImageView) findViewById(R.id.coverView))
                            .execute(coverImg);

                    new DownloadImageTask((ImageView) findViewById(R.id.artistView))
                            .execute(artistViewImage);

                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
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
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    private void playAudio() throws Exception {

        final ImageView playSong = (ImageView) findViewById(R.id.songPlay);

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            playSong.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            mediaPlayer.pause();
            playSong.setImageResource(android.R.drawable.ic_media_play);
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}
