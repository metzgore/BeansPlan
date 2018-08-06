package de.metzgore.beansplan.shared;

import java.util.Date;

public interface UpdatableScheduleFragment {

    void update(Date date);

    Date getDateKey();
}
