package ua.stellar.seatingchart.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import ua.stellar.seatingchart.domain.LayoutComposition;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.utils.JsonUtils;

public class BackgroundUpdateTask extends AsyncTask<Void, Void, List<LayoutComposition>> {

    private final String LOG_TAG = "RESERVE";

    private String url;

    private NotifyEvent onLoadingComplete;

    public BackgroundUpdateTask(String url) {
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<LayoutComposition> doInBackground(Void... params) {
        try {
            return JsonUtils.sendRequest(url, LayoutComposition.class);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error loader: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<LayoutComposition> result) {
        super.onPostExecute(result);

        doLoadingComplete(result);
//        progressDialog.setVisibility(View.INVISIBLE);
    }

    public void setOnLoadingComplete(NotifyEvent onLoadingComplete) {
        this.onLoadingComplete = onLoadingComplete;
    }

    private void doLoadingComplete(List<LayoutComposition> items) {
        if (onLoadingComplete != null) {
            onLoadingComplete.onAction(items);
        }
    }
}
