/*
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.any23.extractor.yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.utils.IOUtils;

/**
 * Yaml file utility class
 * 
 * @author Jacek Grzebyta (grzebyta.dev [at] gmail.com)
 */
public class YamlUtils {

	/**
	 * Counts number of yaml documents in file. See more at
	 * <a href="http://www.yaml.org/spec/1.2/spec.html#id2800132">YAML specification</a>.
	 * 
	 * @param is
	 * @return
	 */
	public static int countDocuments(InputStream is) {
		return 0;
	}

	public static byte[] toBytes(InputStream is)
		throws IOException
	{
		return IOUtils.toByteArray(is);
	}

	public static InputStream toInputStream(byte[] array) {
		return new ByteArrayInputStream(array);
	}
}
