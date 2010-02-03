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

package org.deri.any23.source;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Creates local copies of {@link org.deri.any23.source.DocumentSource} by
 * reading them into an in-memory buffer. This allows opening
 * several input streams over the content at lower cost.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class MemCopyFactory implements LocalCopyFactory {

    private static final int TEMP_SIZE = 10000;

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] temp = new byte[TEMP_SIZE];
        while (true) {
            int bytes = in.read(temp);
            if (bytes == -1) break;
            out.write(temp, 0, bytes);
        }
        return out.toByteArray();
    }

    public DocumentSource createLocalCopy(final DocumentSource in) throws IOException {
        return new ByteArrayDocumentSource(in.openInputStream(), in.getDocumentURI(), in.getContentType());
    }
}
