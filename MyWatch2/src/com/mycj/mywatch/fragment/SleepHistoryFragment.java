package com.mycj.mywatch.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mycj.mywatch.BaseFragment;
import com.mycj.mywatch.R;
import com.mycj.mywatch.bean.Constant;
import com.mycj.mywatch.bean.SleepData;
import com.mycj.mywatch.business.ParseSleepData;
import com.mycj.mywatch.util.DataUtil;
import com.mycj.mywatch.util.DateUtil;
import com.mycj.mywatch.util.SharedPreferenceUtil;
import com.mycj.mywatch.view.HistoryView;

public class SleepHistoryFragment extends BaseFragment implements OnClickListener{
	private final String SDF = "yyyy-MM";
	private Handler mhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
	
				break;

			default:
				break;
			}
		};
	};
	private TextView tvDate;
	private TextView tvPreious;
	private TextView tvNext;
	private HistoryView sleepHistoryView;
	private TextView tvComplete;
	private TextView tvTotal;
	private int monthMaxDay =30;
	private ProgressDialog showProgressDialog;
//	private float total;
//	private float awak;
//	private float light;
//	private float deep;
	private Date currentDate;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sleep_history, container,false);
		initViews(view);
		setListener();
		loadData();
		return view;
	}
	
	@Override
	public void onResume() {
//		total = 0f;
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onPause() {
		super.onPause();
		if (showProgressDialog != null&&showProgressDialog.isShowing()) {
			showProgressDialog.dismiss();
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_preious:
		//1.动画变化
		startAnimation(tvPreious);
			currentDate = getDateForDiff(-1);
		tvDate.setText(DateUtil.dateToString(currentDate, SDF));
		//3。查询数据库，根据日期
		List<Float> countList = getCountData(currentDate);
		Log.e("", "----------------------" +countList);
		sleepHistoryView.setData(countList);
		break;
	case R.id.tv_next:
		//1.动画变化
		startAnimation(tvPreious);
		//2。日期变化 -1
		 currentDate = getDateForDiff(1);
		tvDate.setText(DateUtil.dateToString(currentDate, SDF));
		//3。查询数据库，根据日期
		List<Float> listNext = getCountData(currentDate);
		Log.e("", "----------------------" +listNext);
		sleepHistoryView.setData(listNext);
		break;
		default:
			break;
		}
		loadData();
	}
	
	
	private void loadData(){
		showProgressDialog = showProgressDialog(getResources().getString(R.string.loading));
		mhandler.post(new Runnable() {
			@Override
			public void run() {
				List<Float> listNext = getCountData(currentDate);
				sleepHistoryView.setData(listNext);
				updateCountSleep();
			}
		});
		mhandler.postDelayed((new Runnable() {
			@Override
			public void run() {
				if (showProgressDialog != null) {
					showProgressDialog.dismiss();
				}
			}
		}),3000);
	}
	
	
	private void updateCountSleep(){
		String total = String.valueOf(sleepHistoryView.getToal());
		float avg = Float.valueOf(total)/monthMaxDay;
		tvTotal.setText(DataUtil.format(Float.valueOf(total)/monthMaxDay)+"");
		int start = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_SLEEP_START_HOUR, 0);
		int end = (int) SharedPreferenceUtil.get(getActivity(), Constant.SHARE_SLEEP_END_HOUR, 23);
		int size;
		if (start>end) {
			size = (24-start)+end;
		}else{
			size = end-start;
		}
		tvComplete.setText(DataUtil.format(avg*100/size)+"%");
	}
	
	
	
	private List<Float> getCountData(Date date) {
		List<Float> countData = new ArrayList<Float>();
		//获取本月天数
		Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	Log.v("", "——————————————————————月份 ："+c.get(Calendar.MONTH));
    	monthMaxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
    	Log.e("", "本月天数 monthMaxDay : " + monthMaxDay );
    	//遍历查找每天的数据
		for (int i = 0; i < monthMaxDay; i++) {
			c.set(Calendar.DAY_OF_MONTH, i+1);
			SleepData findSleepDateByDate = findSleepDateByDate(c.getTime());
		
			//有数据就添加
			if (findSleepDateByDate!=null&&findSleepDateByDate.getSdatas()!=null) {
				String sdatas = findSleepDateByDate.getSdatas();
				String[] split = sdatas.split(",");
//				parseSleeps(split);
				float[] datas = ParseSleepData.parseSleepData(split);
				countData.add(datas[1]+datas[2]);
			}else{
				//没有数据就为0
				countData.add(0f);
			}
		}
		if (showProgressDialog != null&&showProgressDialog.isShowing()) {
			showProgressDialog.dismiss();
		}
		//从数据库中获取一个月所有的天数的数据
//		List<SleepData> list = findAllSleepDateByMonth(date);
		return countData;
	}
	
	/**
	 * 根据每天的数据得到每天的睡眠时间   
	 * @param sleeps
	 * @return
	 */
//	private void parseSleeps(String [] sleeps){
//		total=0;
//		awak=0;
//		deep=0;
//		light=0;
//		for (int i = 0; i < sleeps.length; i++) {
//			switch (Integer.valueOf(sleeps[i])) {
//			case 0:
//				awak+=1f;
//				break;
//			case 1:
//				awak += 0.75f;
//				light += 0.25f;
//				break;
//			case 2:
////				awakTime += 0.25f;
//				light += 1f;
//				break;
//			case 3:
//				light += 0.75f;
//				deep+=0.25f;
//				break;
//			case 4:
//				light+=0.25f ;
//				deep +=0.75f;
//				break;
//			case 5:
//				deep += 1f;
//				break;
//			default:
//				break;
//			}
//			total = light+deep;
//		}
//	}
	
	/**
	 *	根据日期查找sleepData
	 * @param date
	 * @return
	 */
	private SleepData findSleepDateByDate(Date date){
		String sql = DateUtil.dateToString(date, "yyyyMMdd");
		List<SleepData>  sleepDataNext = DataSupport.where("year=? and month=? and day=?", sql.substring(0,4),sql.substring(4,6),sql.substring(6,8)).find(SleepData.class);
		
		if (sleepDataNext!=null && sleepDataNext.size()>0) {
			return sleepDataNext.get(0);
		}else {
			return null;
		}
	}
	
	
	
	private List<SleepData> findAllSleepDateByMonth(Date date) {
		return null;
	}

	private Date getDateForDiff(int diff){
		String dateStr = tvDate.getText().toString();
		Date date = DateUtil.stringToDate(dateStr, SDF);
		Date diffDate = DateUtil.getDateOfDiffMonth(date, diff);
		return diffDate;
	}
	

	private void initViews(View view) {
		tvDate = (TextView) view.findViewById(R.id.tv_sleep_history_date);
		tvPreious = (TextView) view.findViewById(R.id.tv_preious);
		tvNext = (TextView) view.findViewById(R.id.tv_next);
		tvTotal = (TextView) view.findViewById(R.id.tv_sleep_total);
		tvComplete = (TextView) view.findViewById(R.id.tv_sleep_complete);
		sleepHistoryView = (HistoryView) view.findViewById(R.id.history_hr);
		sleepHistoryView.setTextY(new String[]{"7","8","9","10","11","12"});
		sleepHistoryView.setMax(12);
		currentDate = new Date();
		tvDate.setText(DateUtil.dateToString(currentDate, SDF));//默认本月
		
	}
	

	private void setListener(){
		tvPreious.setOnClickListener(this);
		tvNext.setOnClickListener(this);
	}

	
	
}
