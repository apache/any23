package org.deri.any23.extractor.html;

import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.extractor.ExtractionResult;
import org.openrdf.model.BNode;
import org.w3c.dom.Node;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Work in progress
 *
 * @author Gabriele Renzi
 */
public abstract class EntityBasedMicroformatExtractor extends
        MicroformatExtractor {

    protected abstract String getBaseClassName();

    protected abstract boolean extractEntity(Node node, ExtractionResult out) throws ExtractionException;

    @Override
    public boolean extract() throws ExtractionException {
        List<Node> nodes = DomUtils.findAllByClassName(document.getDocument(), getBaseClassName());
        boolean foundAny = false;
        int count = 1;
        for (Node node : nodes) {
            String contextID = Integer.toString(count);
            ExtractionResult subResult = out.openSubResult(contextID);
            foundAny |= extractEntity(node, subResult);
            subResult.close();
        }
        return foundAny;
    }

    /**
     * @param node a DOM node representing a blank node
     * @return an RDF blank node corresponding to that DOM node, by using a
     *         blank node ID like "MD5 of http://doc-uri/#xpath/to/node"
     */
    protected BNode getBlankNodeFor(Node node) {
        return valueFactory.createBNode("node" + md5(documentURI + "#" + DomUtils.getXPathForNode(node)));
    }

    private String md5(String s) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(s.getBytes());
            byte[] digest = md5.digest();
            StringBuffer result = new StringBuffer();
            for (byte b : digest) {
                result.append(Integer.toHexString(0xFF & b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Should never happen, MD5 is supported", e);
		}
	}
}