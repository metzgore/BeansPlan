package de.metzgore.rbtvschedule.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer;

public class GsonSerializer<T> implements CacheSerializer<T> {

    private final Gson mGson;
    private final Class<T> mClazz;

    public GsonSerializer(Class<T> clazz) {
        this.mClazz = clazz;

        GsonBuilder gsonBuilder = new GsonBuilder()
                .setPrettyPrinting();
        
        this.mGson = gsonBuilder.create();
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
