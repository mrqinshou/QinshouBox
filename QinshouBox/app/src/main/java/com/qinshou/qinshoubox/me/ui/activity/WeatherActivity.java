package com.qinshou.qinshoubox.me.ui.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.qinshou.commonmodule.util.SharedPreferencesUtil;
import com.qinshou.qinshoubox.R;
import com.qinshou.qinshoubox.base.MyBaseActivity;
import com.qinshou.qinshoubox.me.bean.CityBean;
import com.qinshou.qinshoubox.me.bean.DistrictBean;
import com.qinshou.qinshoubox.me.bean.ProvinceBean;
import com.qinshou.qinshoubox.me.bean.WeatherBean;
import com.qinshou.qinshoubox.me.constant.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.flutter.app.FlutterActivity;
import io.flutter.facade.Flutter;
import io.flutter.view.FlutterMain;


/**
 * Description:天气界面
 * Created by 禽兽先生
 * Created on 2018/11/8
 */
public class WeatherActivity extends MyBaseActivity {
    private ViewGroup rootView;
    private List<ProvinceBean> provinceList = new ArrayList<>();
    private List<List<CityBean>> cityList = new ArrayList<>();
    private List<List<List<DistrictBean>>> districtList = new ArrayList<>();
    private ImageButton ibChooseCity;
    private TextView tvCity;
    private ImageButton ibShare;
    private TextView tvTemperature;
    private TextView tvWeather;
    private TextView tvUpdateTime;
    private TextView tvWeek;
    private ImageView ivAirCondition;
    private TextView tvAirCondition;
    private TextView tvWind;
    private TextView tvHumidity;
    private TextView tvSunrise;
    private TextView tvSunset;
    private TextView tvWashIndex;
    private TextView tvExerciseIndex;
    private TextView tvColdIndex;
    private TextView tvDressingIndex;
    private WeatherBean mWeatherBean;

