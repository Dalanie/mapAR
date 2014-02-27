package de.dala;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;

import de.dala.views.AccuracyView;

/**
 * This class handles the different layouts, which should be shown in the
 * application
 * 
 * @author Daniel Langerenken
 * 
 */
public class LayerManager {

	private static final int MARGIN = 15;

	/**
	 * basic layout which is visible by default
	 */
	private RelativeLayout basicLayout;

	/**
	 * layout which contains specific debug elements (e.g. sensor-data)
	 */
	private LinearLayout debugLayout;

	private FragmentActivity activity;

	private int actionBarHeight = 100;

	public LayerManager(SherlockFragment fragment) {
		activity = fragment.getActivity();
		if (activity instanceof MainActivity) {
			actionBarHeight = ((MainActivity) activity).getActionBarHeight();
		}
	}

	public View initializeViews() {
		debugLayout = new LinearLayout(activity);
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		basicLayout = (RelativeLayout) inflater.inflate(R.layout.activity_main,
				null);
		addBasicLayoutCenter(debugLayout);
		return basicLayout;
		// basicLayout = new FrameLayout(activity);
		// addBasicLayoutCenter(debugLayout);
		// return basicLayout;
	}

	public void addBasicLayout(View view) {
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		basicLayout.addView(view);
	}

	public void addBasicLayoutTop(View view) {
		basicLayout.addView(view, 0);
	}

	public void addDebugElement(View view) {
		debugLayout.addView(view);
	}

	public void removeDebugElement(View view) {
		debugLayout.removeView(view);
	}

	public void removeBasicLayout(View view) {
		basicLayout.removeView(view);
	}

	public void addBasicLayoutCenter(View view) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		view.setLayoutParams(params);
		basicLayout.addView(view);
	}

	public void addBasicLayout(View view, LayoutParams params) {
		basicLayout.addView(view, params);
	}

	public void addBasicLayoutBottom(View view) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		view.setLayoutParams(params);
		basicLayout.addView(view);
	}

	public void addBasicLayoutRight(AccuracyView view) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_RIGHT);
		params.setMargins(0, actionBarHeight + MARGIN, MARGIN, 0);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		view.setLayoutParams(params);
		basicLayout.addView(view);
	}
}
