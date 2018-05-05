package de.metzgore.rbtvschedule.shared;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.inject.Singleton;

import de.metzgore.rbtvschedule.AppExecutors;
import de.metzgore.rbtvschedule.RBTVScheduleApp;
import de.metzgore.rbtvschedule.api.ApiResponse;
import de.metzgore.rbtvschedule.api.RBTVScheduleApi;
import de.metzgore.rbtvschedule.data.DailySchedule;
import de.metzgore.rbtvschedule.data.Resource;
import de.metzgore.rbtvschedule.data.WeeklySchedule;
import de.metzgore.rbtvschedule.util.GsonSerializer;
import de.metzgore.rbtvschedule.util.NetworkBoundResource;
import de.metzgore.rbtvschedule.util.di.Injector;

@Singleton
public class ScheduleRepository {

    private final String TAG = ScheduleRepository.class.getSimpleName();
    private static final String DAILY_SCHEDULE_KEY = "daily_schedule_key";
    private static final String WEEKLY_SCHEDULE_KEY = "weekly_schedule_key";

    private final RBTVScheduleApi api;
    private final AppExecutors appExecutors;
    private DualCache<DailySchedule> scheduleCache;
    private DualCache<WeeklySchedule> weeklyScheduleCache;
    private MutableLiveData<DailySchedule> scheduleCacheData = new MutableLiveData<>();
    private MutableLiveData<WeeklySchedule> weeklyScheduleCacheData = new MutableLiveData<>();

    //TODO dagger
    //@Inject
    public ScheduleRepository(/*RBTVScheduleApi api, AppExecutors appExecutors*/) {
        api = Injector.provideRBTVScheduleApi();
        this.appExecutors = new AppExecutors();

        scheduleCache = new Builder<DailySchedule>(DAILY_SCHEDULE_KEY, 1)
                .enableLog()
                .useSerializerInRam(1000000, new GsonSerializer<>(DailySchedule.class))
                .useSerializerInDisk(1000000, true, new GsonSerializer<>(DailySchedule.class), RBTVScheduleApp.getAppContext())
                .build();

        weeklyScheduleCache = new Builder<WeeklySchedule>(WEEKLY_SCHEDULE_KEY, 1)
                .enableLog()
                .useSerializerInRam(1000000, new GsonSerializer<>(WeeklySchedule.class))
                .useSerializerInDisk(1000000, true, new GsonSerializer<>(WeeklySchedule.class), RBTVScheduleApp.getAppContext())
                .build();
    }

    public LiveData<Resource<DailySchedule>> loadScheduleOfToday(boolean forceRefresh) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return loadScheduleOfDay(forceRefresh, year, month, day);
    }

    private LiveData<Resource<DailySchedule>> loadScheduleOfDay(boolean forceRefresh, int year, int month, int day) {
        String formattedDay = formatDoubleDigit(day);
        String formattedMonth = formatDoubleDigit(month);

        return new NetworkBoundResource<DailySchedule, DailySchedule>(appExecutors, forceRefresh) {
            @Override
            protected void saveCallResult(@NonNull DailySchedule item) {
                //TODO check preSerialize
                item.updateTimestamp();
                scheduleCache.put(DAILY_SCHEDULE_KEY, item);
            }

            @Override
            protected boolean shouldFetch(@Nullable DailySchedule schedule) {
                return forceRefresh || schedule == null || schedule.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<DailySchedule> loadFromDb() {
                scheduleCacheData.setValue(scheduleCache.get(DAILY_SCHEDULE_KEY));
                return scheduleCacheData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<DailySchedule>> createCall() {
                return api.scheduleOfDay(year, formattedMonth, formattedDay);
            }
        }.asLiveData();
    }

    public LiveData<Resource<WeeklySchedule>> loadWeeklySchedule(boolean forceRefresh) {
        return new NetworkBoundResource<WeeklySchedule, WeeklySchedule>(appExecutors, forceRefresh) {
            @Override
            protected void saveCallResult(@NonNull WeeklySchedule item) {
                //TODO check preSerialize
                item.updateTimestamp();
                weeklyScheduleCache.put(WEEKLY_SCHEDULE_KEY, item);
            }

            @Override
            protected boolean shouldFetch(@Nullable WeeklySchedule schedule) {
                return forceRefresh || schedule == null || schedule.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<WeeklySchedule> loadFromDb() {
                weeklyScheduleCacheData.setValue(weeklyScheduleCache.get(WEEKLY_SCHEDULE_KEY));
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
