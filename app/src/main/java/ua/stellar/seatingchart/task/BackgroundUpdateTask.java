package ua.stellar.seatingchart.task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import ua.stellar.seatingchart.domain.LayoutComposition;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.utils.JsonUtils;
import ua.stellar.ua.test.seatingchart.R;

public class BackgroundUpdateTask extends AsyncTask<Void, Void, List<LayoutComposition>> {

    private final String LOG_TAG = "RESERVE";

    private String url;
    private View view;
    private ProgressBar progressDialog;
    private NotifyEvent onLoadingComplete;

    public BackgroundUpdateTask(String url, View view) {
        this.url = url;
        this.view = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog = (ProgressBar) view.findViewById(R.id.pbBackgroundUpdate);

        Runnable showProgressUpdate = new Runnable() {
            public void run() {
                progressDialog.setVisibility(View.VISIBLE);
            }
        };

        progressDialog.post(showProgressUpdate);
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
        progressDialog.setVisibility(View.INVISIBLE);
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
