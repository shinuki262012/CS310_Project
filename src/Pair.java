public final class Pair<V1, V2> {
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

    public Pair setFirst(V1 v) {
        first = v;
        return this;
    }

    public Pair setSecond(V2 v) {
        second = v;
        return this;
    }

}
