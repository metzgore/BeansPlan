package de.metzgore.beansplan.util.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Injector {

    //TODO dagger
    public static Gson provideGson() {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").setDateFormat("dd.MM" +
                ".yyyy").create();
    }
}
