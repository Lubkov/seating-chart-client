package ua.stellar.seatingchart.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

public class ApplicationService {

    //проверить подключение к интернету
    public static boolean hasConnection(final Context context) {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static String getDeviceUniqueID(Context context) {
        String number = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return number;
    }

    public static void closeApplication() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

//    private void storeImage(Bitmap image) {
//        File pictureFile = getOutputMediaFile();
//        if (pictureFile == null) {
//            return;
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(pictureFile);
//            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
//            fos.close();
//        } catch (Exception e) {
//
//        }
//    }

//    private File getOutputMediaFile(){
//        // To be safe, you should check that the SDCard is mounted
//        // using Environment.getExternalStorageState() before doing this.
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
//                + "/Android/data/"
//                + this.getContext().getPackageName()
//                + "/Files");
//
//        // This location works best if you want the created images to be shared
//        // between applications and persist after your app has been uninstalled.
//
//        // Create the storage directory if it does not exist
//        if (! mediaStorageDir.exists()){
//            if (! mediaStorageDir.mkdirs()){
//                return null;
//            }
//        }
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
//        File mediaFile;
//        String mImageName="MI_"+ timeStamp +".jpg";
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
//
//        return mediaFile;
//    }

//    private Drawable getDrawable(String name) throws Resources.NotFoundException {
//        Log.d(LOG_TAG, "Get Drawable = " + name);
//
//        Resources resources = context.getResources();
//        int id = resources.getIdentifier(name,
//                "drawable",
//                context.getPackageName());
//
//        Log.d(LOG_TAG, layout.getBackground() + " = " + id);
//        Log.d(LOG_TAG, context.getPackageName());
//        return context.getDrawable(id);
//
//        //return ContextCompat.getDrawable(getActivity(), resourceId);
//    }
//
//
//    private int getDrawableID(String name) throws Resources.NotFoundException {
//        Resources resources = context.getResources();
//        int id = resources.getIdentifier(name,
//                "drawable",
//                context.getPackageName());
//        return id;
//    }

    private void createImage() {
//        image = new ImageView(context);
//
//        Matrix matrix = new Matrix();
//        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(),
//                getDrawableID("background_" + layoutComposition.getGoods().getGoodsType().getBackground().getId()));
//        matrix.postRotate(getBackgrounAngle(layoutComposition.getBackgroundAngle())); //===>angle to be rotated
//        Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
//        image.setImageBitmap(bMapRotate);
//
//        //image.setImageDrawable(getDrawable("background_" + layoutComposition.getGoods().getGoodsType().getBackground().getId()));
//        image.setAdjustViewBounds(true);
//        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        layout.addView(image, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
    }

}
