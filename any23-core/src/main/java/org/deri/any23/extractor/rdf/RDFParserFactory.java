package org.deri.any23.extractor.rdf;

import org.deri.any23.extractor.ExtractionResult;
import org.deri.any23.rdf.Any23ValueFactoryWrapper;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.ParseErrorListener;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.turtle.TurtleParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This factory provides a common logic for creating and configuring correctly
 * any RDF parser used within the library.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
//TODO: move within this factory also the creation of the RDFXMLParser.
public class RDFParserFactory {

    private static final Logger logger = LoggerFactory.getLogger(RDFParserFactory.class);

    private static RDFParserFactory instance;

    public static RDFParserFactory getInstance() {
        if(instance == null) {
            instance = new RDFParserFactory();
        }
        return instance;
    }

    /**
     * Returns a new instance of a configured {@link org.openrdf.rio.turtle.TurtleParser}.
     *
     * @param verifyDataType data verification enable if <code>true</code>.
     * @param stopAtFirstError the parser stops at first error if <code>true</code>.
     * @param out the output extraction result.
     * @return a new instance of a configured Turtle parser.
     */
    public TurtleParser getTurtleParserInstance(
            boolean verifyDataType,
            boolean stopAtFirstError,
            final ExtractionResult out
    ) {
        if (out == null) {
            throw new NullPointerException("out cannot be null.");
        }

        TurtleParser parser = new TurtleParser();
        parser.setDatatypeHandling(
            verifyDataType ? RDFParser.DatatypeHandling.VERIFY : RDFParser.DatatypeHandling.IGNORE
        );
        parser.setStopAtFirstError(stopAtFirstError);
        parser.setParseErrorListener(new ParseErrorListener() {
            public void warning(String msg, int lineNo, int colNo) {
                try {
                    out.notifyError(ExtractionResult.ErrorLevel.WARN, msg, lineNo, colNo);
                } catch (Exception e) {
                    notifyExceptionInNotification(e);
                }
            }

            public void error(String msg, int lineNo, int colNo) {
                try {
                    out.notifyError(ExtractionResult.ErrorLevel.ERROR, msg, lineNo, colNo);
                } catch (Exception e) {
                    notifyExceptionInNotification(e);
                }
            }

            public void fatalError(String msg, int lineNo, int colNo) {
                try {
                    out.notifyError(ExtractionResult.ErrorLevel.FATAL, msg, lineNo, colNo);
                } catch (Exception e) {
                    notifyExceptionInNotification(e);
                }
            }

            private void notifyExceptionInNotification(Exception e) {
                if(logger != null) {
                    logger.error("An exception occurred while notifying an error.", e);
                }
            }
        });
        parser.setRDFHandler(new RDFHandlerAdapter(out));
        parser.setValueFactory(new Any23ValueFactoryWrapper(ValueFactoryImpl.getInstance(), out));

        return parser; 
    }

}
