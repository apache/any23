package org.deri.any23.validator.rule;

import org.deri.any23.validator.DOMDocument;
import org.deri.any23.validator.Fix;
import org.deri.any23.validator.Rule;
import org.deri.any23.validator.RuleContext;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Fixes the misuse of the meta name attribute.
 *
 * @see org.deri.any23.validator.rule.MetaNameMisuseRule     
 * @author Michele Mostarda (mostarda@fbk.eu)
 * @author Davide Palmisano (palmisano@fbk.eu)
 */
public class MetaNameMisuseFix implements Fix {

    public String getHRName() {
        return "meta-name-misuse-fix";
    }

    public void execute(Rule rule, RuleContext context, DOMDocument document) {
        List<Node> nodes = (List<Node>) context.getData(MetaNameMisuseRule.ERRORED_META_NODES);
        for(Node node : nodes) {
            final String nameValue = node.getAttributes().getNamedItem("name").getTextContent();
            node.getAttributes().removeNamedItem("name");
            Node propertyNode = document.getOriginalDocument().createAttribute("property");
            propertyNode.setNodeValue(nameValue);
            node.getAttributes().setNamedItem(propertyNode);

        }
    }

}
