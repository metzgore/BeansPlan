package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.metzgore.rbtvschedule.api.ApiResponse;
import de.metzgore.rbtvschedule.api.RBTVScheduleApi;
import de.metzgore.rbtvschedule.data.Resource;
import de.metzgore.rbtvschedule.data.Schedule;
import de.metzgore.rbtvschedule.util.AbsentLiveData;
import de.metzgore.rbtvschedule.util.Injector;

public class ScheduleRepository {

    private final String TAG = ScheduleRepository.class.getSimpleName();

    private final RBTVScheduleApi api;

    //TODO dagger
    //@Inject
    public ScheduleRepository(/*RBTVScheduleApi api*/) {
        this.api = Injector.provideRBTVScheduleApi();
    }

    public LiveData<Resource<Schedule>> loadScheduleOfToday(boolean forceRefresh) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return loadScheduleOfDay(year, month, day);
    }

    public LiveData<Resource<Schedule>> loadScheduleOfDay(int year, int month, int day) {
        String dayOfMonth = String.format("%02d", day);

        return new NetworkBoundResource<Schedule, Schedule>() {
            @Override
            protected void saveCallResult(@NonNull Schedule item) {
                //TODO disk cache
                //repoDao.insert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Schedule data) {
                return data == null;
            }

            @NonNull
            @Override
            protected LiveData<Schedule> loadFromDb() {
                //TODO disk cache
                return AbsentLiveData.create();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Schedule>> createCall() {
                return api.scheduleOfDay(year, month, dayOfMonth);
            }
        }.asLiveData();

        /*call.enqueue(new Callback<Schedule>() {
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
        return data;*/
    }
}
