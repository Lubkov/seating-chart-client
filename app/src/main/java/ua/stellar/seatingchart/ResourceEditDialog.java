package ua.stellar.seatingchart;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import ua.stellar.seatingchart.domain.Background;
import ua.stellar.seatingchart.domain.LockedGoods;
import ua.stellar.seatingchart.domain.OperationType;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.event.OnResourceChangeListener;
import ua.stellar.seatingchart.event.OnTaskCompleteListener;
import ua.stellar.seatingchart.task.LoadDataTask;
import ua.stellar.seatingchart.task.RemoveLockResourceTask;
import ua.stellar.seatingchart.utils.ApplicationService;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.ua.test.seatingchart.R;

public class ResourceEditDialog extends DialogFragment implements OnClickListener {

    private final String LOG_TAG = "RESERVE";

    private boolean result = false;
    private boolean readOnly = false;
    private ResourceItem resourceItem;

    //UI links
    private View view = null;
    private TextView tvGoodsName = null;
    private TextView tvStatus = null;
    private ImageView ivStatus = null;
    private TextView twResourceNum = null;
    private Button buttonCancel = null;
    private Button buttonYes = null;
    private Button buBack = null;
    private Button buFoward = null;
    private ProgressBar pbUpdateLocked;
    private TextView twReadOnly;
    private RadioGroup edState;

    private OnResourceChangeListener onBackResource = null;
    private OnResourceChangeListener onForwardResource = null;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Resource edit dialog: onCreateView");

        getDialog().setTitle("Редактирование состояния");
        getDialog().setCanceledOnTouchOutside(false);

        view = inflater.inflate(R.layout.resource_edit_dialog, null);

