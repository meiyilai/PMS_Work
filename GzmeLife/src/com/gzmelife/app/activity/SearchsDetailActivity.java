package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.LvfoodSearchAdapter;
import com.gzmelife.app.adapter.LvfoodsSearchAdapter;
import com.gzmelife.app.adapter.LvfoodsSearchsAdapter;
import com.gzmelife.app.bean.SearchFoodBean;
import com.gzmelife.app.bean.SearchMenuBookBean;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.TimeNode;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchsDetailActivity extends BaseActivity {
	private ListView lv_food;
	LvfoodsSearchsAdapter lvFoodSearchAdapter;
	TextView tv_title;

	private List<SearchFoodBean> searchMenuBookBeanList = new ArrayList<SearchFoodBean>();
	private String name;
	UserInfoBean user;

	TimeNode timeNode;
	int startTime;
	int endTime;
	String step;
	boolean state;
	private String filePath;
	private int count;
	private ArrayList<String> threeList;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_search_deatail);
		// if (KappAppliction.liLogin == 1) {
		// user = KappAppliction.myApplication.getUser();
		// if (user != null) {
		// searchMenuBook(text);
		// }
		//
		// } else {
		// KappUtils.showToast(context, "暂未登录");
		// Intent intent = new Intent(SearchsDetailActivity.this,
		// LoginActivity.class);
		// startActivity(intent);
		// SearchsDetailActivity.this.finish();
		// return;
		// }

		initView();

	}

	private void initView() {
		
		threeList = getIntent().getStringArrayListExtra("threeList");
		filePath = getIntent().getStringExtra("filePath");
		timeNode = (TimeNode) getIntent().getSerializableExtra("timeNode");
		state = getIntent().getBooleanExtra("isEdt", false);
		startTime = getIntent().getIntExtra("startTime", 0);
		endTime = getIntent().getIntExtra("endTime", 0);
		step = getIntent().getStringExtra("step");
		count = getIntent().getIntExtra("count", 0);
		lv_food = (ListView) findViewById(R.id.lv_food);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("搜索结果");
		name = getIntent().getStringExtra("name");
		searchMenuBook(name);
		final ArrayList<String> mList = new ArrayList<String>();
		lv_food.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String strName = searchMenuBookBeanList.get(position).getName();
				mList.add(strName);
				Intent intent = new Intent();
				intent.putExtra("mlistMore", mList);
				intent.putExtra("timeNode", timeNode);
				intent.putExtra("startTime", startTime);
				intent.putExtra("endTime", endTime);
				intent.putExtra("step", step);
				intent.putExtra("isEdt", state);
				intent.putExtra("filePath", filePath);
				setResult(RESULT_OK, intent);
				SearchsDetailActivity.this.finish();
				KappUtils.showToast(context, "食材添加成功");
				// startActivity(intent);
				// CookFoodsMaterialManageActivity cook = new
				// CookFoodsMaterialManageActivity();
				// cook.cookfood.finish();
				// AddStepActivity add = new AddStepActivity();
				// add.instance.finish();
				// FoodsMangerSearchActivity foods = new
				// FoodsMangerSearchActivity();
				// foods.fooddManger.finish();

			}
		});
	}

	private void searchMenuBook(String text) {
		// 搜索
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_SERACHFOODSTORE);
		params.addBodyParameter("foodStoreName", text);
		params.addBodyParameter("userId", KappAppliction.myApplication
				.getUser().getId());

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result.trim());
					// 菜谱
					MyLog.i(MyLog.TAG_I_JSON, "result123=" + result);
					searchMenuBookBeanList = gson.fromJson(
							obj.getJSONObject("data")
									.getJSONArray("foodStores").toString(),
							new TypeToken<List<SearchFoodBean>>() {
							}.getType());
					if (searchMenuBookBeanList.size() == 0) {
						KappUtils.showToast(SearchsDetailActivity.this,
								"搜索不到结果哦");
					} else {
						lvFoodSearchAdapter = new LvfoodsSearchsAdapter(
								context, searchMenuBookBeanList);
						// ImageView iv_icon = (ImageView)
						// findViewById(R.id.iv_icon);
						// iv_icon.setVisibility(View.GONE);
						lv_food.setAdapter(lvFoodSearchAdapter);
						lvFoodSearchAdapter.notifyDataSetChanged();
						// lv_food.setOnItemClickListener(new
						// OnItemClickListener() {
						//
						// @Override
						// public void onItemClick(AdapterView<?> parent,
						// View view, int position, long id) {
						// // TODO Auto-generated method stub
						// Intent intent = new Intent(context,
						// NetCookBookDetailActivity.class);
						// // intent.putExtra("category",
						// // categoryFirstBeanList.get(position));
						// intent.putExtra("position", position);
						// intent.putExtra("menuBookId",
						// searchMenuBookBeanList.get(position)
						// .getId());
						// startActivity(intent);
						// }
						//
						// });

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println("======result======>>>>" + result.toString());
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// TODO Auto-generated method stub
				closeDlg();
			}

			@Override
			public void onCancelled(CancelledException cex) {
				// TODO Auto-generated method stub
				closeDlg();
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				closeDlg();
			}

		});

	}
}
