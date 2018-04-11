package ua.stellar.seatingchart;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import ua.stellar.seatingchart.domain.TheUser;
import ua.stellar.seatingchart.event.NotifyEvent;
import ua.stellar.seatingchart.event.OnLoginListener;
import ua.stellar.seatingchart.task.LoginUserTask;
import ua.stellar.seatingchart.utils.ApplicationService;
import ua.stellar.seatingchart.utils.JsonResponse;
import ua.stellar.ua.test.seatingchart.R;

public class LoginUserDialog extends DialogFragment implements View.OnClickListener {

    private final String LOG_TAG = "RESERVE";

    //UI links
    Activity activity = null;
    private View view = null;
    private EditText edLogin = null;
    private EditText edPassword = null;
    private TextView twErrorMessage = null;
    private Button buLogin = null;

    private OnLoginListener onLoginListener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //this.setCancelable(false);
        activity = this.getActivity();

        getDialog().setTitle("Авторизация в системе");
        getDialog().setCanceledOnTouchOutside(false);

        view = inflater.inflate(R.layout.login_user_dialog, null);

        edLogin = (EditText) view.findViewById(R.id.edLogin);
        edPassword = (EditText) view.findViewById(R.id.edPassword);
        twErrorMessage = (TextView) view.findViewById(R.id.twErrorMessage);

        buLogin = (Button) view.findViewById(R.id.buLogin);
        buLogin.setOnClickListener(this);

        //востановить имя пользователя
        restoreLogin();

        if (!isEmptyLogin()) {
            edPassword.requestFocus();
        }

        Button buSettings = (Button) view.findViewById(R.id.buSettings);
        buSettings.setOnClickListener((View v)-> {
            Intent settingsActivity = new Intent(activity.getBaseContext(), SettingsActivity.class);
            startActivity(settingsActivity);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        int width = getResources().getDimensionPixelSize(R.dimen.login_dialog_width);
        //int height = getResources().getDimensionPixelSize(R.dimen.resource_edit_dialog_height);
        getDialog().getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        ApplicationService.closeApplication();
    }

    @Override
    public void onClick(View button) {

        if (button.equals(buLogin)) {

            showError("");

            if (isEmptyLogin()) {
                edLogin.setError("Необходимо ввести имя пользователя");
            } else {
                edLogin.setError(null);
            }

            if (isEmptyPassword()) {
                edPassword.setError("Необходимо ввести пароль");
            } else {
                edPassword.setError(null);
            }

            if (!isEmptyLogin() && !isEmptyPassword()) {

                LoginUserTask task = new LoginUserTask(activity,
                                                       edLogin.getText().toString(),
                                                       edPassword.getText().toString());

                task.setOnLoadingComplete(new NotifyEvent<JsonResponse>() {
                    @Override
                    public void onAction(JsonResponse response) {
                        saveLogin(response);
                    }
                });

                task.execute();
            }
        }

//        if (button.equals(buCancel)) {
//            android.os.Process.killProcess(android.os.Process.myPid());
//        }
    }

    private void restoreLogin() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String login = pref.getString("login", "");

        Log.d(LOG_TAG, "Restore login = " + login);

        edLogin.setText(login);
    }

    private void saveLogin(JsonResponse response) {

        TheUser user = null;

        if (response.isSuccess()) {
            try {
                user = new Gson().fromJson(response.getResult().toString(), TheUser.class);
            } catch(Exception e) {
                showError(e.getMessage());
                return;
            }
        } else {
            showError(response.getResult().toString());
            return;
        }

        if ((user == null) ||(user.getId() <= 0)) {
            showError("Ошибка авторизации");
            return;
        }

        String login = edLogin.getText().toString();
        Log.d(LOG_TAG, "Save login = " + login);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("login", login);
        edit.commit();

        dismiss();

        doLoginListener(user);
    }

    private boolean isEmptyLogin() {

        return (edLogin.getText().toString().length() <= 0);
    }

    private boolean isEmptyPassword() {

        return (edPassword.getText().toString().length() <= 0);
    }

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    private void doLoginListener(TheUser user) {
        if (onLoginListener != null) {
            onLoginListener.onLogin(user);
        }
    }

    private void showError(String message) {
        twErrorMessage.setText(message);
    }
}