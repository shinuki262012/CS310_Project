package SLP;

import java.io.Serializable;

public final class Pair<V1, V2> implements Serializable {

    public V1 first;
    public V2 second;

    public Pair() {
        first = null;
        second = null;
    }

    public Pair(V1 value1, V2 value2) {
        first = value1;
        second = value2;
    }

    public Pair<V1, V2> setFirst(V1 v) {
        first = v;
        return this;
    }

    public Pair<V1, V2> setSecond(V2 v) {
        second = v;
        return this;
    }

}
