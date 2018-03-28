package ua.stellar.seatingchart.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ua.stellar.seatingchart.event.NotifyEvent;

public class LoadPictureTask extends AsyncTask<Void, Void, Bitmap> {

    private NotifyEvent onLoadComplete = null;
    private String pictureUrl;

    public LoadPictureTask(String url) {
        this.pictureUrl = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL url = new URL(pictureUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        doLoadComplete(result);
    }

    public void setOnLoadComplete(NotifyEvent onLoadComplete) {
        this.onLoadComplete = onLoadComplete;
    }

    private void doLoadComplete(Bitmap result) {
        if (onLoadComplete != null) {
            onLoadComplete.onAction(result);
        }
    }
}
