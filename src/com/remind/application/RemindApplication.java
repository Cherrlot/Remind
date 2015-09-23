package com.remind.application;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Application;
import android.app.Service;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.help.remind.R;
import com.remind.sevice.WeatherGetRequest;
import com.remind.util.AppUtil;

public class RemindApplication extends Application {
	/**
	 * 晴
	 */
	public static int WEATHER_FINE = R.drawable.sun;
	/**
	 * 多云,暂无
	 */
	public static int WEATHER_CLOUD = R.drawable.cloud;
	/**
	 * 雾霾,暂无
	 */
	public static int WEATHER_HAZE = R.drawable.sun;
	/**
	 * 雾,暂无
	 */
	public static int WEATHER_FOG = R.drawable.sun;
	/**
	 * 大雨,暂无
	 */
	public static int WEATHER_DAYU = R.drawable.rain;
	/**
	 * 中雨,暂无
	 */
	public static int WEATHER_ZHONGYU = R.drawable.rain;
	/**
	 * 小雨
	 */
	public static int WEATHER_XIAOYU = R.drawable.rain;
	/**
	 * 阵雨,暂无
	 */
	public static int WEATHER_ZHENYU = R.drawable.sun;
	/**
	 * 雷阵雨
	 */
	public static int WEATHER_LEIZHENYU = R.drawable.lei_zhen_yu;
	/**
	 * 雨夹雪,暂无
	 */
	public static int WEATHER_YUJIAXUE = R.drawable.sun;
	/**
	 * 雪
	 */
	public static int WEATHER_SNOW = R.drawable.snow;
	/**
	 * 阴
	 */
	public static int WEATHER_YIN = R.drawable.cloud;
	
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;

	public TextView mLocationResult, logMsg, mTemp, mWeather;
	public TextView trigger, exit;
	public Vibrator mVibrator;
	public ImageView mWeatherImg;

	private int mWeatherIconFlg;
	/**
	 * 城市名称
	 */
	public String city;

	private RequestQueue mQueue;
	private String code = "101010100";
	/**
	 * 城市编码
	 */
	public String cityCode;
	public final static int GET_INFO_OK = 1000001;// 成功
	public final static int GET_INFO_ERROR = 1000002;// 连接出错
	public final static int GET_WEATHER_OK = 1000003;// 连接出错
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_INFO_ERROR:
				Toast.makeText(getApplicationContext(), "获取天气失败",
						Toast.LENGTH_SHORT).show();
				break;
			case GET_INFO_OK:
				getWeatherByCode();
				break;
			case GET_WEATHER_OK:
				mLocationClient.stop();
				break;
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mVibrator = (Vibrator) getApplicationContext().getSystemService(
				Service.VIBRATOR_SERVICE);

