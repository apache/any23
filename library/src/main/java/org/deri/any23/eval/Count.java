package org.deri.any23.eval;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Count<T> extends HashMap<T, Integer> {
    long total = 0;

    public long getTotal() {
        return total;
    }

    public Count() {
        super();
    }


    public void add(T id, int value) {
//		System.out.println(id.hashCode());
        Integer i = get(id);
        if (i == null) {
            i = new Integer(0);
        }
        i += value;
        total += value;
        put(id, i);

//		if (_ht.containsKey(id)) {
//			Integer i = _ht.get(id);
//			_ht.put(id, new Integer(i.intValue() + 1));
//		} else {
//			_ht.put(id, new Integer(1));
//		}
//		System.out.println(_ht.size());
    }

    public void add(T id) {
//		System.out.println(id.hashCode());
        Integer i = get(id);
        if (i == null) {
            i = new Integer(0);
        }
        i++;
        total++;
        put(id, i);

//		if (_ht.containsKey(id)) {
//			Integer i = _ht.get(id);
//			_ht.put(id, new Integer(i.intValue() + 1));
//		} else {
//			_ht.put(id, new Integer(1));
//		}
//		System.out.println(_ht.size());
    }


    public void printStats(java.io.OutputStream out) throws IOException {
        Iterator<Map.Entry<T, Integer>> it = this.entrySet().iterator();

        for (Map.Entry<T, Integer> ent : this.entrySet()) {
            out.write((ent.getKey() + "\t" + ent.getValue() + "\n").getBytes());
            out.flush();
        }
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        Iterator<Map.Entry<T, Integer>> it = this.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<T, Integer> e = it.next();

            s.append(e.getKey() + "\t" + e.getValue() + "\n");
        }
        return s.toString();
    }

}
