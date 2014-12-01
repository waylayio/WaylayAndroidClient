package waylay.client.activity;


import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    protected void alert(final String alert) {
        AlertDialogActivity.show(getActivity(), alert);
    }
}
