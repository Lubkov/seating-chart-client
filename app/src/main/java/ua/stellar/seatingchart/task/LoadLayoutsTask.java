package ua.stellar.seatingchart.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import ua.stellar.seatingchart.domain.GoodsType;
import ua.stellar.seatingchart.domain.Layout;
import ua.stellar.seatingchart.domain.OperationType;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.utils.JsonUtils;

public class LoadLayoutsTask extends AsyncTask<Void, Void, List<Layout>> {

    private final String LOG_TAG = "RESERVE";
    private final String LAYOUT_LIST_URL = "/order/get-active-layout-list";
    private final String GOODS_TYPE_URL = "/order/get-goods-type-list";
    private final String OPERATION_TYPE_URL = "/order/get-operation-types";

    private Activity activity;
    private ProgressDialog pDialog;
    private String res = "";
    private NotifyEvent onLoadingComplete;

    public LoadLayoutsTask(final Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(activity);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setMessage("Загрузка данных карты...");
        pDialog.show();
    }

    @Override
    protected List<Layout> doInBackground(Void... params) {
        List<Layout> layouts = null;

        try {
            Log.v(LOG_TAG, "Загрузка списка активных карт");
            String urlString = SysInfo.getInstance().getUrlAddress() + LAYOUT_LIST_URL;
            layouts = JsonUtils.sendRequest(urlString, Layout.class);

            if (layouts != null) {
                Log.v(LOG_TAG, "Загружено " + layouts.size() + " активных карт");
            }

            Log.v(LOG_TAG, "Загрузка видов ресурса");
            urlString = SysInfo.getInstance().getUrlAddress() + GOODS_TYPE_URL;
            SysInfo.getInstance().setGoodsTypes(JsonUtils.sendRequest(urlString, GoodsType.class));

            if (SysInfo.getInstance().getGoodsTypes() != null) {
                Log.v(LOG_TAG, "Загружено " + SysInfo.getInstance().getGoodsTypes().size() + " видов ресурса");
            }

            Log.v(LOG_TAG, "Загрузка состояний ресурсов");
            urlString = SysInfo.getInstance().getUrlAddress() + OPERATION_TYPE_URL;
            SysInfo.getInstance().setOperationTypes(JsonUtils.sendRequest(urlString, OperationType.class));

            if (SysInfo.getInstance().getOperationTypes() != null) {
                Log.v(LOG_TAG, "Загружено " + SysInfo.getInstance().getOperationTypes().size() + " состояний ресурса");
            }

            return layouts;
        } catch (Exception e) {
            hide();
            Log.e(LOG_TAG, "Error loader: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Layout> result) {
        super.onPostExecute(result);

        hide();
        doLoadingComplete(result);
    }

    private void hide() {
        if (null != pDialog && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public void setOnLoadingComplete(NotifyEvent onLoadingComplete) {
        this.onLoadingComplete = onLoadingComplete;
    }

    private void doLoadingComplete(List<Layout> layouts) {
        if (onLoadingComplete != null) {
            onLoadingComplete.onAction(layouts);
        }
    }
}