package de.metzgore.rbtvschedule.util;

import com.google.gson.Gson;
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer;

import de.metzgore.rbtvschedule.data.Show;
import io.gsonfire.GsonFireBuilder;

public class GsonSerializer<T> implements CacheSerializer<T> {

    private final Gson mGson;
    private final Class<T> mClazz;

    public GsonSerializer(Class<T> clazz) {
        this.mClazz = clazz;

        this.mGson = new GsonFireBuilder()
                .enableHooks(Show.class)
                .createGsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public T fromString(String data) {
        return mGson.fromJson(data, mClazz);
    }

    @Override
    public String toString(T object) {
        return mGson.toJson(object, mClazz);
    }

}
