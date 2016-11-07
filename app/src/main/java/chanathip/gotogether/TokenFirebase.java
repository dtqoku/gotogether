package chanathip.gotogether;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by neetc on 11/7/2016.
 */

public class TokenFirebase extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String newToken = FirebaseInstanceId.getInstance().getToken();
        saveToken(newToken);
    }

    private void saveToken(String token){
        SharedPreferences sharedPreferences = getSharedPreferences("Token", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString("token",token);
        sharedPreferencesEditor.apply();
    }
}
