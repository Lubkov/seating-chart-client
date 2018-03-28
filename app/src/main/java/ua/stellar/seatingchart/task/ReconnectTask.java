package ua.stellar.seatingchart.task;

import android.os.AsyncTask;

import ua.stellar.seatingchart.event.NotifyEvent;

public class ReconnectTask extends AsyncTask<Void, Void, Void> {

    private NotifyEvent onReconnect = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        doReconnect();
    }

    public void setOnReconnect(NotifyEvent onReconnect) {
        this.onReconnect = onReconnect;
    }

    private void doReconnect() {
        if (onReconnect != null) {
            onReconnect.onAction(null);
        }
    }
}
