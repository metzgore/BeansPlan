package de.metzgore.beansplan.shared;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.metzgore.beansplan.AppExecutors;
import de.metzgore.beansplan.api.ApiResponse;
import de.metzgore.beansplan.api.RbtvScheduleApi;
import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.data.WeeklySchedule;
import de.metzgore.beansplan.util.NetworkBoundResource;

public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final String TAG = ScheduleRepositoryImpl.class.getSimpleName();

    private final RbtvScheduleApi api;
    private final ScheduleCache cache;
    private final AppExecutors appExecutors;
    private MutableLiveData<DailySchedule> scheduleCacheData = new MutableLiveData<>();
    private MutableLiveData<WeeklySchedule> weeklyScheduleCacheData = new MutableLiveData<>();

    public ScheduleRepositoryImpl(final RbtvScheduleApi api, final ScheduleCache cache, final
    AppExecutors appExecutors) {
        this.api = api;
        this.cache = cache;
        this.appExecutors = appExecutors;
    }

    @Override
    public LiveData<Resource<DailySchedule>> loadScheduleOfToday(boolean forceRefresh) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return loadScheduleOfDay(forceRefresh, year, month, day);
    }

    private LiveData<Resource<DailySchedule>> loadScheduleOfDay(boolean forceRefresh, int year,
                                                                int month, int day) {
        String formattedDay = formatDoubleDigit(day);
        String formattedMonth = formatDoubleDigit(month);

        return new NetworkBoundResource<DailySchedule, DailySchedule>(appExecutors, forceRefresh) {
            @Override
            protected void saveCallResult(@NonNull DailySchedule item) {
                //TODO check preSerialize
                item.updateTimestamp();
                cache.saveDailySchedule(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable DailySchedule schedule) {
                return forceRefresh || schedule == null || schedule.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<DailySchedule> loadFromDb() {
                scheduleCacheData.setValue(cache.getDailySchedule());
                return scheduleCacheData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<DailySchedule>> createCall() {
                return api.scheduleOfDay(year, formattedMonth, formattedDay);
            }
        }.asLiveData();
    }

    @Override
    public LiveData<Resource<WeeklySchedule>> loadWeeklySchedule(boolean forceRefresh) {
        return new NetworkBoundResource<WeeklySchedule, WeeklySchedule>(appExecutors,
                forceRefresh) {
            @Override
            protected void saveCallResult(@NonNull WeeklySchedule item) {
                //TODO check preSerialize
                item.updateTimestamp();
                cache.saveWeeklySchedule(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable WeeklySchedule schedule) {
                return forceRefresh || schedule == null || schedule.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<WeeklySchedule> loadFromDb() {
                weeklyScheduleCacheData.setValue(cache.getWeeklySchedule());
                return weeklyScheduleCacheData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<WeeklySchedule>> createCall() {
                return api.scheduleOfCurrentWeek();
            }
        }.asLiveData();
    }

    private String formatDoubleDigit(int digit) {
        return String.format(Locale.GERMANY, "%02d", digit);
    }
}
