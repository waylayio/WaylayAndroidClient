package waylay.client.activity;

import android.app.Fragment;

import waylay.client.WaylayApplication;


public class WaylayFragment extends Fragment {

    protected final WaylayApplication getWaylayApplication(){
        return (WaylayApplication)getActivity().getApplication();
    }
}
