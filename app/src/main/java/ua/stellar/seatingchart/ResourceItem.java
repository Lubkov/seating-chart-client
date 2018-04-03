package ua.stellar.seatingchart;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ua.stellar.seatingchart.domain.Background;
import ua.stellar.seatingchart.domain.GoodsType;
import ua.stellar.seatingchart.domain.LayoutComposition;
import ua.stellar.seatingchart.domain.Operation;
import ua.stellar.seatingchart.domain.OperationType;
import ua.stellar.seatingchart.domain.SysInfo;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.event.OnResourceStatusChangedListener;
import ua.stellar.seatingchart.task.SaveResourceTask;
import ua.stellar.ua.test.seatingchart.R;

public class ResourceItem {

    private static final int TITLE_POSITION_TOP = 1;
    private static final int TITLE_POSITION_BOTTOM = 2;
    private static final int TITLE_POSITION_LEFT = 3;
    private static final int TITLE_POSITION_RIGHT = 4;

    private static final int BAGROUND_ANGLE_0 = 0;
    private static final int BAGROUND_ANGLE_90 = 1;
    private static final int BAGROUND_ANGLE_180 = 2;
    private static final int BAGROUND_ANGLE_270 = 3;

    private final String LOG_TAG = "RESERVE";


    private LayoutComposition layoutComposition = null;
    private GoodsType goodsType = null;
    private int index = -1;

    private int height;
    private int width;
    private int left;
    private int top;

    //UI links
    private Activity activity;
    private Context context = null;
    private RelativeLayout container = null;
    private ImageView image = null;
    private TextView caption = null;
    private RelativeLayout layout = null;

    //events
    private View.OnClickListener onClickEvent = null;
    private View.OnLongClickListener onLongClickEvent = null;
    private OnClickListener onClick = null;
    private OnLongClickListener onLongClick = null;
    private OnResourceStatusChangedListener onStatusChanged = null;

    public ResourceItem(final Activity activity,
                        final Context context,
                        final RelativeLayout container,
                        final LayoutComposition layoutComposition,
                        final int index) {

        this.layoutComposition = layoutComposition;
        this.activity = activity;
        this.context = context;
        this.container = container;
        this.index = index;
        goodsType = SysInfo.getInstance().getGoodsType(layoutComposition.getGoodsTypeID());

        height = layoutComposition.getHeight();
        width = layoutComposition.getWidth();
        left = layoutComposition.getPositionX();
        top = layoutComposition.getPositionY();

        onClickEvent = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "On click event");

//                updateStyle(layoutComposition.getGoods().getGoodsType().getBackground().getColor(),
//                        layoutComposition.getGoods().getGoodsType().getBackground().getBorderSize(),
//                        Color.BLUE);

