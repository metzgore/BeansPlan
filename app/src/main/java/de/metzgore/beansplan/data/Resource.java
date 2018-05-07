package de.metzgore.beansplan.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static de.metzgore.beansplan.data.Status.ERROR;
import static de.metzgore.beansplan.data.Status.LOADING;
import static de.metzgore.beansplan.data.Status.SUCCESS;

public class Resource<T> {
    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;

    public final boolean forceRefresh;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message, boolean forceRefresh) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.forceRefresh = forceRefresh;
    }

    public static <T> Resource<T> success(@NonNull T data, boolean refreshForced) {
        return new Resource<>(SUCCESS, data, null, refreshForced);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data, boolean refreshForced) {
        return new Resource<>(ERROR, data, msg, refreshForced);
    }

    public static <T> Resource<T> loading(@Nullable T data, boolean refreshForced) {
        return new Resource<>(LOADING, data, null, refreshForced);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resource<?> resource = (Resource<?>) o;

        if (status != resource.status) {
            return false;
        }
        if (message != null ? !message.equals(resource.message) : resource.message != null) {
            return false;
        }
        if (forceRefresh != resource.forceRefresh)
            return false;
        return data != null ? data.equals(resource.data) : resource.data == null;
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", forceRefresh='" + forceRefresh + '\'' +
                ", data=" + data +
                '}';
    }
}
