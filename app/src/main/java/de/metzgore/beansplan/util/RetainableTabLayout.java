package de.metzgore.beansplan.util;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

public class RetainableTabLayout extends TabLayout {

    /*
     * Variable to store invalid position.
     */
    private static final int INVALID_TAB_POS = -1;

    /*
     * Variable to store last selected position, init it with invalid tab position.
     */
    private int lastSelectedTabPosition = INVALID_TAB_POS;

    public RetainableTabLayout(Context context) {
        super(context);
    }

    public RetainableTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RetainableTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void removeAllTabs() {
        // Retain last selected position before removing all tabs
        lastSelectedTabPosition = getSelectedTabPosition();
        super.removeAllTabs();
    }

    @Override
    public int getSelectedTabPosition() {
        // Override selected tab position to return your last selected tab position
        final int selectedTabPositionAtParent = super.getSelectedTabPosition();
        return selectedTabPositionAtParent == INVALID_TAB_POS ?
                lastSelectedTabPosition : selectedTabPositionAtParent;
    }

    public void notifyDataSetChanged() {
        post(() -> {
            final TabLayout.Tab selectedTab = getTabAt(getSelectedTabPosition());
            if (selectedTab != null) {
                selectedTab.select();
            }
        });
    }
}
