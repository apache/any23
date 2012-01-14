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

package org.deri.any23.vocab;

import org.openrdf.model.URI;

/**
 * Vocabulary definitions from vocabularies/review.rdf
 */
public class REVIEW extends Vocabulary {

    private static REVIEW instance;

    public static REVIEW getInstance() {
        if(instance == null) {
            instance = new REVIEW();
        }
        return instance;
    }

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://purl.org/stuff/rev#";

    /**
     * The namespace of the vocabulary as a URI.
     */
    public final URI NAMESPACE = createURI(NS);

    /**
     * The commenter on the review.
     */
    public final URI commenter =  createProperty("commenter");

    /**
     * Used to associate a review with a comment on the review.
     */
    public final URI hasComment = createProperty("hasComment");

    /**
     * Associates a review with a feedback on the review.
     */
    public final URI hasFeedback = createProperty("hasFeedback");

    /**
     * Associates a work with a a review.
     */
    public final URI hasReview = createProperty("hasReview");

    /**
     * A numeric value.
     */
    public final URI maxRating = createProperty("maxRating");

    /**
     * A numeric value.
     */
    public final URI minRating = createProperty("minRating");

    /**
     * Number of positive usefulness votes (integer).
     */
    public final URI positiveVotes = createProperty("positiveVotes");

    /**
     * A numeric value.
     */
    public final URI rating = createProperty("rating");

    /**
     * The person that has written the review.
     */
    public final URI reviewer = createProperty("reviewer");

    /**
     * The text of the review.
     */
    public final URI text = createProperty("text");

    /**
     * The title of the review.
     */
    public final URI title = createProperty("title");

    /**
     * Number of usefulness votes (integer).
     */
    public final URI totalVotes = createProperty("totalVotes");

    /**
     * The type of media of a work under review.
     */
    public final URI type = createProperty("type");

    /**
     * A comment on a review.
     */
    public final URI Comment = createProperty("Comment");

    /**
     * Feedback on the review. Expresses whether the review was useful or not.
     */
    public final URI Feedback = createProperty("Feedback");

    /**
     * A review of an work.
     */
    public final URI Review = createProperty("Review");

    private URI createProperty(String localName) {
        return createProperty(NS, localName);
    }

    private REVIEW(){
        super(NS);
    }

}
