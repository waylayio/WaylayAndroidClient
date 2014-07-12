package waylay.client.activity;

import android.app.Fragment;
import android.content.Intent;


public class BaseFragment extends Fragment {

    protected void alert(final String alert) {
        AlertDialogActivity.show(getActivity(), alert);
    }
}
