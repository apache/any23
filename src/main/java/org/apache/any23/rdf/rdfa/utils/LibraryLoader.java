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
package org.apache.any23.rdf.rdfa.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Julio Caguano
 *
 */
public final class LibraryLoader {

    public static void loadLibrary(String name) throws IOException {
        try {
            System.loadLibrary(name);
        } catch (UnsatisfiedLinkError e) {
            String filename = System.mapLibraryName(name);
            InputStream in = LibraryLoader.class.getClassLoader().getResourceAsStream(filename);
            int pos = filename.lastIndexOf('.');
            File file = File.createTempFile(filename.substring(0, pos), filename.substring(pos));
            file.deleteOnExit();
            try {
                byte[] buf = new byte[4096];
                OutputStream out = new FileOutputStream(file);
                try {
                    while (in.available() > 0) {
                        int len = in.read(buf);
                        if (len >= 0) {
                            out.write(buf, 0, len);
                        }
                    }
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
            System.load(file.getAbsolutePath());
        }
    }

}
