package ua.stellar.seatingchart.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ua.stellar.seatingchart.domain.Background;
import ua.stellar.seatingchart.domain.Total;
import ua.stellar.ua.test.seatingchart.R;

public class TotalAdapter extends BaseAdapter {

    private final String LOG_TAG = "RESERVE";

    private Context context;
    private LayoutInflater lInflater;
    private List<Total> items;

    public TotalAdapter(Context context, List<Total> items) {
        this.context = context;
        this.items = items;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            view = lInflater.inflate(R.layout.total_item, parent, false);
        }

        Total total = items.get(position);
        if (total != null) {
            TextView twName = (TextView) view.findViewById(R.id.twTotalName);
            TextView twTotal = (TextView) view.findViewById(R.id.twTotalValue);
            ImageView imState = (ImageView) view.findViewById(R.id.imTotalState);

            twName.setText(total.getName());
            twTotal.setText(total.getAmount() + "/" + total.getAmountAll());
            imState.setBackgroundColor(Background.getRGBColor(total.getColor()));
        }

        return view;
    }

    public List<Total> getItems() {
        return items;
    }

    public Total getTotal(Long id) {
        for (Total total : items) {
            if (total.getId().equals(id)) {
                return total;
            }
        }
        return null;
    }
}
