package com.sikdorok.system;

import java.util.Collection;

public class CollectionUtils {

    public static boolean isEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

}
