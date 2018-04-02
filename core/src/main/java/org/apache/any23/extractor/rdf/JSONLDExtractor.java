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

package org.apache.any23.extractor.rdf;

import com.github.jsonldjava.utils.JarCacheStorage;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.any23.extractor.ExtractionContext;
import org.apache.any23.extractor.ExtractionResult;
import org.apache.any23.extractor.ExtractorDescription;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.cache.BasicHttpCacheStorage;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClientBuilder;
import org.eclipse.rdf4j.rio.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

/**
 * Concrete implementation of {@link org.apache.any23.extractor.Extractor.ContentExtractor}
 * handling <a href="http://www.w3.org/TR/json-ld/">JSON-LD</a> format.
 *
 */
public class JSONLDExtractor extends BaseRDFExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    //TODO: the static members of this class can be removed once jsonldjava 0.11.2 is released
    //See https://issues.apache.org/jira/browse/ANY23-336
    static final boolean needsHttpClientSwap;

    static {
        if (!(needsHttpClientSwap = isHttpClientSwapNeeded())) {
            LOG.warn("The static members of this class are no longer needed.");
        } else {
            try {
                Field field = JsonUtils.class.getDeclaredField("DEFAULT_HTTP_CLIENT");
                field.setAccessible(true);
                field.set(null, createDefaultHttpClient());
            } catch (Throwable e) {
                LOG.warn("failed to swap jsonldjava http client", e);
            }
        }
    }

    private static boolean isHttpClientSwapNeeded() {
        try {
            JsonUtils.class.getDeclaredField("JSONLD_JAVA_USER_AGENT");
            return false;
        } catch (Throwable th) {
            return true;
        }
    }

    private static CloseableHttpClient createDefaultHttpClient() {
        // Common CacheConfig for both the JarCacheStorage and the underlying
        // BasicHttpCacheStorage
        final CacheConfig cacheConfig = CacheConfig.custom().setMaxCacheEntries(500)
                .setMaxObjectSize(1024 * 256).setSharedCache(false)
                .setHeuristicCachingEnabled(true).setHeuristicDefaultLifetime(86400).build();

        final CloseableHttpClient result = CachingHttpClientBuilder.create()
                // allow caching
                .setCacheConfig(cacheConfig)
                // Wrap the local JarCacheStorage around a BasicHttpCacheStorage
                .setHttpCacheStorage(new JarCacheStorage0(null, cacheConfig,
                        new BasicHttpCacheStorage(cacheConfig)))
                // Support compressed data
                // https://wayback.archive.org/web/20130901115452/http://hc.apache.org:80/httpcomponents-client-ga/tutorial/html/httpagent.html#d5e1238
                .addInterceptorFirst(new RequestAcceptEncoding())
                .addInterceptorFirst(new ResponseContentEncoding())
                .setRedirectStrategy(DefaultRedirectStrategy.INSTANCE)
                // use system defaults for proxy etc.
                .useSystemProperties().build();

        return result;
    }

    private static class JarCacheStorage0 extends JarCacheStorage {

        private final HttpCacheStorage delegate;

        public JarCacheStorage0(ClassLoader classLoader, CacheConfig cacheConfig,
                               HttpCacheStorage delegate) {
            super(classLoader, cacheConfig, delegate);
            this.delegate = delegate;
        }

        @Override
        public HttpCacheEntry getEntry(String key) throws IOException {
            HttpCacheEntry entry = delegate.getEntry(key);
            return entry != null ? entry : super.getEntry(key);
        }
    }





    public JSONLDExtractor(boolean verifyDataType, boolean stopAtFirstError) {
        super(verifyDataType, stopAtFirstError);
    }

    public JSONLDExtractor() {
        this(false, false);
    }

    @Override
    public ExtractorDescription getDescription() {
        return JSONLDExtractorFactory.getDescriptionInstance();
    }

    @Override
    protected RDFParser getParser(ExtractionContext extractionContext, ExtractionResult extractionResult) {
        return RDFParserFactory.getInstance().getJSONLDParser(
                isVerifyDataType(), isStopAtFirstError(), extractionContext, extractionResult
        );
    }
}
