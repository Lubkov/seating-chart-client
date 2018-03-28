package ua.stellar.seatingchart;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.domain.Total;
import ua.stellar.seatingchart.event.OnTaskCompleteListener;
import ua.stellar.seatingchart.task.LoadDataTask;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.seatingchart.utils.TotalAdapter;
import ua.stellar.ua.test.seatingchart.R;

public class TotalsFragment extends Fragment {

    private final String LOG_TAG = "RESERVE";

    //UI links
    private Activity activity = null;

    //вью
    private View view;

    private ListView totalList = null;
    private TotalAdapter totalAdapter = null;
    private TextView twAllCount = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_totals, container, false);
        activity = this.getActivity();
        totalList = (ListView) view.findViewById(R.id.lwTotal);
        twAllCount = (TextView) view.findViewById(R.id.twAllCount);

        return view;
    }

    public void loadTotals(Long layoutId) {
        //загрузка итогов
        String url = SysInfo.getInstance().getUrlAddress() + "/order/get-totals?layout_id=" + layoutId;
        LoadDataTask totalLoadTask = new LoadDataTask(this.getActivity(), false, url);
        totalLoadTask.setOnTaskComplete(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(JsonResponse response) {
                Log.d(LOG_TAG, "Загружены итоги: " + response.isSuccess());
                showTotals(response);
            }
        });
        totalLoadTask.execute();
    }

    private void showTotals(JsonResponse response) {

        if (response.isSuccess()) {
            List<Total> list = null;
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Total>>(){}.getType();
            String innerJson = gson.toJson(response.getResult());
            list = gson.fromJson(innerJson, listType);

            if (totalAdapter == null) {
                totalAdapter = new TotalAdapter(this.getContext(), list);
                totalList.setAdapter(totalAdapter);
            } else {
                for (Total totalNew : list) {
                    Total total = totalAdapter.getTotal(totalNew.getId());

                    if (total != null) {
                        total.setName(totalNew.getName());
                        total.setAmount(totalNew.getAmount());
                        total.setAmountAll(totalNew.getAmountAll());
                    }
                }

                totalAdapter.notifyDataSetChanged();
            }

            updateAllCount();
        }
    }

    private void updateAllCount() {
        Integer all = 0;
        for (Total totalNew : totalAdapter.getItems()) {
            all+= totalNew.getAmount();
        }
        twAllCount.setText("" + all);
    }


}
