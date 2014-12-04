package com.gsbina.deviceownermanager;

import android.app.Activity;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ApplicationHiddenFragment extends Fragment implements AbsListView.OnItemClickListener {

	private ListAdapter mAdapter;

	public static ApplicationHiddenFragment newInstance() {
		return new ApplicationHiddenFragment();
	}

	public ApplicationHiddenFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<ApplicationHidden> items = buildApplicationHiddenList();
		mAdapter = new ArrayAdapter<ApplicationHidden>(getActivity(),
				R.layout.app_hidden, R.id.package_name, items);
	}

	private List<ApplicationHidden> buildApplicationHiddenList() {
		Activity activity = getActivity();
		DevicePolicyManager manager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName admin = AdminReceiver.getComponentName(activity);

		PackageManager packageManager = activity.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		int flags = PackageManager.GET_DISABLED_COMPONENTS | PackageManager.GET_UNINSTALLED_PACKAGES;
		List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, flags);
		Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(packageManager));
		List<String> addedPackageName = new ArrayList<String>();
		List<ApplicationHidden> list = new ArrayList<ApplicationHidden>();
		for (ResolveInfo info : resolveInfos) {
			String packageName = info.activityInfo.packageName;
			if (!addedPackageName.contains(packageName)) {
				addedPackageName.add(packageName);
				boolean hidden = manager.isApplicationHidden(admin, packageName);
				list.add(new ApplicationHidden(packageName, hidden));
			}
		}

		Collections.sort(list, new ApplicationHiddenComparator());

		return list;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_applicationhidden, container, false);

		AbsListView listView = (AbsListView) view.findViewById(android.R.id.list);
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(this);

		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Activity activity = getActivity();
		if (activity == null) {
			return;
		}
		ApplicationHidden appHidden = (ApplicationHidden) parent.getItemAtPosition(position);
		DevicePolicyManager manager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName admin = AdminReceiver.getComponentName(activity);

		manager.setApplicationHidden(admin, appHidden.packageName, !appHidden.hidden);

		// Update view
		appHidden.hidden = !appHidden.hidden;
		parent.getAdapter().getView(position, view, parent);
	}

	public static class ApplicationHidden {
		public String packageName;
		public boolean hidden;

		public ApplicationHidden(String packageName, boolean hidden) {
			this.packageName = packageName;
			this.hidden = hidden;
		}

		@Override
		public String toString() {
			return packageName + " - " + hidden;
		}
	}

	private static class ApplicationHiddenComparator implements Comparator<ApplicationHidden> {

		@Override
		public int compare(ApplicationHidden lhs, ApplicationHidden rhs) {
			if (lhs.hidden) {
				if (!rhs.hidden) {
					return -1;
				}
			} else {
				if (rhs.hidden) {
					return 1;
				}
			}
			return lhs.packageName.compareTo(rhs.packageName);
		}
	}
}
