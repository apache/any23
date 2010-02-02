package org.deri.any23.eval;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple counter class.
 */
class Count<T> extends HashMap<T, Integer> {

    /**
     * The total.
     */
    private long total = 0;

    public Count() {
        super();
    }

    public long getTotal() {
        return total;
    }

    protected void add(T id, int value) {
        Integer i = get(id);
        if (i == null) {
            i = 0;
        }
        i += value;
        total += value;
        put(id, i);
    }

    protected void add(T id) {
        Integer i = get(id);
        if (i == null) {
            i = 0;
        }
        i++;
        total++;
        put(id, i);
    }


    protected void printStats(java.io.OutputStream out) throws IOException {
        for (Map.Entry<T, Integer> ent : this.entrySet()) {
            out.write((ent.getKey() + "\t" + ent.getValue() + "\n").getBytes());
            out.flush();
        }
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        for (Map.Entry<T, Integer> tIntegerEntry : this.entrySet()) {
            s.append(tIntegerEntry.getKey()).append("\t").append(tIntegerEntry.getValue()).append("\n");
        }
        return s.toString();
    }

}