                doResourceClick();
            }
        };

        onLongClickEvent = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(LOG_TAG, "On long click resource");

                doLongClick();
                return true;
            }
        };

        layout = new RelativeLayout(context);
        layout.setLeft(layoutComposition.getPositionX());
        layout.setTop(layoutComposition.getPositionY());
        //layout.setOrientation(LinearLayout.VERTICAL);

        //Log.d(LOG_TAG, "Item position: " + layoutComposition.getPositionX() + "x" + layoutComposition.getPositionY());

        createImage();
        createTitle();

        //обновить визуальное состояние ресурса
        update();

        RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(width, height);
        lParams.setMargins(left, top, 0, 0);

        container.addView(layout, lParams);
    }


    private void createTitle() {
        caption = new TextView(context);
        caption.setText(layoutComposition.getGoodName());
        caption.setTypeface(Typeface.create(goodsType.getTitleFont().getName(), Typeface.NORMAL));
        caption.setTextSize(goodsType.getTitleFont().getSize());
        caption.setTextColor(Background.getRGBColor(goodsType.getTitleFont().getColor()));

        caption.setOnClickListener(onClickEvent);
        caption.setOnLongClickListener(onLongClickEvent);
        setTextHorizontalAlign(caption, layoutComposition.getTitleAlignment());
        caption.setGravity(Gravity.CENTER);


        //поворот надписи
//        if ((layoutComposition.getBackgroundAngle() == 1) ||
//                (layoutComposition.getBackgroundAngle() == 3)) {
//            RotateAnimation rotate = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate90);
//            caption.setAnimation(rotate);
//        }

        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.MATCH_PARENT); //ViewGroup.LayoutParams.WRAP_CONTENT

        layout.addView(caption, layoutParams);
    }

    private void createImage() {
        image = new ImageView(context);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height, conf);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#3d3d3d"));
        paint.setStrokeWidth(0.3f);

        int x = 0;
        int y = 0;
        int step = 8;

        while ((x < 2 * width) || (y < 2 * height)) {
            x+= step;
            y+= step;

            canvas.drawLine(x, 0, 0, y, paint);
        }

        x = 0;
        y = height;

        while ((x < 2 * width) || (y > -height)) {
            x+= step;
            y-= step;

            canvas.drawLine(0, y, x, height, paint);
        }

        image.setImageBitmap(bitmap);

        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT); //ViewGroup.LayoutParams.WRAP_CONTENT

        layout.addView(image, layoutParams);
    }

    private void updateStyle(final int color,
                             final int borderWidth,
                             final int borderColor) {

        //Log.d(LOG_TAG, "Update style, color: " + color + ", border color: " + borderColor + ", border width: " + borderWidth);

        layout.setBackgroundResource(R.drawable.edit_text_style);
        GradientDrawable gd = (GradientDrawable) layout.getBackground().getCurrent();
        gd.setColor(color);
        gd.setStroke(borderWidth, borderColor);
    }

    public void update() {
        Long id = layoutComposition.getLastOper().getOperationType();
        //Log.d(LOG_TAG, "Item status id #" + id);
        OperationType status = SysInfo.getInstance().getOperationType(id);
        //Log.d(LOG_TAG, "Item status name: " + status.getName());
        updateStyle(Background.getRGBColor(status.getColor()),
                    status.getBorderSize(),
                    Background.getRGBColor(status.getBorderColor()));

        if ((layoutComposition.getLastOper().getId() != null) &&
                (layoutComposition.getLastOper().getId() > 0)) {
            image.setVisibility(View.INVISIBLE);
        } else {
            image.setVisibility(View.VISIBLE);
        }
    }

    public void saveState(OperationType operationType) {

        SaveResourceTask task = new SaveResourceTask(
                activity,
                layoutComposition.getLayoutID(),
                layoutComposition.getGoodID(),
                operationType);

        task.setOnSaveComplete(new NotifyEvent<Operation>() {
            @Override
            public void onAction(Operation operation) {
                Log.d(LOG_TAG, "Сохранение операции...");
                if (operation != null) {
                    Log.d(LOG_TAG, "Сохранение операции: " + operation.getOperationType());
                    layoutComposition.setLastOper(operation);
                    update();
                    doStatusChanged();
                }
            }
        });

        task.execute();
    }

    private void setTextHorizontalAlign(TextView textView, final int align) {

        final int HORIZONTAL_ALIGN_LEFT = 0;
        final int HORIZONTAL_ALIGN_RIGHT = 1;
        final int HORIZONTAL_ALIGN_CENTER = 2;

        switch (align) {
            case HORIZONTAL_ALIGN_LEFT:
                textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                break;
            case HORIZONTAL_ALIGN_RIGHT:
                textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                break;
            case HORIZONTAL_ALIGN_CENTER:
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                break;
        }

        textView.setGravity(Gravity.CENTER_VERTICAL);
    }

    private int getBackgrounAngle(final int angle) {
        switch (angle) {
            case BAGROUND_ANGLE_90:
                return 90;
            case BAGROUND_ANGLE_180:
                return 180;
            case BAGROUND_ANGLE_270:
                return 270;
            default:
                return 0;
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        onClick = listener;
    }

    public void setOnStatusChanged(OnResourceStatusChangedListener onStatusChanged) {
        this.onStatusChanged = onStatusChanged;
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        this.onLongClick = listener;
    }

    public void doResourceClick() {
        if (onClick != null) {
            onClick.onClick(this);
        }
    }

    public void doLongClick() {
        if (onLongClick != null) {
            onLongClick.onLongClick(this);
        }
    }

    private void doStatusChanged() {
        if (onStatusChanged != null) {
            onStatusChanged.onStatusChanged(this);
        }
    }

    public LayoutComposition getLayoutComposition() {
        return layoutComposition;
    }

    public int getIndex() {
        return index;
    }


    public interface OnClickListener {
        void onClick(ResourceItem item);
    }

    public interface OnLongClickListener {

        void onLongClick(ResourceItem item);
    }
}
