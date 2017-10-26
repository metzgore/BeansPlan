package de.metzgore.rbtvschedule.dailyschedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.inject.Singleton;

import de.metzgore.rbtvschedule.AppExecutors;
import de.metzgore.rbtvschedule.RBTVScheduleApp;
import de.metzgore.rbtvschedule.api.ApiResponse;
import de.metzgore.rbtvschedule.api.RBTVScheduleApi;
import de.metzgore.rbtvschedule.data.Resource;
import de.metzgore.rbtvschedule.data.Schedule;
import de.metzgore.rbtvschedule.util.GsonSerializer;
import de.metzgore.rbtvschedule.util.Injector;

@Singleton
public class ScheduleRepository {

    private final String TAG = ScheduleRepository.class.getSimpleName();
    private final RBTVScheduleApi api;
    private final AppExecutors appExecutors;
    private DualCache<Schedule> cache;
    private MutableLiveData<Schedule> cacheData = new MutableLiveData<>();

    //TODO dagger
    //@Inject
    public ScheduleRepository(/*RBTVScheduleApi api, AppExecutors appExecutors*/) {
        api = Injector.provideRBTVScheduleApi();
        this.appExecutors = new AppExecutors();
        cache = new Builder<Schedule>("test", 1)
                .enableLog()
                .useSerializerInRam(10000, new GsonSerializer<>(Schedule.class))
                .useSerializerInDisk(10000, true, new GsonSerializer<>(Schedule.class), RBTVScheduleApp.getAppContext())
                .build();
    }

    public LiveData<Resource<Schedule>> loadScheduleOfToday(boolean forceRefresh) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return loadScheduleOfDay(forceRefresh, year, month, day);
    }

    public LiveData<Resource<Schedule>> loadScheduleOfDay(boolean forceRefresh, int year, int month, int day) {
        String dayOfMonth = String.format("%02d", day);

        return new NetworkBoundResource<Schedule, Schedule>(appExecutors, forceRefresh) {
            @Override
            protected void saveCallResult(@NonNull Schedule item) {
                //TODO disk cache
                cache.put("test", item);
                //repoDao.insert(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Schedule data) {
                return forceRefresh;
            }

            @NonNull
            @Override
            protected LiveData<Schedule> loadFromDb() {
                cacheData.setValue(cache.get("test"));
                return cacheData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Schedule>> createCall() {
                return api.scheduleOfDay(year, month, dayOfMonth);
            }
        }.asLiveData();
    }
}
