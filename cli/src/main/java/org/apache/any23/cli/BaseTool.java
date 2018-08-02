package org.apache.any23.cli;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

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
        return (concise && (out == System.out || out == System.err)) ? new ConcisePrintStream(out)
                : (out instanceof ConcisePrintStream ? ((ConcisePrintStream) out).out : out);
    }

    private static final class ConcisePrintStream extends PrintStream {

        private PrintStream out;

        private ConcisePrintStream(PrintStream out) {
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
            }, true);
            this.out = out;
        }

    }

}
