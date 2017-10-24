package de.metzgore.rbtvschedule.weeklyschedule;

import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.api.RBTVScheduleApi;
import de.metzgore.rbtvschedule.data.WeeklySchedule;
import de.metzgore.rbtvschedule.settings.repository.AppSettings;
import de.metzgore.rbtvschedule.util.Injector;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeeklySchedulePresenter implements WeeklyScheduleContract.UserActionListener {

    private static final String TAG = WeeklyScheduleContract.class.getSimpleName();

    private WeeklyScheduleContract.View mView;
    private WeeklySchedule mWeeklySchedule;
    private AppSettings mAppSettings;

    WeeklySchedulePresenter(WeeklyScheduleContract.View view, AppSettings appSettings) {
        mView = view;
        mAppSettings = appSettings;
    }

    @Override
    public void loadWeeklySchedule() {
        mView.hideSnackbar();
        mView.showRefreshIndicator(true);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        RBTVScheduleApi rbtvScheduleApi = Injector.provideRBTVScheduleApi();
        Call<WeeklySchedule> call = rbtvScheduleApi.scheduleOfCurrentWeek();

        call.enqueue(new Callback<WeeklySchedule>() {
            @Override
            public void onResponse(Call<WeeklySchedule> call, Response<WeeklySchedule> response) {
                Log.d(TAG, "received response for weekly schedule");
                Log.d(TAG, "response code: " + response.code());

                Log.d(TAG, "cache response: " + response.raw().cacheResponse());
                Log.d(TAG, "network response: " + response.raw().networkResponse());

                mWeeklySchedule = response.body();

                if (mAppSettings.getHidePastShowsSetting())
                    mWeeklySchedule.removePastShows();
                else if (mAppSettings.getHidePastDaysSetting())
                    mWeeklySchedule.removePastDays();

                mView.showRefreshIndicator(false);
                mView.showEmptyView(false);

                if (response.isSuccessful()) {
                    mView.showEmptyView(false);
                    Log.d(TAG, mWeeklySchedule.toString());
                    mView.showSchedule(mWeeklySchedule);
                } else
                    mView.showRetrySnackbar(R.string.error_message_schedule_general);
            }

            @Override
            public void onFailure(Call<WeeklySchedule> call, Throwable t) {
                Log.d(TAG, "did not receive response for weekly schedule: " + t.getMessage());
                Log.d(TAG, call.request().toString());
                mView.showRefreshIndicator(false);
                mView.showEmptyView(true);

                if (t instanceof IOException) {
                    //device is offline
                    mView.showRetrySnackbar(R.string.error_message_device_offline);
                } else {
                    //other errors
                    mView.showRetrySnackbar(R.string.error_message_schedule_general);
                }
            }
        });
    }

    @Override
    public void goToCurrentShow() {
        int idxOfCurrentDay = -1;

        if (mWeeklySchedule != null)
            idxOfCurrentDay = mWeeklySchedule.getIndexOfTodaysSchedule();

        if (idxOfCurrentDay == -1)
            mView.showToast(R.string.error_message_no_day_found);
        else
            mView.showCurrentDay(idxOfCurrentDay);
    }
}
