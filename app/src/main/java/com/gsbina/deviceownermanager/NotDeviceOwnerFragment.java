package com.gsbina.deviceownermanager;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NotDeviceOwnerFragment extends Fragment {

	public static NotDeviceOwnerFragment newInstance() {
		return new NotDeviceOwnerFragment();
	}

	public NotDeviceOwnerFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_not_device_owner, container, false);
	}


}
