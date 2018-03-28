package ua.stellar.seatingchart;

import android.app.Activity;
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
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ua.stellar.seatingchart.domain.Layout;
import ua.stellar.seatingchart.domain.LayoutComposition;
import ua.stellar.seatingchart.domain.Operation;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.event.OnDataUpdateListener;
import ua.stellar.seatingchart.event.OnResourceChangeListener;
import ua.stellar.seatingchart.event.OnResourceClickListener;
import ua.stellar.seatingchart.event.OnResourceLongClickListener;
import ua.stellar.seatingchart.task.BackgroundUpdateTask;
import ua.stellar.seatingchart.task.LoadLayoutCompositionTask;
import ua.stellar.seatingchart.task.LoadPictureTask;
import ua.stellar.seatingchart.task.OperationLoadTask;
import ua.stellar.seatingchart.tcp.TCPClient;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.seatingchart.utils.OperationAdapter;
import ua.stellar.ua.test.seatingchart.R;

public class MapFragment extends Fragment implements View.OnClickListener {

    private final String LOG_TAG = "RESERVE";

    //данные карты
    private Layout layout = null;

    //данные ресурсов на карте
    private List<LayoutComposition> items = null;
    private List<ResourceItem> resourceItems = null;

    private Long lastUpdateID = 0L;

    //UI links
    private Activity activity = null;

    //вью
    private View view;

    //контейнер
    private RelativeLayout container = null;

    //высота карты(контейнера)
    private int mapHeight;

    //высота карты(контейнера)
    private int mapWidth;

    //фоновое изображение карты
    private ImageView mapBackground = null;

    private ProgressBar loadProgressBar;
    private ProgressBar pbOperationLoad;
    private TextView labelStatus;
    private ResourceEditDialog editDialog = null;
    private SearchView swOperation = null;
    private ListView lwOperation = null;
    private OperationAdapter adapter = null;

//    private TotalsFragment totals;

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
        pbOperationLoad = (ProgressBar) view.findViewById(R.id.pbOperationLoad);
        labelStatus = (TextView) view.findViewById(R.id.twLayoutSize);

        Button buUpdate = (Button) view.findViewById(R.id.buUpdate);
        buUpdate.setOnClickListener(this);

        mapBackground = new ImageView(this.getContext());

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

        // TODO: убраны итоги
//        totals = new TotalsFragment();
//        FragmentTransaction fragmentTrans = this.getActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTrans.replace(R.id.paTotals, totals).commit();



//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        int height = displaymetrics.heightPixels;
//        int width = displaymetrics.widthPixels;
//
//        ImageView image = (ImageView) view.findViewById(R.id.imageView1);
//        //TextView tv = (TextView)findViewById(txtViewsid);
//        Matrix mat = new Matrix();
//        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.couch);
//        mat.postRotate(90); //===>angle to be rotated
//        Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mat, true);
//        image.setImageBitmap(bMapRotate);

        //TextView text = (TextView) view.findViewById(R.id.myImageViewText);
        //text.setText("rotated text here");

        //RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate90);
        //text.setAnimation(rotate);

        //создание слушателя на событие - "Автоматическое обноление данных"
        addTCPClientListener();

