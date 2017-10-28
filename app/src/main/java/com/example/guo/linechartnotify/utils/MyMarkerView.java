
package com.example.guo.linechartnotify.utils;

import android.content.Context;
import android.widget.TextView;

import com.example.guo.linechartnotify.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent,tv_marker_time;
    private List<String> mXList;

    public MyMarkerView(Context context, int layoutResource, List<String> xData) {

        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.tvContent);
        tv_marker_time = (TextView) findViewById(R.id.tv_marker_time);
        mXList = xData;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));

        } else {
            //这里
            tvContent.setText( Utils.formatNumber(e.getY(), 0, true));

            String time = mXList.get((int)e.getX());
            tv_marker_time.setText(time);

        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
