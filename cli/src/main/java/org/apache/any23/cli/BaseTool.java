/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.any23.cli;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * This class reduces the verbosity of testing command-line
 * console output by intercepting the underlying {@link PrintStream}
 * when applicable and replacing it with a more concise version.
 *
 * @author Hans Brende (hansbrende@apache.org)
 */
abstract class BaseTool implements Tool {

    abstract PrintStream getOut();
    abstract void setOut(PrintStream out);

    void run(boolean concise) throws Exception {
        PrintStream out = concise(getOut(), concise);
        setOut(out);
        try {
            run();
        } finally {
            close(out);
        }
    }

    private static void close(PrintStream stream) {
        if (stream != null && stream != System.out && stream != System.err) {
            try {
                stream.close();
            } catch (Throwable th) {
                //ignore
            }
        }
    }

    private static PrintStream concise(PrintStream out, boolean concise) {
        try {
          return (concise && (out == System.out || out == System.err)) ? new ConcisePrintStream(out)
                  : (out instanceof ConcisePrintStream ? ((ConcisePrintStream) out).out : out);
        } catch (UnsupportedEncodingException e) {
          throw new RuntimeException("Error supporting UTF-8 encodings in ConcisePrintStream", e);
        }
    }

    private static final class ConcisePrintStream extends PrintStream {

        private PrintStream out;

        private ConcisePrintStream(PrintStream out) throws UnsupportedEncodingException {
            super(new OutputStream() {
                StringBuilder sb = new StringBuilder();
                int lineCount;
                boolean truncated = false;
                @Override
                public void write(int b) throws IOException {
                    if (sb == null) {
                        throw new IOException("stream closed");
                    }
                    if (b == '\n') {
                        lineCount++;
                    }
                    if (lineCount == 0 && sb.length() < 200) {
                        sb.append((char)b);
                    } else if (!Character.isWhitespace(b)) {
                        truncated = true;
                    }
                }

                @Override
                public void close() {
                    if (sb == null) {
                        return;
                    }
                    if (truncated) {
                        sb.append("...");
                    }
                    if (lineCount > 1) {
                        sb.append("\n...\n[Suppressed ").append(lineCount).append(" lines of output.]");
                    }

                    out.println(sb);
                    sb = null;
                    BaseTool.close(out);
                }
            }, true, "UTF-8");
            this.out = out;
        }

    }

}