    @Override
    public boolean getIsImmersive() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_weather;
    }

    @Override
    public void setPresenter() {

    }

    @Override
    public void initView() {
        unbindSlideBackActivity();
        View flutterView = Flutter.createView(getActivity(), getActivity().getLifecycle(), "WeatherView");
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        addContentView(flutterView, layoutParams);

//        rootView = findViewByID(R.id.root_view);
//        final LinearLayout linearLayout = findViewByID(R.id.linear_layout_1);
//        linearLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                StatusBarUtil.appendStatusBarPadding(linearLayout, linearLayout.getMeasuredHeight());
//            }
//        });
//        ibChooseCity = findViewByID(R.id.ib_choose_city);
//        addNotInterceptView(ibChooseCity);
//        tvCity = findViewByID(R.id.tv_city);
//        ibShare = findViewByID(R.id.ib_share);
//        tvTemperature = findViewByID(R.id.tv_temperature);
//        tvWeather = findViewByID(R.id.tv_weather);
//        tvUpdateTime = findViewByID(R.id.tv_update_time);
//        tvWeek = findViewByID(R.id.tv_week);
//        ivAirCondition = findViewByID(R.id.iv_air_condition);
//        tvAirCondition = findViewByID(R.id.tv_air_condition);
//        tvWind = findViewByID(R.id.tv_wind);
//        tvHumidity = findViewByID(R.id.tv_humidity);
//        tvSunrise = findViewByID(R.id.tv_sunrise);
//        tvSunset = findViewByID(R.id.tv_sunset);
//        tvWashIndex = findViewByID(R.id.tv_wash_index);
//        tvExerciseIndex = findViewByID(R.id.tv_exercise_index);
//        tvColdIndex = findViewByID(R.id.tv_cold_index);
//        tvDressingIndex = findViewByID(R.id.tv_dressing_index);
    }

    @Override
    public void setListener() {
//        ibChooseCity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showCityPickerView();
//            }
//        });
//        ibShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShareUtil.showShare(getContext());
//            }
//        });
    }

    @Override
    public void initData() {
//        String ip = SystemUtil.getIPAddress(getActivity());
//        MobApi.getInstance().queryWeatherByIp(ip, new BaseObserver<List<WeatherBean>>() {
//            @Override
//            public void onNext(List<WeatherBean> value) {
//                if (value.size() > 0) {
//                    updateUi(value.get(0));
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                loadData(null, SharedPreferencesUtil.getString(getContext(), Constant.LAST_CHOOSE_CITY));
//                ShowLogUtil.logi("ip--->" + e.getMessage());
//            }
//        });
//        MobApi.getInstance().queryCityList(new BaseObserver<List<ProvinceBean>>() {
//            @Override
//            public void onNext(List<ProvinceBean> value) {
//                provinceList.clear();
//                cityList.clear();
//                districtList.clear();
//
//                provinceList = value;
//                for (ProvinceBean provinceBean : value) {
//                    cityList.add(provinceBean.getCityList());
//                    List<List<DistrictBean>> list = new ArrayList<>();
//                    for (CityBean cityBean : provinceBean.getCityList()) {
//                        list.add(cityBean.getDistrictList());
//                    }
//                    districtList.add(list);
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                ShowLogUtil.logi(e.getMessage());
//            }
//        });
    }

    private void loadData(String province, String city) {
//        MobApi.getInstance().queryWeatherByCity(province, city, new BaseObserver<List<WeatherBean>>() {
//            @Override
//            public void onNext(List<WeatherBean> value) {
//                if (value.size() > 0) {
//                    updateUi(value.get(0));
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                ShowLogUtil.logi(e.getMessage());
//            }
//        });
    }

    /**
     * Description:显示城市选择框
     * Date:2018/11/13
     */
    private void showCityPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String string = provinceList.get(options1).getName()
                        + cityList.get(options1).get(options2).getName()
                        + districtList.get(options1).get(options2).get(options3).getName();
                loadData(provinceList.get(options1).getName(), districtList.get(options1).get(options2).get(options3).getName());
            }
        }).setDecorView(rootView)
                .build();
        pvOptions.setPicker(provinceList, cityList, districtList);
        pvOptions.show();
    }

    private void updateUi(WeatherBean weatherBean) {
        mWeatherBean = weatherBean;
        SharedPreferencesUtil.putString(getContext(), Constant.LAST_CHOOSE_CITY, weatherBean.getCity());
        rootView.setBackgroundResource(getBackground(weatherBean.getWeather()));
        tvCity.setText(weatherBean.getCity());
        tvTemperature.setText(weatherBean.getTemperature());
        tvWeather.setText("天气：" + weatherBean.getWeather());
        StringBuilder updateTime = new StringBuilder();
        updateTime.append(weatherBean.getUpdateTime().substring(0, 4))
                .append("-")
                .append(weatherBean.getUpdateTime().substring(4, 6))
                .append("-")
                .append(weatherBean.getUpdateTime().substring(6, 8))
                .append(" ")
                .append(weatherBean.getUpdateTime().substring(8, 10))
                .append(":")
                .append(weatherBean.getUpdateTime().substring(10, 12))
                .append(":")
                .append(weatherBean.getUpdateTime().substring(12, weatherBean.getUpdateTime().length()));
        tvUpdateTime.setText(updateTime.toString());
        tvWeek.setText(weatherBean.getWeek());
        ivAirCondition.setImageResource(getAirConditionIvSrc(weatherBean.getPollutionIndex()));
        tvAirCondition.setText("空气指数：" + weatherBean.getAirCondition());
        tvWind.setText("风向：" + weatherBean.getWind());
        tvHumidity.setText(weatherBean.getHumidity());
        tvSunrise.setText("日出时间：" + weatherBean.getSunrise());
        tvSunset.setText("日落时间：" + weatherBean.getSunset());
        tvWashIndex.setText("洗车指数：" + weatherBean.getWashIndex());
        tvExerciseIndex.setText("运动指数：" + weatherBean.getExerciseIndex());
        tvColdIndex.setText("感冒指数：" + weatherBean.getColdIndex());
        tvDressingIndex.setText("穿衣指数：" + weatherBean.getDressingIndex());
    }

    /**
     * author：MrQinshou
     * Description:根据空气指数得到对应图片
     * date:2018/11/13 21:04
     * param
     * return
     */
    private int getAirConditionIvSrc(int pollutionIndex) {
        if (pollutionIndex > 300) {
            return R.drawable.weather_air_condition_iv_src_7;
        } else if (pollutionIndex > 250) {
            return R.drawable.weather_air_condition_iv_src_6;
        } else if (pollutionIndex > 200) {
            return R.drawable.weather_air_condition_iv_src_5;
        } else if (pollutionIndex > 150) {
            return R.drawable.weather_air_condition_iv_src_4;
        } else if (pollutionIndex > 100) {
            return R.drawable.weather_air_condition_iv_src_3;
        } else if (pollutionIndex > 50) {
            return R.drawable.weather_air_condition_iv_src_2;
        } else {
            return R.drawable.weather_air_condition_iv_src_1;
        }
    }

    /**
     * author：MrQinshou
     * Description:根据天气得到对应背景
     * date:2018/11/13 21:04
     * param
     * return
     */
    private int getBackground(String weather) {
        //获得当前时间
        int currentHour = Integer.parseInt(new SimpleDateFormat("HH", Locale.CHINA).format(new Date(System.currentTimeMillis())));
        if (weather.contains("晴")) {
            if (currentHour >= 6 && currentHour < 18) {
                return R.drawable.weather_bg_sunny_day;
            } else {
                return R.drawable.weather_bg_sunny_night;
            }
        } else if (weather.contains("雨")) {
            if (currentHour >= 6 && currentHour < 18) {
                return R.drawable.weather_bg_rain_day;
            } else {
                return R.drawable.weather_bg_rain_night;
            }
        } else if (weather.contains("雪")) {
            if (currentHour >= 6 && currentHour < 18) {
                return R.drawable.weather_bg_snow_day;
            } else {
                return R.drawable.weather_bg_snow_night;
            }
        } else if (weather.contains("阴")) {
            if (currentHour >= 6 && currentHour < 18) {
                return R.drawable.weather_bg_overcast_day;
            } else {
                return R.drawable.weather_bg_overcast_night;
            }
        } else if (weather.contains("雾")) {
            if (currentHour >= 6 && currentHour < 18) {
                return R.drawable.weather_bg_fog_day;
            } else {
                return R.drawable.weather_bg_fog_night;
            }
        } else if (weather.contains("霾")) {
            return R.drawable.weather_bg_fog_and_haze;
        }
        // N/A,not available
        return R.drawable.weather_bg_na;
    }