        return view;
    }

    private void showLayoutSize() {
        labelStatus.setText(mapWidth + "x" + mapHeight);
    }

    private void loadMapData() {
        Log.d(LOG_TAG, "Загрузка состава карты");
        //загрузка данных о ресурсах карты - запрос на сервер
        try {
            String url = getLoadLayoutCompositionsUrl(layout.getId(), lastUpdateID);

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

        pbOperationLoad.setVisibility(View.VISIBLE);
        OperationLoadTask task = new OperationLoadTask(this.getActivity(), layout.getId());
        task.setOnLoadingComplete(new NotifyEvent<JsonResponse>() {
            @Override
            public void onAction(JsonResponse response) {

                loadOperation(response);
                pbOperationLoad.setVisibility(View.INVISIBLE);
            }
        });
        task.execute();

        //загрузка итогов
        loadTotals();
    }

    private void loadOperation(JsonResponse response) {
        List<Operation> list = null;

        if (response.isSuccess()) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Operation>>(){}.getType();
                String innerJson = gson.toJson(response.getResult());
                list = gson.fromJson(innerJson, listType);
                Log.d(LOG_TAG, "Загружено " + list.size() + " операций");
            } catch(Exception e) {
                Log.e(LOG_TAG, "Загрузка операций: " + e.getMessage());
                return;
            }
        } else {
            Log.e(LOG_TAG, "Загрузка операций, success = false");
            return;
        }

        adapter = new OperationAdapter(this.getContext(), list);

        // настраиваем список
        lwOperation = (ListView) view.findViewById(R.id.lwOperation);
        lwOperation.setAdapter(adapter);

        lwOperation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                swOperation.setQuery(adapter.getOperation(position).getGoodsNumber().toString(), false);
                swOperation.setIconified(false);
                swOperation.clearFocus();
            }
        });

        swOperation = (SearchView) view.findViewById(R.id.swOperation);
        lwOperation.setVisibility(View.VISIBLE);
        swOperation.setVisibility(View.VISIBLE);

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
                    resizeBackground(sender);
                }

                //загрузить данные карты
                loadMapData();
                loadProgressBar.setVisibility(View.INVISIBLE);
            }
        });
        task.execute();
    }

    private void resizeBackground(Bitmap source) {
        int originalWidth = layout.getBackground().getWidth();
        int originalHeight = layout.getBackground().getHeight();

        Log.d(LOG_TAG, "UpdateView. Background size, width: " + originalWidth + ", height = " + originalHeight);

        if ((originalWidth <= 0) || (originalHeight <= 0)) {
            Log.e(LOG_TAG, "Некорректные размеры карты. Ширина, или высота меньше, либо равно нулю");
        }

        int cropWidth = originalWidth;
        int cropHeight = originalHeight;

        if (originalWidth > mapWidth) {
            cropWidth = mapWidth;
        }

        if (originalHeight > mapHeight) {
            cropHeight = mapHeight;
        }

        Log.d(LOG_TAG, "Crop image size, width: " + cropWidth + ", height = " + cropHeight);

        RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(cropWidth, cropHeight);
        lParams.setMargins(0, 0, 0, 0);
        container.addView(mapBackground, 0, lParams);

        try {
            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            Bitmap targetBitmap = Bitmap.createBitmap(originalWidth, originalHeight, Bitmap.Config.ARGB_8888);
            RectF rectf = new RectF(0, 0, cropWidth, cropHeight);

            Canvas canvas = new Canvas(targetBitmap);
            Path path = new Path();

            path.addRect(rectf, Path.Direction.CW);
            canvas.clipPath(path);

            canvas.drawBitmap(source, new Rect(0, 0, source.getWidth(), source.getHeight()),
                    new Rect(0, 0, originalWidth, originalHeight), paint);

            Matrix matrix = new Matrix();
            matrix.postScale(1f, 1f);
            Bitmap resizedBitmap = Bitmap.createBitmap(targetBitmap, 0, 0, cropWidth, cropHeight, matrix, true);

            /*convert Bitmap to resource */
            BitmapDrawable bd = new BitmapDrawable(resizedBitmap);

            mapBackground.setBackgroundDrawable(bd);
        } catch(Exception e) {
            Log.e(LOG_TAG, "Create background error: " + e.getMessage());
        }

        mapBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swOperation.setQuery("", false);
                swOperation.setIconified(false);
                swOperation.clearFocus();
            }
        });

        showLayoutSize();
    }

    private int getDrawable(String name) throws Resources.NotFoundException {
        Resources resources = this.getContext().getResources();

        int res = resources.getIdentifier(name,
                "drawable",
                this.getContext().getPackageName());
        return res;
    }

    private void createItems(List<LayoutComposition> items) {
        this.items = items;

        if ((items == null) || (items.size() == 0)) {
            return;
        }

        resourceItems = new ArrayList<ResourceItem>();

        for (int i = 0; i < items.size(); i++) {
            LayoutComposition item = items.get(i);
            ResourceItem resourceItem = new ResourceItem(activity, getContext(), container, item, i);
            resourceItems.add(resourceItem);

            //обновить ID последней загруженной операции с сервера
            checklastUpdateID(item.getLastOper().getId());

            resourceItem.setOnResourceClickListener(new OnResourceClickListener() {
                public void onClick(ResourceItem item) {
                    Log.d(LOG_TAG, "Resource item click, event in fragment: " + item.getLayoutComposition().getGoodName());

                    if ((editDialog == null) || (!editDialog.isAdded())) {
                        ResourceEditDialog dialog = createResourceEditDialog();

                        dialog.setResourceItem(item);
                        dialog.show(getActivity().getFragmentManager(), "ResourceEditDialog");
                    }
                }
            });

            resourceItem.setOnResourceLongClick(new OnResourceLongClickListener() {
                @Override
                public void onLongClick(ResourceItem item) {

                    swOperation.setQuery(item.getLayoutComposition().getGoodNumber().toString(), false);
                    swOperation.setIconified(false);
                    swOperation.clearFocus();
                }
            });

//            resourceItem.setOnStatusChanged(new OnResourceStatusChangedListener() {
//                public void onStatusChanged(ResourceItem item) {
//                    showTotals();
//                }
//            });
        }

//        showTotals();
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

    private ResourceItem getResourceItem(LayoutComposition search) {

        for (ResourceItem item : resourceItems) {
            if (item.getLayoutComposition().getId().equals(search.getId())) {
                return item;
            }
        }

        return null;
    }

    private void checklastUpdateID(Long operID) {
        if (operID > lastUpdateID) {
            lastUpdateID = operID;
        }
    }

    private void loadTotals() {

        //TODO: Итоги
//        totals.loadTotals(layout.getId());
    }

    private ResourceEditDialog createResourceEditDialog() {
        editDialog = new ResourceEditDialog();

        editDialog.setOnBackResource(new OnResourceChangeListener() {
            public void onChange(ResourceItem item) {
                int index = getBackResourceIndex(item.getIndex());
                ResourceItem backItem = resourceItems.get(index);
                editDialog.updateView(backItem);
            }
        });

        editDialog.setOnForwardResource(new OnResourceChangeListener() {
            public void onChange(ResourceItem item) {
                int index = getForwardResourceIndex(item.getIndex());
                ResourceItem nextItem = resourceItems.get(index);
                editDialog.updateView(nextItem);
            }
        });

        return editDialog;
    }

    @Override
    public void onClick(View v) {
        //Обновление состояния ресурсов карты
        loadUpdateData();
    }

    public void loadUpdateData() {
        String url = getLoadLayoutCompositionsUrl(layout.getId(), lastUpdateID);

        //загрузить обновление данных
        BackgroundUpdateTask task = new BackgroundUpdateTask(url, view);

        task.setOnLoadingComplete(new NotifyEvent<List<LayoutComposition>>() {
            public void onAction(List<LayoutComposition> items) {
                if (items != null) {
                    Log.d(LOG_TAG, "Необходимо обновить " + items.size() + " ресурсов");

                    for (LayoutComposition item : items) {
                        //поиск ресурса на карте
                        ResourceItem resourceItem = getResourceItem(item);

                        //нашли ресурс на карте
                        if (resourceItem != null) {
                            Log.d(LOG_TAG, "Old resource state: " + resourceItem.getLayoutComposition().getLastOper().getOperationType());
                            Log.d(LOG_TAG, "New resource state: " + item.getLastOper().getOperationType());

                            //заменяем последнюю операцию
                            resourceItem.getLayoutComposition().setLastOper(item.getLastOper());

                            //обновляем визуальное состояние
                            resourceItem.update();

                            if ((item.getLastOper().getId() != null) && (item.getLastOper().getId() > 0)) {
                                //добавить операцию в список
                                adapter.addItem(new Operation(item.getLastOper()));
                                lwOperation.smoothScrollToPosition(adapter.getItems().size());
                            }
                        }

                        //обновить ID последней загруженной операции с сервера
                        checklastUpdateID(item.getLastOper().getId());
                    }
                } else {
                    Log.d(LOG_TAG, "Данные актуальны");
                }
            }
        });

        task.execute();

        //загрузка итогов
        loadTotals();
    }

    private String getLoadLayoutCompositionsUrl(Long layoutID, Long lastOperID) {
        return SysInfo.getInstance().getUrlAddress() +
                "/order/get-layout-compositions?" +
                "layout_id=" + layoutID + "&" +
                "last_oper_id=" + lastOperID;
    }

    //создание слушателя на событие - "Автоматическое обноление данных"
    private void addTCPClientListener() {
        ReserveApplication application = (ReserveApplication) getActivity().getApplicationContext();
        TCPClient client = application.getTcpClient();

        if (client != null) {
            OnDataUpdateListener onDataUpdate = new OnDataUpdateListener() {
                @Override
                public void onDataUpdate(String tag) {

                    //загрузить обновление данных
                    loadUpdateData();
                }
            };
            client.setOnDataUpdateListener(onDataUpdate);
        }
    }
}
