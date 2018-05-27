package de.metzgore.beansplan.util.di;

import com.google.gson.Gson;
import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.Show;
import de.metzgore.beansplan.data.WeeklySchedule;
import io.gsonfire.GsonFireBuilder;

public class Injector {

    //TODO dagger
    public static Gson provideGson() {
        return new GsonFireBuilder().enableHooks(Show.class).enableHooks(DailySchedule.class)
                .enableHooks(WeeklySchedule.class).createGsonBuilder().setDateFormat
                        ("yyyy-MM-dd'T'HH:mm:ssZ").setDateFormat("dd.MM.yyyy").create();
    }
}
