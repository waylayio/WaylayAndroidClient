package waylay.client.activity;

import waylay.client.WaylayApplication;


public class WaylayFragment extends BaseFragment {

    protected final WaylayApplication getWaylayApplication(){
        return (WaylayApplication)getActivity().getApplication();
    }
}
