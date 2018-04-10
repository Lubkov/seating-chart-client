package ua.stellar.seatingchart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ua.stellar.seatingchart.domain.Layout;
import ua.stellar.seatingchart.domain.LayoutComposition;
import ua.stellar.seatingchart.domain.Operation;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.domain.TheUser;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.event.OnDataUpdateListener;
import ua.stellar.seatingchart.event.OnLoginListener;
import ua.stellar.seatingchart.task.BackgroundUpdateTask;
import ua.stellar.seatingchart.task.LoadLayoutsTask;
import ua.stellar.seatingchart.task.OperationLoadTask;
import ua.stellar.seatingchart.tcp.TCPClient;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.seatingchart.utils.MapPageAdapter;
import ua.stellar.ua.test.seatingchart.R;

public class MainActivity extends FragmentActivity implements OnLoginListener, TotalsFragment.OnTotalsListener {

    private final String LOG_TAG = "RESERVE";

    //map list
    private List<Layout> layouts;

    private MapPageAdapter mapPageAdapter;

    private Long lastUpdateID = 0L;

    //UI links
    private RelativeLayout container;
    private ViewPager mapContainer;

    private TotalsFragment totals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "Main activity: OnCreate");
        if (layouts != null) {
            Log.d(LOG_TAG, "Есть информация о карте");
        }

        container = (RelativeLayout) findViewById(android.R.id.tabhost);
        mapContainer = (ViewPager) findViewById(R.id.mapContainer);
        mapContainer.setOffscreenPageLimit(10);

        if (SysInfo.getInstance().isEmpty()) {
            showSettings();
        }

        if (SysInfo.getInstance().isEmpty()) {
            Toast.makeText(this, "Необходимо настроить подключение к серверу", Toast.LENGTH_SHORT).show();
        } else {
            if ((SysInfo.getInstance().getUser() == null) ||
                    (SysInfo.getInstance().getUser().getId() == null)) {

                LoginUserDialog loginDialog = new LoginUserDialog();
                loginDialog.setOnLoginListener(this);
                loginDialog.show(this.getFragmentManager(), "login");
            } else {
                onLogin(SysInfo.getInstance().getUser());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Настройки подключения");
//        MenuItem item = menu.add(0, 1, 0, "Настройки");
//        item.setIntent(new Intent(this, SettingsActivity.class));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //показать настройки
        showSettings();

//        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    private void initMap() {
        if ((getCount() == 0) || (container == null)) {
            return;
        }
        //создание адаптера для фрагментов карты, создание фрагментов карты
        mapPageAdapter = new MapPageAdapter(getSupportFragmentManager());
        createMapFragments();
        mapContainer.setAdapter(mapPageAdapter);

        totals = new TotalsFragment();
        FragmentTransaction fragmentTrans = getSupportFragmentManager().beginTransaction();
        fragmentTrans.replace(R.id.paTotals, totals).commit();

        //загрузка итогов
        loadTotals();

        //создание слушателя на событие - "Автоматическое обноление данных"
        addTCPClientListener();

        mapContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mapPageAdapter.setPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void loadLayoutList() {
        LoadLayoutsTask task = new LoadLayoutsTask(this);

        try {
            layouts = task.execute().get();
            SysInfo.getInstance().setLayoutIdList(layouts);
        } catch (Exception e) {
            //TODO: добавить показ ошибки с возможность закрыть приложение, либо повторить загрузку данных
            e.printStackTrace();
        }
    }

    private void createMapFragments() {
        for (Layout layout: layouts) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("layout", layout);

            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(bundle);
            mapPageAdapter.addFragment(mapFragment, layout.getName());

            mapFragment.setOnResourceLongClickListener((ResourceItem item) ->
                    totals.setSearchText(item.getLayoutComposition().getGoodNumber().toString()));
            mapFragment.setOnClickListener((MapFragment fragment) -> totals.setSearchText(""));
            mapFragment.setOnDoubleClickListener((MapFragment fragment) -> showNextLayout());
        }
    }

    private void showSettings() {
        Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
        startActivity(settingsActivity);
    }

    @Override
    public void onLogin(TheUser user) {
        SysInfo.getInstance().setUser(user);
        loadLayoutList();
        initMap();
    }

    public void showError(final String error) {
        Log.e(LOG_TAG, error);
    }

    private void loadTotals() {
        totals.updateTotals(layouts);
    }

    private void initOperationList(final List<Operation> operations) {
        totals.initOperationList(operations);
    }

    private void addOperations(final List<Operation> operations) {
        totals.addOperations(operations);
    }

    private void showNextLayout() {
        int n = mapContainer.getCurrentItem() + 1;
        if (n >= mapPageAdapter.getCount()) {
            n = 0;
        }
        mapContainer.setCurrentItem(n);
    }

    //создание слушателя на событие - "Автоматическое обноление данных"
    private void addTCPClientListener() {
        ReserveApplication application = (ReserveApplication) getApplicationContext();
        TCPClient client = application.getTcpClient();

        if (client != null) {
            OnDataUpdateListener onDataUpdate = new OnDataUpdateListener() {
                @Override
                public void onDataUpdate(String tag) {

                    //загрузить обновление данных
                    loadChanges();
                }
            };
            client.setOnDataUpdateListener(onDataUpdate);
        }
    }

    @Override
    public void onTotalsCreate(final TotalsFragment fragment) {
        //изменилась активная карта
        mapPageAdapter.setOnChangeListener((final int position, final String title) -> totals.showCurrentMap(title));

        //показать название первой карты
        if (layouts.size() > 0) {
            totals.showCurrentMap(layouts.get(0).getName());
        }

        //создать вью и загрузить все операции
        loadAllOperation();
    }

    private void loadAllOperation() {
        OperationLoadTask task = new OperationLoadTask(this, SysInfo.getInstance().getLayoutIdList());
        task.setOnLoadingComplete(new NotifyEvent<JsonResponse>() {
            @Override
            public void onAction(JsonResponse response) {
                List<Operation> list = null;
                if (response.isSuccess()) {
                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<Operation>>(){}.getType();
                        String innerJson = gson.toJson(response.getResult());
                        list = gson.fromJson(innerJson, listType);
                        Log.d(LOG_TAG, "Загружено " + list.size() + " операций");
                        initOperationList(list);
                    } catch(Exception e) {
                        showError("Загрузка операций: " + e.getMessage());
                    }
                } else {
                    showError("Загрузка операций, success = false");
                }
            }
        });
        task.execute();
    }

    @Override
    public void loadChanges() {
        Log.d(LOG_TAG, "Обновление данных");
        String url = getLoadLayoutCompositionsUrl(SysInfo.getInstance().getLayoutIdList(), lastUpdateID); //layout.getId()

        //загрузить обновление данных
        BackgroundUpdateTask task = new BackgroundUpdateTask(url);

        task.setOnLoadingComplete(new NotifyEvent<List<LayoutComposition>>() {
            public void onAction(List<LayoutComposition> items) {
                if (items != null) {
                    Log.d(LOG_TAG, "Необходимо обновить " + items.size() + " ресурсов");

                    List<Operation> operations = new ArrayList<>();
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
                                operations.add(new Operation(item.getLastOper()));
                            }
                        } else {
                            Log.e(LOG_TAG, "Ресурс не найден на карте");
                        }

                        //обновить ID последней загруженной операции с сервера
                        setLastOperation(item.getLastOper());
                    }

                    addOperations(operations);
                } else {
                    Log.d(LOG_TAG, "Данные актуальны");
                }
            }
        });
        task.execute();

        //обновление итогов
        loadTotals();
    }

    private String getLoadLayoutCompositionsUrl(final String layoutID, final Long lastOperID) {
        return SysInfo.getInstance().getUrlAddress() +
                "/order/get-layout-compositions?" +
                "layout_id=" + layoutID + "&" +
                "last_oper_id=" + lastOperID;
    }

    @Override
    public void setLastOperation(final Operation operation) {
        if (operation.getId() > lastUpdateID) {
            lastUpdateID = operation.getId();
        }
    }

    private ResourceItem getResourceItem(final LayoutComposition search) {
        for (int i = 0; i < mapPageAdapter.getCount(); i++) {
            MapFragment layout = (MapFragment) mapPageAdapter.getItem(i);
            ResourceItem item = layout.getResourceItem(search);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    private long getCount() {
        if (layouts == null) {
            return 0;
        }
        return layouts.size();
    }
}