        Bundle bundle = getArguments();
        if (bundle != null) {
            setReadOnly(bundle.getBoolean("readOnly"));
        }

        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);
        buttonYes = (Button) view.findViewById(R.id.buttonYes);
        buttonCancel.setOnClickListener(this);
        buttonYes.setOnClickListener(this);

        twResourceNum = (TextView) view.findViewById(R.id.twResourceNum);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        tvGoodsName = (TextView) view.findViewById(R.id.tvGoodsName);
        ivStatus = (ImageView) view.findViewById(R.id.ivStatus);

        //предыдущий ресурс
        buBack = (Button) view.findViewById(R.id.buResourceBack);

        OnClickListener onBackClick =  new OnClickListener() {
            @Override
            public void onClick(View v) {
                doBackResource();
            }
        };
        buBack.setOnClickListener(onBackClick);

        //следующий ресурс
        buFoward = (Button) view.findViewById(R.id.buResourceFoward);
        OnClickListener onFowardClick =  new OnClickListener() {
            @Override
            public void onClick(View v) {
                doForwardResource();
            }
        };
        buFoward.setOnClickListener(onFowardClick);

        pbUpdateLocked = (ProgressBar) view.findViewById(R.id.pbUpdateLocked);
        twReadOnly = (TextView) view.findViewById(R.id.twReadOnly);

        edState = (RadioGroup) view.findViewById(R.id.edState);

        //создать список состояний
        for (OperationType item : SysInfo.getInstance().getOperationTypes()) {
            RadioButton button = new RadioButton(view.getContext());
            button.setId(item.getId().intValue());
            button.setText(item.getName());

            edState.addView(button);
        }

        edState.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                OperationType type = SysInfo.getInstance().getOperationType((long) checkedId);

                Log.d(LOG_TAG, "Changed state: " + type.getName());
                updateTitle(type);
            }
        });

        updateView(resourceItem);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        int width = getResources().getDimensionPixelSize(R.dimen.resource_edit_dialog_width);
        //int height = getResources().getDimensionPixelSize(R.dimen.resource_edit_dialog_height);
        getDialog().getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG, "Resource edit dialog: " + ((Button) v).getText());

        if (v.equals(buttonYes)) {
            Integer radioButtonID = edState.getCheckedRadioButtonId();
            OperationType operType = SysInfo.getInstance().getOperationType((long) radioButtonID);
            Long lastOperID = resourceItem.getLayoutComposition().getLastOper().getOperationType();

            //Состояние изменилось, необходимо сохранить
            if (!lastOperID.equals(radioButtonID.longValue())) {
                resourceItem.saveState(operType);
                result = true;
            } else {
                result = false;
            }
            dismiss();
        }

        if (v.equals(buttonCancel)) {
            dismiss();
        }
    }

    public void onDismiss(DialogInterface dialog) {
        //super.onDismiss(dialog);

        resourceItem.update();

        if (!result) {
            RemoveLockResourceTask task = new RemoveLockResourceTask(getActivity());
            try {
                Boolean res = task.execute().get();
                if (res) {
                    super.onDismiss(dialog);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        result = false;
    }

    public void updateView(final ResourceItem resourceItem) {
        Log.d(LOG_TAG, "Обновления состояния updateView " + resourceItem.getLayoutComposition().getGoodNumber());

        this.resourceItem = resourceItem;

        //запрет сохранять изменения, пока обновляется состояние
        setEnableToControls(false);
        //показ прогресса обновления состояние
        pbUpdateLocked.setVisibility(View.VISIBLE);

        //обновить возможность редактирования ресурса и блокировать для других устройств
        updateReadOnly();

        Long status = resourceItem.getLayoutComposition().getLastOper().getOperationType();
        edState.check(status.intValue());

        //обновить статус
        updateTitle(SysInfo.getInstance().getOperationType(status));
    }

    private void updateTitle(OperationType operationType) {

        String name = "";
        String status = "";
        String goodsNum = "999";
        Integer color = 0;

        if (resourceItem != null) {
            goodsNum = resourceItem.getLayoutComposition().getGoodNumber().toString();
            name = resourceItem.getLayoutComposition().getGoodsTypeName();
        }

        if (operationType != null) {
            status = operationType.getName();
            color = Background.getRGBColor(operationType.getColor());
        }

        if (tvGoodsName != null) {
            tvGoodsName.setText(name);
        }

        if (twResourceNum != null) {
            twResourceNum.setText(goodsNum);
        }

        if (tvStatus != null) {
            tvStatus.setText(status);
        }

        if (ivStatus != null) {
            ivStatus.setBackgroundResource(R.drawable.edit_text_style);
            GradientDrawable gd = (GradientDrawable) ivStatus.getBackground().getCurrent();
            gd.setColor(color);
            gd.setStroke(1, Color.BLACK);
        }
    }

    public ResourceItem getResourceItem() {
        return resourceItem;
    }

    public void setResourceItem(ResourceItem resourceItem) {
        this.resourceItem = resourceItem;
    }

    public void setOnBackResource(OnResourceChangeListener onBackResource) {
        this.onBackResource = onBackResource;
    }

    public void setOnForwardResource(OnResourceChangeListener onFowardResource) {
        this.onForwardResource = onFowardResource;
    }

    private void doBackResource() {
        if (onBackResource != null) {
            onBackResource.onChange(resourceItem);
        }
    }

    private void doForwardResource() {
        if (onForwardResource != null) {
            onForwardResource.onChange(resourceItem);
        }
    }

    private void setEnableToControls(boolean enable) {
        buttonYes.setEnabled(enable);
        buttonCancel.setEnabled(enable);
        buBack.setEnabled(enable);
        buFoward.setEnabled(enable);
    }

    private void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;

        buttonYes.setEnabled(!readOnly);
        buttonCancel.setEnabled(true);
        buBack.setEnabled(true);
        buFoward.setEnabled(true);

        pbUpdateLocked.setVisibility(View.INVISIBLE);
    }

    private void showErrorText(String text) {
        twReadOnly.setText(text);

        if (text.length() > 0) {
            twReadOnly.setVisibility(View.VISIBLE);
        } else {
            twReadOnly.setVisibility(View.INVISIBLE);
        }
    }

    private void updateReadOnly(){
        showErrorText("");

        String url = SysInfo.getInstance().getUrlAddress() +
                "/order/lock-layout-item?" +
                "layout_id=" + resourceItem.getLayoutComposition().getLayoutID() + "&" +
                "goods_id=" + resourceItem.getLayoutComposition().getGoodID() + "&" +
                "user_id=" + SysInfo.getInstance().getUser().getId() + "&" +
                "device_id=" + ApplicationService.getDeviceUniqueID(this.getActivity());

        LoadDataTask totalLoadTask = new LoadDataTask(this.getActivity(), false, url);
        totalLoadTask.setOnTaskComplete(new OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(JsonResponse response) {
                LockedGoods lock = null;

                if (response.isSuccess()) {
                    Gson gson = new Gson();
                    String responseData = gson.toJson(response.getResult());

                    lock = gson.fromJson(responseData, LockedGoods.class);
                } else {
                    showErrorText(response.getResult().toString());
                    setReadOnly(true);
                    return;
                }

                if (lock == null) {
                    showErrorText("Не получен ответ от сервера");
                    setReadOnly(true);
                    return;
                }

                //ресурс редактируется на другом устройстве
                if ((lock != null) &&
                        (!lock.getDeviceId().equals(SysInfo.getInstance().getDeviceId()))) {

                    showErrorText("Ресурс редактируется на другом устройстве");
                    setReadOnly(true);
                    return;
                }

                showErrorText("");
                setReadOnly(false);
            }
        });
        totalLoadTask.execute();

//
//        LockResourceTask task = new LockResourceTask(
//                this.getActivity(),
//                resourceItem.getLayoutComposition().getLayoutID(),
//                resourceItem.getLayoutComposition().getGoodID());
//
//        task.setOnLockedComplete(new NotifyEvent<LockedGoods>() {
//            @Override
//            public void onAction(LockedGoods lock) {
//                boolean readOnly = false;
//
//                //ресурс редактируется на другом устройстве
//                if ((lock != null) &&
//                        (!lock.getDeviceId().equals(SysInfo.getInstance().getDeviceId()))) {
//
//                    readOnly = true;
//                }
//
//                setReadOnly(readOnly);
//            }
//        });
//        task.execute();
    }
}
