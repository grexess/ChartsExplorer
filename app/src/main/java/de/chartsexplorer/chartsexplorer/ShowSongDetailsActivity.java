package de.chartsexplorer.chartsexplorer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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

import de.chartsexplorer.chartsexplorer.util.FontAwsomeBitmapMaker;
import de.chartsexplorer.chartsexplorer.util.ImagePoJo;
import de.chartsexplorer.chartsexplorer.util.SongImageHelper;

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
            setTitle(selectedYear + " - " + getResources().getString(R.string.song_position) + " " + song.getString("pos"));

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
                playSong.setImageBitmap(FontAwsomeBitmapMaker.getBitmap("\uf04b", "#008577", getApplicationContext().getAssets()));
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

    private void playAudio() throws Exception {

        final ImageView playSong = (ImageView) findViewById(R.id.songPlay);

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            playSong.setImageBitmap(FontAwsomeBitmapMaker.getBitmap("\uf04c", "#F9DB22", getApplicationContext().getAssets()));
        } else {
            mediaPlayer.pause();
            playSong.setImageBitmap(FontAwsomeBitmapMaker.getBitmap("\uf04b", "#008577", getApplicationContext().getAssets()));
        }


    }

    private void getImagesFromiTunes(final JSONObject song) throws JSONException {

        SongImageHelper helper = new SongImageHelper();
        helper.setURL("https://itunes.apple.com/search?term=" + URLEncoder.encode(song.getString("interpret")) + "%20" + URLEncoder.encode(song.getString("title")) + "&limit=25");
        helper.setInterpret(song.getString("interpret"));

        //get song details from iTunes
        new DownloadImageTask(findViewById(R.id.detailsView).getRootView()).execute(helper);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    private class DownloadImageTask extends AsyncTask<SongImageHelper, Void, ImagePoJo> {

        View view;
        ProgressDialog pDialog = new ProgressDialog(ShowSongDetailsActivity.this);

        public DownloadImageTask(View mView) {
            view = mView;
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowSongDetailsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_song_details));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected ImagePoJo doInBackground(SongImageHelper... helpers) {

            ImagePoJo pojo = new ImagePoJo();

            SongImageHelper helper = helpers[0];

            try {

                URL iTunesURL = new URL(helper.getURL());
                HttpURLConnection conn = (HttpURLConnection) iTunesURL.openConnection();

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
                String artistmg = null;
                audioPath = null;
                //loop over results
                JSONObject jObject;
                for (int i = 0; i < result.length(); i++) {
                    jObject = result.getJSONObject(i);
                    if (jObject.getString("artistName").equalsIgnoreCase(helper.getInterpret())) {
                        coverImg = result.getJSONObject(i).getString("artworkUrl100");

                        Document doc = Jsoup.connect(result.getJSONObject(i).getString("artistViewUrl")).get();
                        try {
                            artistmg = doc.select(".we-artist-header img").get(0).attr("src");
                        } catch (Exception e) {
                            artistmg = "";
                        }
                        audioPath = result.getJSONObject(i).getString("previewUrl");
                        break;
                    }
                }

                if (coverImg == null) {
                    coverImg = result.getJSONObject(0).getString("artworkUrl100");
                    audioPath = result.getJSONObject(0).getString("previewUrl");
                }

                pojo.setPreviewURl(audioPath);
                pojo.setArtistImage(this.getImageAsBitmap(artistmg));
                pojo.setCoverImage(this.getImageAsBitmap(coverImg));


            } catch (Exception ex) {
                Log.e("E100", ex.getMessage());
            }
            return pojo;
        }


        private Bitmap getImageAsBitmap(String url) {
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return bitmap;
        }

        protected void onPostExecute(ImagePoJo result) {

            //set the images
            ImageView artist = (ImageView) findViewById(R.id.artistView);
            if (result.getArtistImage() != null) {
                artist.setImageBitmap(result.getArtistImage());
            } else {
                artist.setImageBitmap(FontAwsomeBitmapMaker.getBitmap("", "#FFFFFF", getApplicationContext().getAssets()));
            }
            ImageView cover = (ImageView) findViewById(R.id.coverView);
            if (result.getArtistImage() != null) {
                cover.setImageBitmap(result.getCoverImage());
            } else {
                cover.setImageBitmap(FontAwsomeBitmapMaker.getBitmap("\uf05e", "#D81B60", getApplicationContext().getAssets()));
            }
            //set the play Icon
            ImageView preview = (ImageView) findViewById(R.id.songPlay);
            if (result.getPreviewURl() != null) {
                preview.setImageBitmap(FontAwsomeBitmapMaker.getBitmap("\uf04b", "#008577", getApplicationContext().getAssets()));
            } else {
                preview.setImageBitmap(FontAwsomeBitmapMaker.getBitmap("\uf0e7", "#D81B60", getApplicationContext().getAssets()));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.nothing_found), Toast.LENGTH_SHORT).show();
            }

            //remove the progress dialog
            pDialog.dismiss();
        }
    }
}
