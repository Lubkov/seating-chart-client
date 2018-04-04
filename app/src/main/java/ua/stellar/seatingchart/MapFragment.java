package ua.stellar.seatingchart;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import ua.stellar.seatingchart.domain.Layout;
import ua.stellar.seatingchart.domain.LayoutComposition;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.event.OnResourceChangeListener;
import ua.stellar.seatingchart.service.MapService;
import ua.stellar.seatingchart.task.LoadLayoutCompositionTask;
import ua.stellar.seatingchart.task.LoadPictureTask;
import ua.stellar.ua.test.seatingchart.R;

public class MapFragment extends Fragment {

    private final String LOG_TAG = "RESERVE";

    //данные карты
    private Layout layout = null;

    //данные ресурсов на карте
    private List<LayoutComposition> items = null;
    private List<ResourceItem> resourceItems = null;

    //UI links
    private Activity activity = null;

    //вью
    private View view;

    private MapView mapView;

    //контейнер
    private RelativeLayout container = null;

    //высота карты(контейнера)
    private int mapHeight;

    //высота карты(контейнера)
    private int mapWidth;

    private ProgressBar loadProgressBar;
    private ResourceEditDialog editDialog = null;

    //events
    private OnClickListener onClickListener;
    private OnDoubleClickListener onDoubleClickListener;
    private ResourceItem.OnLongClickListener onResourceLongClick;

    public MapService mapService;

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        activity = this.getActivity();
        this.container = (RelativeLayout) view.findViewById(R.id.mainContainer);
        loadProgressBar = (ProgressBar) view.findViewById(R.id.loadProgressBar);
//        mapBackground = new ImageView(this.getContext());
        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.setOnClickListener((MapView map) -> doOnClickListener());
        mapView.setOnDoubleClickListener((MapView map) -> doOnDoubleClickListener());

        Bundle bundle = getArguments();
        if (bundle != null) {
            layout = bundle.getParcelable("layout");
            Log.d(LOG_TAG, layout.getId() + " " + layout.getName());
        } else {
            Log.e(LOG_TAG, "Данные о карте не получены");
        }

        ViewTreeObserver observer = view.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                //получение размеров карты
                mapHeight = view.getHeight();
                mapWidth = view.getWidth();

                //получены данные о карте
                if (layout != null) {
                    //формирование карты - размеры, фоновое изображение
                    loadBackground();
                } else {
                    Log.e(LOG_TAG, "Данные карты не закгружены, layout = null");
                    //собообщить пользователю о ошибке
                }
            }
        });

        return view;
    }

    private void showLayoutSize() {
//        labelStatus.setText(mapWidth + "x" + mapHeight);
    }

    private void loadMapData() {
        Log.d(LOG_TAG, "Загрузка состава карты");
        //загрузка данных о ресурсах карты - запрос на сервер
        try {
            String url = getLoadLayoutCompositionsUrl(layout.getId().toString(), 0L);

            LoadLayoutCompositionTask task = new LoadLayoutCompositionTask(this.getActivity(), url);
            task.setOnLoadingComplete(new NotifyEvent<List<LayoutComposition>>() {
                public void onAction(List<LayoutComposition> items) {
                    createItems(items);
                }
            });

            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBackground() {
        Log.d(LOG_TAG, "updateView. Map size, width: " + mapWidth + ", height = " + mapHeight);

        String backgroundUrl = SysInfo.getInstance().getUrlAddress() + "/resources/images/" + layout.getBackground().getNumber() + ".png";
        Log.d(LOG_TAG, "backgroundUrl: " + backgroundUrl);

        LoadPictureTask task = new LoadPictureTask(backgroundUrl);
        task.setOnLoadComplete(new NotifyEvent<Bitmap>() {
            @Override
            public void onAction(Bitmap sender) {
                if (sender != null) {
                    //вставить изображение не маштабируя
//                    resizeBackground(sender);
                }
                mapView.setBackground(sender);

                //загрузить данные карты
                loadMapData();
                loadProgressBar.setVisibility(View.INVISIBLE);
            }
        });
        task.execute();
    }

    private void createItems(List<LayoutComposition> items) {
        this.items = items;

        if ((items == null) || (items.size() == 0)) {
            return;
        }

        resourceItems = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            LayoutComposition item = items.get(i);
            ResourceItem resourceItem = new ResourceItem(activity, getContext(), container, item, i);
            resourceItems.add(resourceItem);

            resourceItem.setOnClickListener((ResourceItem resource) -> {
                Log.d(LOG_TAG, "Resource item click, event in fragment: " + resource.getLayoutComposition().getGoodName());

                if ((editDialog == null) || (!editDialog.isAdded())) {
                    ResourceEditDialog dialog = createResourceEditDialog();

                    dialog.setResourceItem(resource);
                    dialog.show(getActivity().getFragmentManager(), "ResourceEditDialog");
                }
            });

            resourceItem.setOnLongClickListener((ResourceItem resource) -> {
                doResourceLongClick(resource);
            });
        }
    }

    private int getBackResourceIndex(final int index) {
        int res = index - 1;

        if (res < 0) {
            res = resourceItems.size() - 1;
        }

        return res;
    }

    private int getForwardResourceIndex(final int index) {
        int res = index + 1;

        if (res >= resourceItems.size()) {
            res = 0;
        }

        return res;
    }

    public ResourceItem getResourceItem(LayoutComposition search) {

        if (!search.getLayoutID().equals(layout.getId())) {
            return null;
        }

        for (ResourceItem item : resourceItems) {
            if (item.getLayoutComposition().getId().equals(search.getId())) {
                return item;
            }
        }

        return null;
    }

    private ResourceEditDialog createResourceEditDialog() {
        editDialog = new ResourceEditDialog();

        editDialog.setOnBackResource((ResourceItem item) -> {
            int index = getBackResourceIndex(item.getIndex());
            ResourceItem backItem = resourceItems.get(index);
            editDialog.updateView(backItem);
        });

        editDialog.setOnForwardResource((ResourceItem item) -> {
            int index = getForwardResourceIndex(item.getIndex());
            ResourceItem nextItem = resourceItems.get(index);
            editDialog.updateView(nextItem);
        });

        return editDialog;
    }

    private String getLoadLayoutCompositionsUrl(final String layoutID, final Long lastOperID) {
        return SysInfo.getInstance().getUrlAddress() +
                "/order/get-layout-compositions?" +
                "layout_id=" + layoutID + "&" +
                "last_oper_id=" + lastOperID;
    }

    public void setOnResourceLongClickListener(ResourceItem.OnLongClickListener listener) {
        this.onResourceLongClick = listener;
    }

    private void doResourceLongClick(final ResourceItem item) {
        if (onResourceLongClick != null) {
            onResourceLongClick.onLongClick(item);
        }
    }

    public void setOnClickListener(final OnClickListener listener) {
        this.onClickListener = listener;
    }

    private void doOnClickListener() {
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    public void setOnDoubleClickListener(final OnDoubleClickListener listener) {
        this.onDoubleClickListener = listener;
    }

    private void doOnDoubleClickListener() {
        if (onDoubleClickListener != null) {
            onDoubleClickListener.onDoubleClick(this);
        }
    }


    public interface OnClickListener {

        void onClick(final MapFragment mapFragment);
    }

    public interface OnDoubleClickListener {

        void onDoubleClick(final MapFragment mapFragment);
    }


}