//    private void showFuture(WeatherBean weatherBean) {
//        if (weatherBean == null) {
//            return;
//        }
//        List<Label> xLabelList = new ArrayList<>();
//        List<Label> yLabelList = new ArrayList<>();
//        List<Integer> highestList = new ArrayList<>();
//        List<Integer> lowestList = new ArrayList<>();
//        List<String> dayTimeWeatherList = new ArrayList<>();
//        for (int i = 0; i < weatherBean.getFuture().size(); i++) {
//            dayTimeWeatherList.add(weatherBean.getFuture().get(i).getDayTime());
//            String temperature = weatherBean.getFuture().get(i).getTemperature();
//            String[] temperatureArray = temperature.split("/");
//            if (temperatureArray.length > 1) {
//                highestList.add(Integer.valueOf(temperatureArray[0].trim().replace("°C", "")));
//                lowestList.add(Integer.valueOf(temperatureArray[1].trim().replace("°C", "")));
//            } else if (temperatureArray.length > 0) {
//                highestList.add(Integer.valueOf(temperatureArray[0].trim().replace("°C", "")));
//                lowestList.add(Integer.valueOf(temperatureArray[0].trim().replace("°C", "")));
//            } else {
//                highestList.add(0);
//                lowestList.add(0);
//            }
//            xLabelList.add(new Label(weatherBean.getFuture().get(i).getWeek()));
//        }
//        ShowLogUtil.logi(xLabelList);
//        ShowLogUtil.logi(highestList);
//        ShowLogUtil.logi(lowestList);
//        ShowLogUtil.logi(dayTimeWeatherList);
//        View view = new DialogUtil.Builder(getContext())
//                .setView(R.layout.ppw_weather_future)
//                .create()
//                .showCustomDialog();
//
////        RecyclerView rvFuture = view.findViewById(R.id.rv_future);
//        MultipleLineChartView multipleLineChartView = view.findViewById(R.id.multiple_line_chart_view);
//        LineList lineList = new LineList();
//        DataLine highestLine = new DataLine();
//        highestLine.setType(DataLine.Type.BEZIER);
//        for (int i = 0; i < highestList.size(); i++) {
//            highestLine.addPoint(new DataPoint(i, highestList.get(i)));
//        }
//        lineList.addLine(highestLine);
//
//        DataLine lowestLine = new DataLine();
//        lowestLine.setType(DataLine.Type.BEZIER);
//        for (int i = 0; i < lowestList.size(); i++) {
//            lowestLine.addPoint(new DataPoint(i, lowestList.get(i)));
//        }
//        lineList.addLine(lowestLine);
//
//        multipleLineChartView.setAnimateDraw(true);
//        multipleLineChartView.getXAxis().setLabelList(xLabelList);
//        for (int i = Collections.max(highestList); i > Collections.min(lowestList) ; i--) {
//            yLabelList.add(new Label(i + "°C"));
//        }
//        multipleLineChartView.getYAxis().setLabelList(yLabelList);
//        multipleLineChartView.setLineList(lineList);
//        multipleLineChartView.setMax(Collections.max(highestList) + 1);
//        multipleLineChartView.setMin(Collections.min(lowestList) - 1);
//    }
}
