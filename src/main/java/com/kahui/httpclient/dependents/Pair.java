package com.kahui.httpclient.dependents;

public final class Pair<FIRST, SECOND> {
    public final FIRST first;
    public final SECOND second;

    public Pair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    public FIRST getFirst() {
        return this.first;
    }

    public SECOND getSecond() {
        return this.second;
    }

    public static <FIRST, SECOND> Pair<FIRST, SECOND> of(FIRST first, SECOND second) {
        return new Pair(first, second);
    }

    public int hashCode() {
        return 17 * (this.first != null?this.first.hashCode():0) + 17 * (this.second != null?this.second.hashCode():0);
    }

    public boolean equals(Object o) {
        if(!(o instanceof Pair)) {
            return false;
        } else {
            Pair that = (Pair)o;
            return equal(this.first, that.first) && equal(this.second, that.second);
        }
    }

    private static boolean equal(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }

    public String toString() {
        return String.format("{%s,%s}", new Object[]{this.first, this.second});
    }
}
