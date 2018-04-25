package de.metzgore.rbtvschedule.weeklyschedule;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.metzgore.rbtvschedule.dailyschedule.DailyScheduleFragment;
import de.metzgore.rbtvschedule.data.WeeklySchedule;
import de.metzgore.rbtvschedule.shared.UpdatableScheduleFragment;

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_WEEKDAY;

class WeeklySchedulePagerAdapter extends PagerAdapter {

    private final FragmentManager fragmentManager;
    private Context context;
    private WeeklySchedule weeklySchedule;
    private List<Date> dateKeys;
    private FragmentTransaction curTransaction = null;
    private Fragment currentPrimaryItem = null;

    WeeklySchedulePagerAdapter(Context context, FragmentManager mgr) {
        this.context = context;
        fragmentManager = mgr;
        weeklySchedule = new WeeklySchedule();
        dateKeys = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return weeklySchedule.getSchedule().size();
    }

    @Override
    public String getPageTitle(int position) {
        Date dateOfSchedule = dateKeys.get(position);
        return DateUtils.formatDateTime(context, dateOfSchedule.getTime(), FORMAT_SHOW_WEEKDAY | FORMAT_SHOW_DATE);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof UpdatableScheduleFragment) {
            UpdatableScheduleFragment fragment = (UpdatableScheduleFragment) object;
            Date dateKey = fragment.getDateKey();
            if (weeklySchedule.getSchedule().containsKey(dateKey)) {
                fragment.update(weeklySchedule.getSchedule().get(dateKey));
                return dateKeys.indexOf(dateKey);
            } else {
                return PagerAdapter.POSITION_NONE;
            }
        }
        return super.getItemPosition(object);
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id");
        }
    }

    @NonNull
    @SuppressWarnings({"ReferenceEquality", "CommitTransaction"})
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (curTransaction == null) {
            curTransaction = fragmentManager.beginTransaction();
        }

        final long itemId = getItemId(position);

        // Do we already have this fragment?
        String name = makeFragmentName(container.getId(), itemId);
        Fragment fragment = fragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            if (fragment instanceof UpdatableScheduleFragment) {
                Date dateKey = ((UpdatableScheduleFragment) fragment).getDateKey();
                ((UpdatableScheduleFragment) fragment).update(weeklySchedule.getDailySchedule(dateKey));
            }
            curTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            curTransaction.add(container.getId(), fragment,
                    makeFragmentName(container.getId(), itemId));
        }
        if (fragment != currentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    @SuppressWarnings("CommitTransaction")
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (curTransaction == null) {
            curTransaction = fragmentManager.beginTransaction();
        }

        curTransaction.detach((Fragment) object);
    }

    @SuppressWarnings("ReferenceEquality")
    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != currentPrimaryItem) {
            if (currentPrimaryItem != null) {
                currentPrimaryItem.setMenuVisibility(false);
                currentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            currentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        if (curTransaction != null) {
            curTransaction.commitNowAllowingStateLoss();
            curTransaction = null;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    public void setSchedule(WeeklySchedule weeklySchedule) {
        if (!this.weeklySchedule.equals(weeklySchedule)) {
            this.weeklySchedule = weeklySchedule;
            dateKeys = new ArrayList<>(this.weeklySchedule.getSchedule().keySet());
            notifyDataSetChanged();
        }
    }

    public int getPositionOfCurrentDay() {
        return weeklySchedule.getPositionOfCurrentDay();
    }

    public boolean containsScheduleForCurrentDay() {
        return weeklySchedule.containsScheduleForCurrentDay();
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    private long getItemId(int position) {
        return dateKeys.get(position).getTime();
    }

    private Fragment getItem(int position) {
        Date selectedDate = dateKeys.get(position);
        return DailyScheduleFragment.newInstance(selectedDate, weeklySchedule.getDailySchedule(selectedDate));
    }
}
