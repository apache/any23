package org.deri.any23.validator;

import org.deri.any23.extractor.html.DomUtils;
import org.w3c.dom.Node;

import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface ValidationReport {

    /**
     * Defines the different issue levels.
     */
    enum IssueLevel {
        error,
        warning,
        info
    }

    List<Issue> getIssues();

    List<RuleActivation> getRuleActivations();

    List<Error> getErrors();

        class Issue {
        final IssueLevel level;
        final String message;
        final Node origin;

        public Issue(IssueLevel level, String message, Node origin) {
            this.level = level;
            this.message = message;
            this.origin = origin;
        }

        @Override
        public String toString() {
            return String.format(
                    "Issue %s '%s' %s",
                    level,
                    message,
                    DomUtils.getXPathForNode(origin)
            );
        }
    }

    class RuleActivation {
        private final String ruleStr;

        RuleActivation(Rule r) {
            ruleStr = r.getHRName();
        }
        @Override
         public String toString() {
            return ruleStr;
        }
    }

    abstract class Error {
        private final Exception cause;
        private final String message;

        public Error(Exception e, String msg) {
            cause   = e;
            message = msg;
        }

        @Override
        public String toString() {
            return String.format("%s %s %s", this.getClass().getName(), cause, message);
        }
    }

    class RuleError extends Error {
        private final Rule origin;

        RuleError(Rule r, Exception e, String msg) {
            super(e, msg);
            origin = r;
        }

        @Override
        public String toString() {
            return String.format("%s - %s", super.toString(), origin.getHRName());
        }
    }

    class FixError extends Error {
        private final Fix origin;

        FixError(Fix f, Exception e, String msg) {
             super(e, msg);
             origin = f;
        }

        @Override
        public String toString() {
            return String.format("%s - %s", super.toString(), origin.getHRName());
        }
    }

}
