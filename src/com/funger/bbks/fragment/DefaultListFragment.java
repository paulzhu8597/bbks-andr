package com.funger.bbks.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.funger.bbks.BookDetailActivity;
import com.funger.bbks.MainActivity;
import com.funger.bbks.R;
import com.funger.bbks.app.UIHelper;

public class DefaultListFragment extends ListFragment {
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	
	View mView = inflater.inflate(R.layout.book_list, null);
	UIHelper.bindHeader( ((MainActivity)getActivity()) , mView, "书城");
	
	return mView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	Map<String, Object> item1 = new HashMap<String, Object>();
	item1.put("list_title", getString(R.string.title1));
	item1.put("list_image", R.drawable.p1);
	item1.put("list_contect", getString(R.string.test));
	
	Map<String, Object> item2 = new HashMap<String, Object>();
	item2.put("list_title", getString(R.string.title1));
	item2.put("list_image", R.drawable.p2);
	item2.put("list_contect", getString(R.string.test));
	
	Map<String, Object> item3 = new HashMap<String, Object>();
	item3.put("list_title", getString(R.string.title1));
	item3.put("list_image", R.drawable.p3);
	item3.put("list_contect", getString(R.string.test));
	Map<String, Object> item4 = new HashMap<String, Object>();
	item4.put("list_title", getString(R.string.title1));
	item4.put("list_image", R.drawable.p4);
	item4.put("list_contect", getString(R.string.test));
	Map<String, Object> item5 = new HashMap<String, Object>();
	item5.put("list_title", getString(R.string.title1));
	item5.put("list_image", R.drawable.p5);
	item5.put("list_contect", getString(R.string.test));
	Map<String, Object> item6 = new HashMap<String, Object>();
	item6.put("list_title", getString(R.string.title1));
	item6.put("list_image", R.drawable.p6);
	item6.put("list_contect", getString(R.string.test));
	Map<String, Object> item7 = new HashMap<String, Object>();
	item7.put("list_title", getString(R.string.title1));
	item7.put("list_image", R.drawable.p7);
	item7.put("list_contect", getString(R.string.test));
	List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	data.add(item1);
	data.add(item2);
	data.add(item3);
	data.add(item4);
	data.add(item5);
	data.add(item6);
	data.add(item7);

	String[] from = new String[] { "list_title", "list_image",
		"list_contect" };
	int[] to = new int[] { R.id.list_title, R.id.list_image,
		R.id.list_contect };
//	SimpleAdapter adapter = new SimpleAdapter(getActivity(), data,
//		R.layout.book_list_item, from, to);
//	setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);
	Intent intent = new Intent(getActivity(), BookDetailActivity.class);
	startActivity(intent);
    }
}
