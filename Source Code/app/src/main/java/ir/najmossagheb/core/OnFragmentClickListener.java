package ir.najmossagheb.core;


import android.support.v7.app.ActionBar;

import ir.najmossagheb.model.NodeCategory;
import ir.najmossagheb.model.NodeWallpaper;

public interface OnFragmentClickListener extends ActionBar.OnNavigationListener {
	public void onCategorySelected(NodeCategory node);

	public void onWallpaperSelected(NodeWallpaper node);
}
