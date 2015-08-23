package ir.najmossagheb.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;

import ir.najmossagheb.adapter.Adapter;
import ir.najmossagheb.adapter.Adapter.OnGetViewListener;
import ir.najmossagheb.core.com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import ir.najmossagheb.core.com.jess.ui.TwoWayGridView;
import ir.najmossagheb.R;

public abstract class GridFragment extends Fragment implements OnItemLongClickListener, OnGetViewListener, OnItemClickListener {

	private TwoWayGridView mGridView;
	private View mView;
	private Adapter mAdapter;

	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		if (container == null) {
			return null;
		}

		this.mView = inflater.inflate(R.layout.fragment_grid, container, false);
		this.mGridView = (TwoWayGridView) mView.findViewById(R.id.grid);
		return mView;
	}

	public void setAdapter (Adapter adapter) {
		this.mAdapter = adapter;
		this.mGridView.setAdapter(this.mAdapter);
		this.mGridView.setNumColumns(super.getResources().getInteger(R.integer.column_count));
		this.mGridView.setNumRows(super.getResources().getInteger(R.integer.column_count));
		this.mGridView.setOnItemClickListener(this);
		this.mGridView.setLongClickable(false);
		this.mGridView.setClickable(true);
	}

	public void setData (ArrayList<?> data) {
		this.setAdapter(new Adapter(this, super.getActivity(), data));
	}

}
