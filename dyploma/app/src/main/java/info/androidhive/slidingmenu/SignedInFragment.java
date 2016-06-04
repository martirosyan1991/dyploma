package info.androidhive.slidingmenu;

import android.app.Fragment;
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

public class SignedInFragment extends Fragment {

    private static final String TAG = "AuthorizationFragment";

    public SignedInFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_signed_in, container, false);

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
