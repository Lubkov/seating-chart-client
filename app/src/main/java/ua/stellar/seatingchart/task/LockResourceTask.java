package ua.stellar.seatingchart.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import ua.stellar.seatingchart.domain.LockedGoods;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.utils.ApplicationService;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.seatingchart.utils.JsonUtils;

public class LockResourceTask extends AsyncTask<Void, Void, LockedGoods> {

    private final String LOG_TAG = "RESERVE";

    private Activity activity;
    //private ProgressDialog pDialog;
    private Long layoutId;
    private Long goodId;
    private NotifyEvent onLockedComplete;

    public LockResourceTask(final Activity activity,
                            final Long layoutId,
                            final Long goodId) {
        this.activity = activity;
        this.layoutId = layoutId;
        this.goodId = goodId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

//        pDialog = new ProgressDialog(activity);
//        pDialog.setCanceledOnTouchOutside(false);
//        pDialog.setMessage("Заргузка данных...");
//        pDialog.show();
    }

    @Override
    protected LockedGoods doInBackground(Void... params) {

        JsonResponse response = null;

        try {
            response = JsonUtils.getJsonResponse(getUrl());

            if (response.isSuccess()) {
                Gson gson = new Gson();
                String responseData = gson.toJson(response.getResult());

                return gson.fromJson(responseData, LockedGoods.class);
            } else {
                Log.d(LOG_TAG, "response = false");
                return null;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error loader: " + e.getMessage());
            hide();
        }
        return null;
    }

    @Override
    protected void onPostExecute(LockedGoods result) {
        super.onPostExecute(result);

        doLockedComplete(result);
        hide();
    }

    private void hide() {
//        if (null != pDialog && pDialog.isShowing()) {
//            pDialog.dismiss();
//        }
    }

    private String getUrl(){
        return SysInfo.getInstance().getUrlAddress() +
                "/order/lock-layout-item?" +
                "layout_id=" + layoutId + "&" +
                "goods_id=" + goodId + "&" +
                "user_id=" + SysInfo.getInstance().getUser().getId() + "&" +
                "device_id=" + ApplicationService.getDeviceUniqueID(activity);
    }

    public void setOnLockedComplete(NotifyEvent onLockedComplete) {
        this.onLockedComplete = onLockedComplete;
    }

    private void doLockedComplete(LockedGoods result) {
        if (onLockedComplete != null) {
            onLockedComplete.onAction(result);
        }
    }
}
