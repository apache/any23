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

package org.deri.any23.plugin.htmlscraper;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.Init;
import net.xeoh.plugins.base.annotations.events.Shutdown;
import net.xeoh.plugins.base.annotations.meta.Author;
import org.deri.any23.extractor.ExtractorFactory;
import org.deri.any23.plugin.ExtractorPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ExtractorPlugin} based on the
 * <a href="https://code.google.com/p/boilerpipe/">BoilerPipe Library</a>.
 *
 * @see HTMLScraperExtractor
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
@PluginImplementation
@Author(name="Michele Mostarda (mostarda@fbk.eu)")
public class HTMLScraperPlugin implements ExtractorPlugin {

    private static final Logger logger = LoggerFactory.getLogger(HTMLScraperPlugin.class);

    @Init
    public void init() {
        logger.info("Plugin initialization.");
    }

    @Shutdown
    public void shutdown() {
        logger.info("Plugin shutdown.");
    }

    public ExtractorFactory getExtractorFactory() {
        return HTMLScraperExtractor.factory;
    }
}
