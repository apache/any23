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

package org.deri.any23.vocab;

import org.deri.any23.parser.NQuadsWriter;
import org.deri.any23.rdf.RDFUtils;
import org.deri.any23.util.DiscoveryUtils;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * This class provides a set of methods for generating
 * <a href="http://www.w3.org/TR/rdf-schema/">RDF Schema</a>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class RDFSchemaUtils {

    /**
     * Serializes a vocabulary composed of the given <code>namespace</code>,
     * <code>resources</code> and <code>properties</code>.
     *
     * @param namespace vocabulary namespace.
     * @param resources list of resources.
     * @param properties list of properties.
     * @param writer writer to print out the RDF Schema triples.
     * @throws RDFHandlerException
     */
    public static void serializeVocabulary(
            URI namespace,
            URI[] resources,
            URI[] properties,
            RDFWriter writer
    ) throws RDFHandlerException {
        writer.startRDF();
        for(URI entity : resources) {
            writer.handleStatement( RDFUtils.quad(entity, RDF.TYPE, RDFS.RESOURCE, namespace) );
            writer.handleStatement( RDFUtils.quad(entity, RDFS.MEMBER, namespace, namespace)  );
        }
        for(URI property : properties) {
            writer.handleStatement(RDFUtils.quad(property, RDF.TYPE, RDF.PROPERTY, namespace));
            writer.handleStatement(RDFUtils.quad(property, RDFS.MEMBER, namespace, namespace));
        }
        writer.endRDF();
    }

    /**
     * Serializes the given <code>vocabulary</code> to triples over the given <code>writer</code>.
     *
     * @param vocabulary vocabulary to be serialized.
     * @param writer output writer.
     * @throws RDFHandlerException
     */
    public static void serializeVocabulary(Vocabulary vocabulary, RDFWriter writer)
    throws RDFHandlerException {
        serializeVocabulary(
                vocabulary.getNamespace(),
                vocabulary.getResources(),
                vocabulary.getProperties(),
                writer
        );
    }

    /**
     * Serializes the given <code>vocabulary</code> to <i>NQuads</i> over the given output stream.
     *
     * @param vocabulary vocabulary to be serialized.
     * @param os output stream.
     * @throws RDFHandlerException
     */
    public static void serializeVocabularyToNQuads(Vocabulary vocabulary, OutputStream os)
    throws RDFHandlerException {
        final NQuadsWriter nQuadsWriter = new NQuadsWriter(os);
        serializeVocabulary(vocabulary, nQuadsWriter);
    }

    /**
     * Serialized the given <code>vocabulary</code> to <i>NQuads</i> and return them as string.
     *
     * @param vocabulary vocabulary to be serialized.
     * @return string contained serialization.
     * @throws RDFHandlerException
     */
    public static String serializeVocabularyToNQuads(Vocabulary vocabulary)
    throws RDFHandlerException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        serializeVocabularyToNQuads(vocabulary, baos);
        return baos.toString();
    }

    /**
     * Serializes all the vocabularies to <i>NQuads</i> over the given output stream.
     *
     * @param os output stream.
     */
    public static void serializeVocabulariesToNQuads(OutputStream os) {
        final Class vocabularyClass = Vocabulary.class;
        final List<Class> vocabularies = DiscoveryUtils.getClassesInPackage(
                vocabularyClass.getPackage().getName(),
                vocabularyClass
        );
        for (Class vocabClazz : vocabularies) {
            final Vocabulary instance;
            try {
                final Constructor constructor = vocabClazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                instance = (Vocabulary) constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Error while instantiating vocabulary class " + vocabClazz, e);
            }
            try {
                serializeVocabularyToNQuads(instance, os);
            } catch (RDFHandlerException rdfhe) {
                throw new RuntimeException("Error while serializing vocabulary.", rdfhe);
            }
        }
    }

    private RDFSchemaUtils() {}

}