		mQueue = Volley.newRequestQueue(this.getApplicationContext());
	}

	/**
	 * 实现实时位置回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// Receive Location
			// StringBuffer sb = new StringBuffer(256);
			// sb.append("time : ");
			// sb.append(location.getTime());
			// sb.append("\nerror code : ");
			// sb.append(location.getLocType());
			// sb.append("\nlatitude : ");
			// sb.append(location.getLatitude());
			// sb.append("\nlontitude : ");
			// sb.append(location.getLongitude());
			// sb.append("\nradius : ");
			// sb.append(location.getRadius());
			// if (location.getLocType() == BDLocation.TypeGpsLocation) {//
			// GPS定位结果
			// sb.append("\nspeed : ");
			// sb.append(location.getSpeed());// 单位：公里每小时
			// sb.append("\nsatellite : ");
			// sb.append(location.getSatelliteNumber());
			// sb.append("\nheight : ");
			// sb.append(location.getAltitude());// 单位：米
			// sb.append("\ndirection : ");
			// sb.append(location.getDirection());
			// sb.append("\naddr : ");
			// sb.append(location.getAddrStr());
			// sb.append("\ndescribe : ");
			// sb.append("gps定位成功");
			//
			// } else if (location.getLocType() ==
			// BDLocation.TypeNetWorkLocation) {// 网络定位结果
			// sb.append("\naddr : ");
			// sb.append(location.getAddrStr());
			// // 运营商信息
			// sb.append("\noperationers : ");
			// sb.append(location.getOperators());
			// sb.append("\ndescribe : ");
			// sb.append("网络定位成功");
			// } else if (location.getLocType() ==
			// BDLocation.TypeOffLineLocation) {// 离线定位结果
			// sb.append("\ndescribe : ");
			// sb.append("离线定位成功，离线定位结果也是有效的");
			// } else if (location.getLocType() == BDLocation.TypeServerError) {
			// sb.append("\ndescribe : ");
			// sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			// } else if (location.getLocType() ==
			// BDLocation.TypeNetWorkException) {
			// sb.append("\ndescribe : ");
			// sb.append("网络不同导致定位失败，请检查网络是否通畅");
			// } else if (location.getLocType() ==
			// BDLocation.TypeCriteriaException) {
			// sb.append("\ndescribe : ");
			// sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			// }
			// sb.append("\nlocationdescribe : ");// 位置语义化信息
			// sb.append(location.getLocationDescribe());
			// sb.append("\ncity : ");
			// sb.append(location.getCity());
			// sb.append("\ncityCode : ");
			// sb.append(location.getCityCode());
			// city = location.getCity();
			// List<Poi> list = location.getPoiList();// POI信息
			// if (list != null) {
			// sb.append("\npoilist size = : ");
			// sb.append(list.size());
			// for (Poi p : list) {
			// sb.append("\npoi= : ");
			// sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
			// }
			// }

			city = location.getCity();
			getCityCode();
		}

	}

	/**
	 * 显示地点
	 * 
	 * @param str
	 */
	public void showTemp(String str) {
		try {
			if (mTemp != null)
				mTemp.setText(str + "℃");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示地点
	 * 
	 * @param str
	 */
	public void showWeather(String str) {
		try {
			if (mWeather != null)
				mWeather.setText(str);
			if (mWeatherImg != null) {
				// 根据天气设置图片
				getWeatherImg(str);
				mWeatherImg.setBackgroundResource(mWeatherIconFlg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示地点
	 * 
	 * @param str
	 */
	public void showLoc(String str) {
		try {
			if (mLocationResult != null)
				mLocationResult.setText(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取可以用来查询的城市名称
	 * 
	 * @return
	 */
	public String getCityForWeather() {
		String result = "北京";
		if (null != city && city.length() > 0) {
			if (city.contains("市")) {
				result = city.replace("市", "");
			}
			if (city.contains("省")) {
				result = city.replace("省", "");
			}
		}

		showLoc(result);
		return AppUtil.encodeGB(result);
	}

	public void cancelRequest() {
		mQueue.cancelAll(this.getApplicationContext());
	}

	/**
	 * 获取编码
	 */
	public void getCityCode() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				String url = "http://apistore.baidu.com/microservice/cityinfo?cityname="
						+ getCityForWeather();
				WeatherGetRequest getRequest = new WeatherGetRequest(url,
						new Listener<String>() {

							@Override
							public void onResponse(String response) {

								if (null != response && !"".equals(response)) {
									try {
										// {
										// errNum: 0,
										// retMsg: "success",
										// retData: {
										// cityName: "北京",
										// provinceName: "北京",
										// cityCode: "101010100", //天气预报城市代码
										// zipCode: "100000", //邮编
										// telAreaCode: "010" //电话区号
										// }
										// }
										JSONObject obj = new JSONObject(
												response);
										String body = obj.getString("retData");
										JSONObject cotent = new JSONObject(body);
										code = cotent.getString("cityCode");
										mHandler.sendEmptyMessage(GET_INFO_OK);
									} catch (Exception e) {
										mHandler.sendEmptyMessage(GET_INFO_ERROR);
									}
								}

							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								mHandler.sendEmptyMessage(GET_INFO_ERROR);
							}
						}, null);
				mQueue.add(getRequest);
				Looper.loop();
			}
		}).start();

	}

	/**
	 * 根据编码获取天气
	 */
	public void getWeatherByCode() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();

				String url = "http://wthrcdn.etouch.cn/weather_mini?citykey="
						+ code;
				WeatherGetRequest getRequest = new WeatherGetRequest(url,
						new Listener<String>() {

							@Override
							public void onResponse(String response) {

								if (null != response && !"".equals(response)) {
									try {
										// {
										// "desc": "OK",
										// "status": 1000,
										// "data": {
										// "wendu": "21",
										// "ganmao":
										// "各项气象条件适宜，无明显降温过程，发生感冒机率较低。",
										// "forecast": [
										// {
										// "fengxiang": "无持续风向",
										// "fengli": "微风级",
										// "high": "高温 26℃",
										// "type": "多云",
										// "low": "低温 17℃",
										// "date": "23日星期三"
										// },
										// {
										// "fengxiang": "无持续风向",
										// "fengli": "微风级",
										// "high": "高温 26℃",
										// "type": "阴",
										// "low": "低温 17℃",
										// "date": "24日星期四"
										// },
										// {
										// "fengxiang": "无持续风向",
										// "fengli": "微风级",
										// "high": "高温 27℃",
										// "type": "多云",
										// "low": "低温 15℃",
										// "date": "25日星期五"
										// },
										// {
										// "fengxiang": "无持续风向",
										// "fengli": "微风级",
										// "high": "高温 26℃",
										// "type": "晴",
										// "low": "低温 16℃",
										// "date": "26日星期六"
										// },
										// {
										// "fengxiang": "无持续风向",
										// "fengli": "微风级",
										// "high": "高温 25℃",
										// "type": "阴",
										// "low": "低温 17℃",
										// "date": "27日星期天"
										// }
										// ],
										// "yesterday": {
										// "fl": "3-4级",
										// "fx": "南风",
										// "high": "高温 25℃",
										// "type": "阵雨",
										// "low": "低温 15℃",
										// "date": "22日星期二"
										// },
										// "aqi": "28",
										// "city": "北京"
										// }
										// }
										JSONObject obj = new JSONObject(
												response);
										String data = obj.getString("data");
										JSONObject cotent = new JSONObject(data);
										String temp = cotent.getString("wendu");
										String list = cotent
												.getString("forecast");
										JSONArray datas = new JSONArray(list);
										JSONObject today = datas
												.getJSONObject(0);
										String weather = today
												.getString("type");
										showWeather(weather);
										showTemp(temp);
										// code = cotent.getString("cityCode");
										 mHandler.sendEmptyMessage(GET_WEATHER_OK);
									} catch (Exception e) {
										mHandler.sendEmptyMessage(GET_INFO_ERROR);
									}
								}

							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								mHandler.sendEmptyMessage(GET_INFO_ERROR);
							}
						}, null);
				mQueue.add(getRequest);
				Looper.loop();
			}
		}).start();
	}
	
	/**
	 * 获取天气对应的图片
	 * @param weather
	 */
	public void getWeatherImg(String weather) {
		if (weather.equals("晴") || 
				weather.equals("以晴为主") || 
				weather.equals("晴间多云") ||
				weather.equals("Clear") ||
				weather.equals("Sunny") ||
				weather.equals("Mostly Sunny") ||
				weather.equals("Partly Sunny") ||
				weather.equals("Fine")) {
				mWeatherIconFlg = WEATHER_FINE;
			} else if (weather.equals("多云") || 
					   weather.equals("局部多云") || 
					   weather.equals("Mostly Cloudy") || 
					   weather.equals("Partly Cloudy") ||
					   weather.equals("Cloudy")) {
				mWeatherIconFlg = WEATHER_CLOUD;
			} else if (weather.equals("雾霾") ||
					   weather.equals("烟雾") ||
					   weather.equals("Smoke") ||
					   weather.equals("Haze")) {
				mWeatherIconFlg = WEATHER_HAZE;
			} else if (weather.equals("阴") ||
					   weather.equals("Overcast")) {
				mWeatherIconFlg = WEATHER_YIN;
			} else if (weather.equals("雪") || 
					   weather.equals("小雪") || 
					   weather.equals("中雪") ||
					   weather.equals("大雪") ||
					   weather.equals("暴雪") ||
					   weather.equals("Light snow") ||
					   weather.equals("Snow")) {
				mWeatherIconFlg = WEATHER_SNOW;
			} else if (weather.equals("雨夹雪") || 
					   weather.equals("Sleet")) {
				mWeatherIconFlg = WEATHER_YUJIAXUE;
			} else if (weather.equals("雷阵雨") || 
					   weather.equals("Thunderstorm")) {
				mWeatherIconFlg = WEATHER_LEIZHENYU;
			} else if (weather.equals("阵雨") || 
					   weather.equals("Storm")) {
				mWeatherIconFlg = WEATHER_ZHENYU;
			} else if (weather.equals("小雨") || 
					   weather.equals("可能有雨") || 
					   weather.equals("可能有暴风雨") || 
					   weather.equals("Chance of Rain") || 
					   weather.equals("Chance of Storm") || 
					   weather.equals("Light rain")) {
				mWeatherIconFlg = WEATHER_XIAOYU;
			} else if (weather.equals("中雨") || 
					   weather.equals("雨") || 
					   weather.equals("小到中雨") ||
					   weather.equals("Rain") ||
					   weather.equals("Moderate rain")) {
				mWeatherIconFlg = WEATHER_ZHONGYU;
			} else if (weather.equals("大雨") ||
					   weather.equals("中到大雨") ||
					   weather.equals("Pour")) {
				mWeatherIconFlg = WEATHER_DAYU;
			} else if (weather.equals("暴雨") || 
					   weather.equals("大到暴雨") || 
					   weather.equals("Rainstorm")) {
				mWeatherIconFlg = WEATHER_DAYU;
			} else if (weather.equals("雾") || 
					   weather.equals("Fog")) {
				mWeatherIconFlg = WEATHER_FOG;
			} else {
				mWeatherIconFlg = WEATHER_FINE;
			}
	}
}
