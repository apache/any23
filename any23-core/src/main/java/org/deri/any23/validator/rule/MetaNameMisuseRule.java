package org.deri.any23.validator.rule;

import org.deri.any23.validator.DOMDocument;
import org.deri.any23.validator.Report;
import org.deri.any23.validator.Rule;
import org.deri.any23.validator.RuleContext;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks whether the meta attribute name is used to contain a property.
 *
 * @see org.deri.any23.validator.rule.MetaNameMisuseFix
 * @author Davide Palmisano (palmisano@fbk.eu)
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class MetaNameMisuseRule implements Rule {

    public static final String ERRORED_META_NODES = "errored-meta-nodes";

    public boolean applyOn(DOMDocument document, RuleContext context, Report report) {
        List<Node> metaNodes = document.getNodes("/HTML/HEAD/META");
        boolean foundIssue = false;
        final List<Node> wrongMetaNodes = new ArrayList<Node>();
        for(Node metaNode : metaNodes) {
            Node nameNode = metaNode.getAttributes().getNamedItem("name");
            if(nameNode != null && nameNode.getTextContent().contains(":")) {
                foundIssue = true;
                wrongMetaNodes.add(metaNode);
                report.reportIssue(
                        Report.IssueLevel.error,
                        "Error detected in meta node: name property contains a prefixed value.",
                        metaNode
                );
            }
        }
        context.putData(ERRORED_META_NODES, wrongMetaNodes);
        return foundIssue;
    }

}
