package chanathip.gotogether;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by neetc on 10/25/2016.
 */

public class DialogConect extends ProgressDialog {

    public DialogConect(Context context) {
        super(context);
    }

    public void cancel(){
        super.cancel();
    }

}
