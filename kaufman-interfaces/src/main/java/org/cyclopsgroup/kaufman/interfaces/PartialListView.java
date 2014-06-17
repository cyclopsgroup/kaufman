package org.cyclopsgroup.kaufman.interfaces;

import java.util.List;

public interface PartialListView<T>
    extends Iterable<T>
{
    List<T> getElements();

    int getFirstElement();

    int getTotalElements();
}
