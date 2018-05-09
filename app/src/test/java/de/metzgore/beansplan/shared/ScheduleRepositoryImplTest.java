package de.metzgore.beansplan.shared;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.data.WeeklySchedule;

import static org.junit.Assert.assertEquals;

public class ScheduleRepositoryImplTest {

    private ScheduleRepositoryImpl scheduleRepository;

    @Rule
    public TestRule instantTaskExecutorRole = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        //scheduleRepository = new ScheduleRepositoryImpl(new MockRbtvScheduleApi(), new MockCache
        //        (), null);
    }

    @Test
    public void loadScheduleOfToday() {
        Resource<WeeklySchedule> test = Resource.Companion.loading(null, false);

        LiveData schedule = scheduleRepository.loadWeeklySchedule(false);
        Resource<WeeklySchedule> actual = (Resource<WeeklySchedule>) schedule.getValue();
        assertEquals(test, actual);
    }

    @Test
    public void loadWeeklySchedule() {
    }
}