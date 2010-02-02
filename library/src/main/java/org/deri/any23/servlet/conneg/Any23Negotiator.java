package org.deri.any23.servlet.conneg;

/**
 * Defines a {@link org.deri.any23.servlet.conneg.ContentTypeNegotiator} for <i>Any23</i>. 
 */
public class Any23Negotiator {

    private final static ContentTypeNegotiator any23negotiator;

    static {
        any23negotiator = new ContentTypeNegotiator();
        any23negotiator.setDefaultAccept("text/turtle");

        any23negotiator.addVariant("application/rdf+xml;q=0.95"     )
                .addAliasMediaType("application/xml;q=0.4"          )
                .addAliasMediaType("text/xml;q=0.4"                 );

        any23negotiator.addVariant("text/rdf+n3;charset=utf-8;q=0.9")
                .addAliasMediaType("text/n3;q=0.9"                  )
                .addAliasMediaType("application/n3;q=0.9"           );

        any23negotiator.addVariant("text/turtle"                    )
                .addAliasMediaType("application/x-turtle"           )
                .addAliasMediaType("application/turtle"             );

        any23negotiator.addVariant("text/plain;q=0.5");
    }

    public static ContentTypeNegotiator getNegotiator() {
        return any23negotiator;
    }
}
