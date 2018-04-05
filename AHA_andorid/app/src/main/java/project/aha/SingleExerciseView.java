package project.aha;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.InputStream;
import java.net.*;
import java.util.HashMap;

import project.aha.models.Exercise;

public class SingleExerciseView extends AppCompatActivity {

    private Exercise e;
    private TextView subjectView;
    private TextView dateView;
    private TextView specializedView;
    private TextView descriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_exercise_view);
        Constants.showLogo(this);

        Intent i = getIntent();

        e = (Exercise) i.getSerializableExtra("exercise");
        if (e != null) {

            subjectView = (TextView) findViewById(R.id.subject);
            subjectView.setText(e.getSubject());

            setTitle(e.getSubject());


            dateView = (TextView) findViewById(R.id.date);
            dateView.setText(e.getAdded_date());


            specializedView = (TextView) findViewById(R.id.specialized);
            specializedView.setText(e.getDiagnose());


            descriptionView = (TextView) findViewById(R.id.desc);
            descriptionView.setText(e.getDescription());


            if (e.getImage_path() != null) {
                SendHttpRequestTask img = new SendHttpRequestTask();
                img.execute("img", e.getImage_path());
            }

            if (e.getVideo_path() != null) {


                Button link_click = (Button) findViewById(R.id.link_click);
                link_click.setVisibility(View.VISIBLE);
                link_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(e.getVideo_path()));
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(e.getVideo_path()));
                        try {
                            try {
                                startActivity(appIntent);
                            } catch (ActivityNotFoundException ex) {
                                startActivity(webIntent);
                            }
                        } catch (ActivityNotFoundException ex) {
                            Toast.makeText(SingleExerciseView.this, getString(R.string.copied_path), Toast.LENGTH_LONG).show();
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("youttube path", e.getVideo_path());
                            clipboard.setPrimaryClip(clip);
                        }
                    }
                });
            }
        }

    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, Object> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ImageView imageView = (ImageView) findViewById(R.id.img);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.loading);
        }

        @Override
        protected Object doInBackground(String... params) {
            try {
                if (params[0].equalsIgnoreCase("img")) {
                    URL url = new URL(Constants.DATABASE_URL + params[1]);
//                    Log.d("download",url.toString());

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    return myBitmap;
                } else {

                }

            } catch (Exception e) {
                Log.d("download error", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result instanceof Bitmap) {
                ImageView imageView = (ImageView) findViewById(R.id.img);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap((Bitmap) result);
            }

//            else if(){
//
//            }

        }
    }

}


