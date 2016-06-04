package info.androidhive.slidingmenu;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dyploma.garik.dyploma.R;

import java.util.concurrent.ExecutionException;

import info.androidhive.slidingmenu.Tasks.RegOrDelMobileTask;
import info.androidhive.slidingmenu.Utils.ServiceUtils;

public class AuthorizationFragment extends Fragment {

    private static final String TAG = "AuthorizationFragment";

    public AuthorizationFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_authorization, container, false);

        Button registerAndLoginButton = (Button) rootView.findViewById(R.id.register_and_login_button);
        registerAndLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((TextView) rootView.findViewById(R.id.loginEditText)).getText().toString();
                String password = ((TextView) rootView.findViewById(R.id.password_editText)).getText().toString();
                String imei  = UserPreferences.getInstance().getImei();
                try {
                    String regMobileResponse = new RegOrDelMobileTask(getActivity().getResources().getString(R.string.reg_mobile),
                            username, password, imei, new Callback<String>() {
                        @Override
                        public void call(String input) {

                        }
                    }).execute().get();
                    if (regMobileResponse.startsWith("1")) {
                        Log.d(TAG, "Регистрация мобильного устройства прошла успешно");
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.supersaved_mobile_password), regMobileResponse.substring(2));
                        editor.commit();
                        // если регистрация устройства прошла успешно, то проходим авторизацию
                        ServiceUtils.logon(getActivity().getResources().getString(R.string.logon),
                                sharedPref.getString(getActivity().getResources().getString(R.string.supersaved_mobile_password), "defaultPwd"),
                                new Callback<String>() {
                                    @Override
                                    public void call(String input) {

                                    }
                                });
                    } else {
                        Log.e(TAG, "Регистрация мобильного устройства не завершена, код ошибки: " + regMobileResponse.substring(0,1));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "Регистрация мобильного устройства завершилась ошибкой: " + e.getLocalizedMessage());
                }
            }
        });

        Button loginButton = (Button) rootView.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        // если регистрация устройства прошла успешно, то проходим авторизацию
                        ServiceUtils.logon(getActivity().getResources().getString(R.string.logon),
                                sharedPref.getString(getActivity().getResources().getString(R.string.supersaved_mobile_password), "defaultPwd"),
                                new Callback<String>() {
                                    @Override
                                    public void call(String input) {

                                    }
                                });

            }
        });

        Button checkButton = (Button) rootView.findViewById(R.id.check_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // проверяем авторизацию
                ServiceUtils.checkAuth(getActivity().getString(R.string.check_auth));
            }
        });

        Button cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((TextView) rootView.findViewById(R.id.loginEditText)).getText().toString();
                String password = ((TextView) rootView.findViewById(R.id.password_editText)).getText().toString();
                String imei  = UserPreferences.getInstance().getImei();
                try {
                    new RegOrDelMobileTask(getActivity().getResources().getString(R.string.del_mobile),
                            username, password, imei, new Callback<String>() {
                        @Override
                        public void call(String input) {

                        }
                    }).execute().get();
                    Log.d(TAG, "Отвязка мобильного устройства прошла успешно");
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(TAG, "Отвязка мобильного устройства завершилась ошибкой: " + e.getLocalizedMessage());
                }
            }
        });

        return rootView;
    }
}
