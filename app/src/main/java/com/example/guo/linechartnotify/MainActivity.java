package com.example.guo.linechartnotify;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.guo.linechartnotify.bean.ChartDayBean;
import com.example.guo.linechartnotify.utils.GetJsonDataUtil;
import com.example.guo.linechartnotify.utils.MyMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.components.Legend.LegendPosition.RIGHT_OF_CHART_INSIDE;

/**
 * Created by ${GuoZhaoHui} on 2017/10/28.
 * Email:guozhaohui628@gmail.com
 * @author Guo
 */

public class MainActivity  extends AppCompatActivity implements OnClickListener,OnChartValueSelectedListener {

    private LineChart lineChart;

    //日实时功率的xy
    private List<String> xDayData = new ArrayList<>();
    private List<String> yDayData = new ArrayList<>();

    //月发电量的xy
    private List<String> xMonthData = new ArrayList<>();
    private List<String> yMonthData = new ArrayList<>();

    //年发电量的xy
    private List<String> xYearData = new ArrayList<>();
    private List<String> yYearData = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        //默认显示
        reqeustDayData();

    }

    private void initView() {
        Button btDay = (Button) this.findViewById(R.id.bt_day);
        Button btMonth = (Button) this.findViewById(R.id.bt_month);
        Button btYear = (Button) this.findViewById(R.id.bt_year);
        Button btAll = (Button) this.findViewById(R.id.bt_all);

        btDay.setOnClickListener(this);
        btMonth.setOnClickListener(this);
        btYear.setOnClickListener(this);
        btAll.setOnClickListener(this);

        //对折线图初始化
        initChartView();
    }

    /**
     * 初始化折线图
      */
    private void initChartView() {
        lineChart = (LineChart) this.findViewById(R.id.linechart);

        //在点击高亮值时回调
        lineChart.setOnChartValueSelectedListener(this);

        //设置整个图表的颜色
        lineChart.setBackgroundResource(R.drawable.bg_line_chart);

        Description description = lineChart.getDescription();
        description.setYOffset(10);
        description.setEnabled(true);
        description.setText("时间");

        //设置标签的位置(如 发电量 实时功率)
        lineChart.getLegend().setPosition(RIGHT_OF_CHART_INSIDE);

        //是否可以缩放、移动、触摸
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);

        //不能让缩放，不然有bug，所以接口也没实现
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(true);

        //设置图表距离上下左右的距离
        lineChart.setExtraOffsets(10, 10, 10, 0);

        //获取左侧侧坐标轴
        YAxis leftAxis = lineChart.getAxisLeft();

        //设置是否显示Y轴的值
        leftAxis.setDrawLabels(true);
        leftAxis.setTextColor(this.getResources().getColor(R.color.homecolor));

        //设置所有垂直Y轴的的网格线是否显示
        leftAxis.setDrawGridLines(true);

        //设置虚线
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        //设置Y极值，我这里没设置最大值，因为项目需要没有设置最大值
        leftAxis.setAxisMinimum(0f);

        //将右边那条线隐藏
        lineChart.getAxisRight().setEnabled(false);
        //获取X轴
        XAxis xAxis = lineChart.getXAxis();
        //设置X轴的位置，可上可下
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //将垂直于X轴的网格线隐藏，将X轴显示
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        //设置X轴上lable颜色和大小
        xAxis.setTextSize(8f);
        xAxis.setTextColor(Color.GRAY);

        //设置X轴高度
        xAxis.setAxisLineWidth(1);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_day:
                if (xDayData.size() != 0 || yDayData.size() != 0) {  //不请求服务器
                    setChartData(xDayData, yDayData,0);
                    lineChart.invalidate();
                } else {
                    reqeustDayData();  //请求服务器
                }
                break;
            case R.id.bt_month:
                if (xMonthData.size() != 0 || yMonthData.size() != 0) {  //有数据
                    setChartData(xMonthData, yMonthData,1);
                    lineChart.invalidate();
                }else{  //没数据,请求服务器
                    reqeustMonthData();
                }
                break;
            case R.id.bt_year:
                if (xYearData.size() != 0 || yYearData.size() != 0) {  //有数据
                    setChartData(xYearData, yYearData,1);
                    lineChart.invalidate();
                }else{  //没数据,请求服务器
                    reqeustYearData();
                }
                break;
            case R.id.bt_all:
                Toast.makeText(this,"hi,what's up?",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     * 数据都是从服务器获取，这里我拿出了项目接口一天的数据，做成json文件（见assests）
     * 关键在于代码逻辑以及方法，反正这个方法是用来获取日实时功率
     */
    private void reqeustDayData(){
        String jsondata = new GetJsonDataUtil().getJson(this,"day.json");
        try {
            JSONArray data = new JSONArray(jsondata);
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);

                //循环遍历时X轴和Y轴的值获取到
                String time = jsonObject.getString("saveTime").substring(11, 16);
                xDayData.add(time);
                yDayData.add(jsonObject.getInt("attribValue") + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (xDayData.size() != 0 || yDayData.size() != 0) {
            setChartData(xDayData, yDayData,0);
            lineChart.invalidate();
        }

    }

    /**
     * 数据都是从服务器获取，这里我拿出了项目接口一天的数据，做成json文件（见assests）
     * 关键在于代码逻辑以及方法，反正这个方法是用来获取月发电量
     */
    private void reqeustMonthData(){
        String jsondata = new GetJsonDataUtil().getJson(this,"month.json");
        try {
            JSONArray data = new JSONArray(jsondata);
            for (int i = 0; i < data.length(); i++) {
                xMonthData.add(i+1+"天");
                yMonthData.add(String.valueOf(data.get(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (xMonthData.size() != 0 || yMonthData.size() != 0) {
            setChartData(xMonthData, yMonthData,1);
            lineChart.invalidate();
        }
    }



    /**
     * 数据都是从服务器获取，这里我拿出了项目接口一天的数据，做成json文件（见assests）
     * 关键在于代码逻辑以及方法，反正这个方法是用来获取年发电量
     */
    private void reqeustYearData(){
        String jsondata = new GetJsonDataUtil().getJson(this,"year.json");
        try {
            JSONArray data = new JSONArray(jsondata);
            for (int i = 0; i < data.length(); i++) {
                xYearData.add(i+1+"月");
                yYearData.add(String.valueOf(data.get(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (xYearData.size() != 0 || yYearData.size() != 0) {
            setChartData(xYearData, yYearData,1);
            lineChart.invalidate();
        }
    }

    /**
     * 为折线图设置数据，并且第一个图表的标签是日实时功率，后面的都是发电量，通过flag来判断,flag 0 实时功率 ，1  发电量
     * @param xData
     * @param yData
     * @param flag
     */
    private void setChartData(final List<String> xData , List<String> yData, int flag) {

        lineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                //对X轴上的值进行Format格式化，转成相应的值
                int intValue = (int)value;

                //筛选出自己需要的值，一般都是这样写没问题，并且一定要加上这个判断，不然会出错
                if(xData.size()>intValue && intValue>=0){
                    //这样显示在X轴上值就是 05:30  05:35，不然会是1.0  2.0
                    return xData.get(intValue);
                }else{
                    return "";
                }
            }
        });

        lineChart.invalidate();

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view,xData);
        mv.setChartView(lineChart);
        lineChart.setMarker(mv);

        final ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < yData.size(); i++) {

            //注意这里的Entry（不一定需要）采用这种方式构造，采用其他的结果是一样的
            values.add(new Entry(i, Float.valueOf(yData.get(i)),xData.get(i)));
        }

        LineDataSet lineDataset ;

        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {

            lineDataset = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            lineDataset.setValues(values);
            if(flag==0){
                lineDataset.setLabel("实时功率");
                lineDataset.setDrawFilled(true);
            }
            else{
                lineDataset.setLabel("发电量");
                lineDataset.setDrawFilled(false);
            }

            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();

         }else{
            if(flag==0){
                lineDataset = new LineDataSet(values, "实时功率");
                lineDataset.setDrawFilled(true);
            }else{
                lineDataset = new LineDataSet(values, "发电量");
                lineDataset.setDrawFilled(false);
            }

            lineDataset.setColor(this.getResources().getColor(R.color.homecolor));

            //设置是否显示圆点
            lineDataset.setDrawCircles(false);

            //是否显示每个点的Y值
            lineDataset.setDrawValues(false);

            LineData lineData = new LineData(lineDataset);
            lineChart.setData(lineData);
            lineChart.animateX(1000);
        }
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
