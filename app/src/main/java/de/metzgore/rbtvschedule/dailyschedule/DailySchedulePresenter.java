package de.metzgore.rbtvschedule.dailyschedule;

import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.metzgore.rbtvschedule.R;
import de.metzgore.rbtvschedule.data.RBTVScheduleApi;
import de.metzgore.rbtvschedule.data.Schedule;
import de.metzgore.rbtvschedule.util.Injector;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class DailySchedulePresenter implements DailyScheduleContract.UserActionListener {

    private static final String TAG = DailySchedulePresenter.class.getSimpleName();

    private DailyScheduleContract.View mView;

    DailySchedulePresenter(DailyScheduleContract.View view) {
        mView = view;
    }

    @Override
    public void loadDailySchedule() {
        mView.showRefreshIndicator(true);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        RBTVScheduleApi rbtvScheduleApi = Injector.provideRBTVScheduleApi();
        Call<Schedule>  call = rbtvScheduleApi.dailySchedule(
                String.valueOf(calendar.get(Calendar.YEAR)),
                //Calendar.MONTH starts at 0
                String.format("%02d", calendar.get(Calendar.MONTH)+1),
                String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));

        call.enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                Log.d(TAG, "received response for daily schedule");
                Log.d(TAG, "response code: " + response.code());

                if (response.isSuccessful()) {
                    Log.d(TAG, response.body().toString());
                    mView.showSchedule(response.body());
                } else
                    mView.showRetrySnackbar(R.string.error_message_schedule_general);
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                Log.d(TAG, "did not receive response for daily schedule");
                if (t instanceof IOException) {
                    //device is offline
                    mView.showRetrySnackbar(R.string.error_message_device_offline);
                } else {
                    //other errors
                    mView.showRetrySnackbar(R.string.error_message_schedule_general);
                }
            }
        });

        mView.showRefreshIndicator(false);
    }
}
