package org.deri.any23.validator.rule;

import org.deri.any23.validator.DOMDocument;
import org.deri.any23.validator.Rule;
import org.deri.any23.validator.RuleContext;
import org.deri.any23.validator.ValidationReport;
import org.w3c.dom.Node;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This rule is able to detect whether an about value is a valid URL
 * or otherwise is a valid relative URL.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class AboutNotURIRule implements Rule {

    public static final String NODES_WITH_INVALID_ABOUT = "nodes-with-invalid-about";

    public String getHRName() {
        return "about-not-uri-rule";
    }

    public boolean applyOn(DOMDocument document, RuleContext context, ValidationReport validationReport) {
        final List<Node> nodesWithAbout = document.getNodesWithAttribute("about");
        final List<Node> nodesWithInvalidAbout = new ArrayList<Node>();
        for(Node nodeWithAbout : nodesWithAbout) {
            if ( ! aboutIsValid(nodeWithAbout) ) {
                validationReport.reportIssue(
                        ValidationReport.IssueLevel.error,
                        "Invalid about value for node, expected valid URL.",
                        nodeWithAbout
                );
                nodesWithInvalidAbout.add(nodeWithAbout);
            }
        }
        if(nodesWithInvalidAbout.isEmpty()) {
            return false;
        }
        context.putData(NODES_WITH_INVALID_ABOUT, nodesWithInvalidAbout);
        return true;
    }

    private boolean aboutIsValid(Node n) {
        final String aboutContent = n.getAttributes().getNamedItem("about").getTextContent();
        if( isURL(aboutContent) ) {
            return true;
        }
        final char firstChar = aboutContent.charAt(0);
        return firstChar == '#' || firstChar == '/';
    }

    private boolean isURL(String candidateURIStr) {
        try {
            new URL(candidateURIStr);
        } catch (MalformedURLException murle) {
            return false;
        }
        return true;
    }

}
