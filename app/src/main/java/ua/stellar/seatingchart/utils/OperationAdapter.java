package ua.stellar.seatingchart.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ua.stellar.seatingchart.domain.Background;
import ua.stellar.seatingchart.domain.Operation;
import ua.stellar.seatingchart.domain.OperationType;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.ua.test.seatingchart.R;

public class OperationAdapter extends BaseAdapter implements Filterable {

    private final String LOG_TAG = "RESERVE";

    private Context context;
    private LayoutInflater lInflater;
    private List<Operation> items;
    private ArrayList<Operation> allItems;
    private String filterText = "";

    public OperationAdapter(Context context, List<Operation> items) {
        this.context = context;
        this.items = items;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.allItems = new ArrayList<>();
        this.allItems.addAll(items);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.operation_item, parent, false);
        }

        Operation operation = getOperation(position);
        OperationType operationType = SysInfo.getInstance().getOperationType(operation.getOperationType());
        Integer color = Background.getRGBColor(operationType.getColor());

        TextView twDate = (TextView) view.findViewById(R.id.twDate);
        TextView twName = (TextView) view.findViewById(R.id.twName);
        TextView twUserName = (TextView) view.findViewById(R.id.twUserName);

        twDate.setText(DateUtils.utilDateToShortTimeStr(operation.getCreateDate()));
        twName.setText(operation.getGoodsNumber() + " " + operation.getGoodsTypeName());
        twUserName.setText(operation.getUserName());
        twDate.setBackgroundColor(color);

        return view;
    }

    public Operation getOperation(int position) {
        return ((Operation) getItem(position));
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    // Filter Class
    public void filter(String charText) {
        filterText = charText.toLowerCase(Locale.getDefault());
        Log.d(LOG_TAG, "Поисковый текст: \"" + filterText + "\"");

        items.clear();
        if (charText.length() == 0) {
            items.addAll(allItems);
        }
        else  {
            for (Operation item : allItems) {
                if (isFilter(item)) {
                    items.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<Operation> getItems() {
        return items;
    }

    public void addItem(Operation item) {

        if (allItems.indexOf(item) >= 0) {
            return;
        }

        allItems.add(item);

        //есть удобвлетворяет фильтра
        if (isFilter(item)) {
            items.add(item);
        }

        notifyDataSetChanged();
    }

    private boolean isFilter(Operation item) {
        return (filterText.equals("")) || filterText.equals(item.getGoodsNumber().toString());
    }

}
