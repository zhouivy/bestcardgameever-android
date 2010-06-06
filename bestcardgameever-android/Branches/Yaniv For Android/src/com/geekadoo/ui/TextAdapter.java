package com.geekadoo.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextAdapter extends BaseAdapter{
	private Context mContext;
	private int		mNumColumns = 4;
	private String[] mStrings;
	
    public TextAdapter(Context c, String[] scores) {
        mContext = c;
        mStrings  = scores;
    }

    @Override
	public int getCount() {
		return mStrings.length;
	}

	@Override
	public Object getItem(int position) {
		//Get the data item associated with the specified position in the data set.
		return null;
	}

	@Override
	public long getItemId(int position) {
		//Get the row id associated with the specified position in the list.
		return position/mNumColumns;
	}

	@Override
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView;
		LinearLayout container;
		
//        if (convertView == null) {  // if it's not recycled, initialize some attributes
        	container = new LinearLayout(mContext);
            textView = new TextView(mContext);
            textView.setBackgroundColor(Color.BLACK);
            textView.setText(mStrings[position]);
            textView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            textView.setGravity(Gravity.CENTER);
            container.addView(textView);
        	container.setBackgroundColor(Color.WHITE);
        	
        	// All the cells should have border at the bottom & right
       		container.setPadding(0, 0, 1, 1);
        	// If this is the first row - add top border
        	if (position < mNumColumns) {
            	container.setPadding(container.getPaddingLeft(), 1, container.getPaddingRight(), container.getPaddingBottom());
        	}
        	// If this is the first column - add left border 
        	if (position % mNumColumns == 0){
            	container.setPadding(1, container.getPaddingTop(), container.getPaddingRight(), container.getPaddingBottom());
        	}
//        } else {
//        	container = (LinearLayout)convertView;
//        	Log.e("ELAD","here");
//        }
        return container;
    }
}
