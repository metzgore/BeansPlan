package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.metzgore.rbtvschedule.data.RBTVScheduleApi;
import de.metzgore.rbtvschedule.data.Schedule;
import de.metzgore.rbtvschedule.util.Injector;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleRepository {

    private final String TAG = ScheduleRepository.class.getSimpleName();
    final MutableLiveData<Schedule> data = new MutableLiveData<>();

    public LiveData<Schedule> loadScheduleOfToday(boolean forceRefresh) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return loadScheduleOfDay(year, month, day);
    }

    public LiveData<Schedule> loadScheduleOfDay(int year, int month, int day) {
        String dayOfMonth = String.format("%02d", day);

        RBTVScheduleApi rbtvScheduleApi = Injector.provideRBTVScheduleApi();

        Call<Schedule> call = rbtvScheduleApi.scheduleOfDay(year, month, dayOfMonth);

        call.enqueue(new Callback<Schedule>() {
            @Override
            public void onResponse(Call<Schedule> call, Response<Schedule> response) {
                Log.d(TAG, "received response for weekly schedule");
                Log.d(TAG, "response code: " + response.code());

                Log.d(TAG, "cache response: " + response.raw().cacheResponse());
                Log.d(TAG, "network response: " + response.raw().networkResponse());

                Schedule schedule = response.body();
                Log.d(TAG, "body: " + schedule.toString());

                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Schedule> call, Throwable t) {
                Log.d(TAG, "did not receive response for weekly schedule: " + t.getMessage());
                Log.d(TAG, call.request().toString());
            }
        });
        return data;
    }
}
