package ua.stellar.seatingchart;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ua.stellar.seatingchart.domain.Layout;
import ua.stellar.seatingchart.domain.Operation;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.domain.Total;
import ua.stellar.seatingchart.task.LoadDataTask;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.seatingchart.utils.OperationAdapter;
import ua.stellar.seatingchart.utils.TotalAdapter;
import ua.stellar.ua.test.seatingchart.R;

public class TotalsFragment extends Fragment {

    private final String LOG_TAG = "RESERVE";

    //UI links
    private Activity activity;

    //вью
    private View view;

    private ListView totalList;
    private TotalAdapter totalAdapter;
    private TextView twAllCount;
    private ProgressBar pbOperationLoad;
    private ListView lwOperation;
    private SearchView swOperation;

    private OperationAdapter adapter;

    private OnTotalsListener totalsListener;

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
        pbOperationLoad = (ProgressBar) view.findViewById(R.id.pbOperationLoad);
        pbOperationLoad.setVisibility(View.VISIBLE);

        Button buUpdate = (Button) view.findViewById(R.id.buUpdate);
        buUpdate.setOnClickListener((View v) -> {
            Log.d(LOG_TAG, "Update data");
            totalsListener.loadChanges();
        });

        totalsListener.onTotalsCreate(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            totalsListener = (OnTotalsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTotalsListener");
        }
    }
    //обновить итоги
    public void updateTotals(List<Layout> layouts) {
        //загрузка итогов по видам ресурсов
        loadTotals();
    }

    public void addOperations(List<Operation> operations) {
        Log.d(LOG_TAG, "Добавление операций в адаптер");
        for (Operation operation : operations) {
            adapter.addItem(operation);
        }
    }

    public void setSearchText(final String text) {
        swOperation.setQuery(text, false);
        swOperation.setIconified(false);
        swOperation.clearFocus();
    }

    private void loadTotals() {
        //загрузка итогов
        String url = SysInfo.getInstance().getUrlAddress() + "/order/get-totals?layout_id=" + SysInfo.getInstance().getLayoutIdList();
        LoadDataTask totalLoadTask = new LoadDataTask(this.getActivity(), false, url);
        totalLoadTask.setOnTaskComplete((JsonResponse response) -> {
            Log.d(LOG_TAG, "Загружены итоги: " + response.isSuccess());
            showTotals(response);
        });
        totalLoadTask.execute();
    }

    private void showTotals(JsonResponse response) {
        if (response.isSuccess()) {
            List<Total> list;
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
            all += totalNew.getAmount();
        }
        twAllCount.setText("" + all);
    }

    public void initOperationList(List<Operation> operations) {
        //обновить ID последней загруженной операции с сервера
        for (Operation operation : operations) {
            totalsListener.setLastOperation(operation);
        }

        adapter = new OperationAdapter(this.getContext(), operations);

        // настраиваем список
        lwOperation = (ListView) view.findViewById(R.id.lwOperation);
        lwOperation.setAdapter(adapter);

        lwOperation.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            swOperation.setQuery(adapter.getOperation(position).getGoodsNumber().toString(), false);
            swOperation.setIconified(false);
            swOperation.clearFocus();
        });

        swOperation = (SearchView) view.findViewById(R.id.swOperation);
        lwOperation.setVisibility(View.VISIBLE);
        swOperation.setVisibility(View.VISIBLE);

        pbOperationLoad.setVisibility(View.INVISIBLE);

        //swOperation.onaddTextChangedListener(new TextWatcher() {

        swOperation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //lwOperation.setText(query);
                swOperation.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }

    public interface OnTotalsListener {
        void onTotalsCreate(final TotalsFragment fragment);
        void setLastOperation(final Operation operation);
        void loadChanges();
    }
}
