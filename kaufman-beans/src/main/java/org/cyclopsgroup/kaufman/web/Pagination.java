package org.cyclopsgroup.kaufman.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.cyclopsgroup.kaufman.interfaces.PartialListView;

public class Pagination<T>
    implements Iterable<T>
{
    private final int currentPage;

    private final List<Integer> displayPages;

    private final boolean firstPageDisplayed;

    private final boolean lastPageDisplayed;

    private final int pageSize;

    private final int totalPages;

    private final PartialListView<T> view;

    public Pagination( PartialListView<T> view, int pageSize )
    {
        Validate.isTrue( pageSize > 0, "Invalid page size value " + pageSize );
        Validate.notNull( view, "Partial view can't be NULL" );

        this.view = view;
        this.pageSize = pageSize;

        this.currentPage = view.getFirstElement() / pageSize;
        this.totalPages =
            (int) Math.ceil( view.getTotalElements() / (double) pageSize );

        int start = Math.max( 0, currentPage - 5 );
        int end = Math.min( totalPages, currentPage + 5 );

        this.firstPageDisplayed = start == 0;
        this.lastPageDisplayed = end == totalPages;

        List<Integer> pages = new ArrayList<Integer>();
        for ( int i = start; i < end; i++ )
        {
            pages.add( i );
        }
        this.displayPages = Collections.unmodifiableList( pages );
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    public List<Integer> getDisplayPages()
    {
        return displayPages;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public int getTotalPages()
    {
        return totalPages;
    }

    public PartialListView<T> getView()
    {
        return view;
    }

    public boolean isFirstPageDisplayed()
    {
        return firstPageDisplayed;
    }

    public boolean isLastPageDisplayed()
    {
        return lastPageDisplayed;
    }

    @Override
    public Iterator<T> iterator()
    {
        return this.view.iterator();
    }
}
