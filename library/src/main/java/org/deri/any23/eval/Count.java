/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
