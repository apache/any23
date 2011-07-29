/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deri.any23.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Utility class for handling files.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class FileUtils {

    /**
     * Moves a <code>target</code> file to a new <code>dest</code> location.
     *
     * @param target file to be moved.
     * @param dest   dest dir.
     * @return destination file.
     */
    public static File mv(File target, File dest) {
        if (!dest.isDirectory()) {
            throw new IllegalArgumentException("destination must be a directory.");
        }

        final File newFile = new File(dest, target.getName());
        boolean success = target.renameTo(newFile);
        if (!success) {
            throw new IllegalStateException(
                    String.format("Cannot move target file [%s] to destination [%s]", target, newFile)
            );
        }
        return newFile;
    }

    /**
     * Copies the content of the input stream within the given dest file.
     * The dest file must not exist.
     *
     * @param is
     * @param dest
     */
    public static void cp(InputStream is, File dest) {
        if (dest.exists()) {
            throw new IllegalArgumentException("Destination must not exist.");
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(dest);
            bos = new BufferedOutputStream(fos);
            final byte[] buffer = new byte[1024 * 4];
            int read;
            while (true) {
                read = bis.read(buffer);
                if (read == -1) {
                    break;
                }
                bos.write(buffer, 0, read);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while copying stream into file.", e);
        } finally {
            StreamUtils.closeGracefully(bis);
            StreamUtils.closeGracefully(bos);
        }
    }

    /**
     * Copies a file <code>src</code> to the <code>dest</code>.
     *
     * @param src  source file.
     * @param dest destination file.
     * @throws java.io.FileNotFoundException if file cannot be copied or created.
     */
    public static void cp(File src, File dest) throws FileNotFoundException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(src);
            cp(fis, dest);
        } finally {
            StreamUtils.closeGracefully(fis);
        }
    }

    /**
     * Dumps the given string within a file.
     *
     * @param f       file target.
     * @param content content to be dumped.
     * @throws IOException
     */
    public static void dumpContent(File f, String content) throws IOException {
        FileWriter fw = new FileWriter(f);
        try {
            fw.write(content);
        } finally {
            StreamUtils.closeGracefully(fw);
        }
    }

    /**
     * Dumps the stack trace of the given exception into the specified file.
     *
     * @param f file to generate dump.
     * @param t exception to be dumped.
     * @throws IOException
     */
    public static void dumpContent(File f, Throwable t) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter pw = new PrintWriter(baos);
        t.printStackTrace(pw);
        pw.close();
        dumpContent(f, baos.toString());
    }

    /**
     * Reads a resource file and returns the content as a string.
     *
     * @param clazz    the class to use load the resource.
     * @param resource the resource to be load.
     * @return the string representing the file content.
     * @throws java.io.IOException
     */
    public static String readResourceContent(Class clazz, String resource) throws IOException {
        return StreamUtils.asString( clazz.getResourceAsStream(resource) );
    }

    /**
     * Reads a resource file and returns the content as a string.
     *
     * @param resource the resource to be load.
     * @return the string representing the file content.
     * @throws java.io.IOException
     */
    public static String readResourceContent(String resource) throws IOException {
        return readResourceContent(FileUtils.class, resource);
    }

    /**
     * Reads the content of a file and return it in a string.
     *
     * @param f the file to read.
     * @return the content of file.
     * @throws IOException if an exception occurs while locating or accessing the file.
     */
    public static String readFileContent(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        return StreamUtils.asString(fis);
    }

    /**
     * Function class.
     */
    private FileUtils() {}

}
