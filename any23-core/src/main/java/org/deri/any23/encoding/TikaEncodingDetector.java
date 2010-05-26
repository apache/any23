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

package org.deri.any23.encoding;

import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An implementation of {@link org.deri.any23.encoding.EncodingDetector} based on
 * <a href="http://tika.apache.org/">Apache Tika</a>.
 *
 * @author Michele Mostarda ( michele.mostarda@gmail.com )
 * @author Davide Palmisano ( dpalmisano@gmail.com )
 * @version $Id$
 */
public class TikaEncodingDetector implements EncodingDetector {

    public String guessEncoding(InputStream is) throws IOException {
        CharsetDetector charsetDetector = new CharsetDetector();
        charsetDetector.setText( is instanceof BufferedInputStream ? is : new BufferedInputStream(is) );
        charsetDetector.enableInputFilter(true);
        CharsetMatch cm = charsetDetector.detect();
        return cm.getName();
    }

}
