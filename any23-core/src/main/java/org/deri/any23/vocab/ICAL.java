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
 * Vocabulary definitions from <code>ical.rdf</code>
 */
public class ICAL extends Vocabulary {

    /**
     * The namespace of the vocabulary as a string.
     */
    public static final String NS = "http://www.w3.org/2002/12/cal/icaltzd#";

    private static ICAL instance;

    public static ICAL getInstance() {
        if(instance == null) {
            instance = new ICAL();
        }
        return instance;
    }

    /**
     * The namespace of the vocabulary as a URI.
     */
    public final URI NAMESPACE = createURI(NS);

    public final URI DomainOf_rrule = createClass("DomainOf_rrule");
    public final URI List_of_Float  = createClass("List_of_Float");

    /**
     * Provide a grouping of component properties that define an alarm..
     */
    public final URI Valarm            = createClass("Valarm");

    public final URI Value_CAL_ADDRESS = createClass("Value_CAL-ADDRESS");

    public final URI Value_DATE        = createClass("Value_DATE");

    public final URI Value_DURATION    = createClass("Value_DURATION");

    public final URI Value_PERIOD      = createClass("Value_PERIOD");

    public final URI Value_RECUR       = createClass("Value_RECUR");

    public final URI Vcalendar         = createClass("vcalendar");

    /**
     * Provide a grouping of component properties that describe an event..
     */
    public final URI Vevent = createClass("Vevent");

    /**
     * Provide a grouping of component properties that describe either a request
     * for free/busy time, describe a response to a request for free/busy time or
     * describe a published set of busy time..
     */
    public final URI Vfreebusy = createClass("Vfreebusy");

    /**
     * Provide a grouping of component properties that describe a journal entry..
     */
    public final URI Vjournal = createClass("Vjournal");

    /**
     * Provide a grouping of component properties that defines a time zone..
     */
    public final URI Vtimezone = createClass("Vtimezone");

    /**
     * Provide a grouping of calendar properties that describe a to-do..
     */
    public final URI Vtodo = createClass("Vtodo");


    /**
     * The URI provides the capability to associate a document object with a
     * calendar component.default value type: URI.
     */
    public final URI attach = createProperty("attach");

    /**
     * The URI defines an "Attendee" within a calendar component.value type:
     * CAL-ADDRESS.
     */
    public final URI attendee   = createProperty("attendee");
    public final URI calAddress = createProperty("calAddress");
    public final URI component  = createProperty("component");
    public final URI daylight  = createProperty("daylight");

    /**
     * The URI specifies a positive duration of time.value type: DURATION.
     */
    public final URI duration = createProperty("duration");

    /**
     * This URI defines a rule or repeating pattern for an exception to a recurrence
     * set.value type: RECUR.
     */
    public final URI exrule = createProperty("exrule");

    /**
     * The URI defines one or more free or busy time intervals.value type: PERIOD.
     */
    public final URI freebusy = createProperty("freebusy");

    /**
     * value type: list of FLOATThis URI specifies information related to the
     * global position for the activity specified by a calendar component..
     */
    public final URI geo = createProperty("geo");

    /**
     * value type: CAL-ADDRESSThe URI defines the organizer for a calendar component..
     */
    public final URI organizer = createProperty("organizer");

    /**
     * This URI defines a rule or repeating pattern for recurring events, to-dos,
     * or time zone definitions.value type: RECUR.
     */
    public final URI rrule = createProperty("rrule");

    public final URI standard = createProperty("standard");

    /**
     * This URI specifies when an alarm will trigger.default value type: DURATION.
     */
    public final URI trigger = createProperty("trigger");

    /**
     * The TZURL provides a means for a VTIMEZONE component to point to a network
     * location that can be used to retrieve an up-to- date version of itself.value
     * type: URI.
     */
    public final URI tzurl = createProperty("tzurl");

    /**
     * This URI defines a Uniform URI Locator (URL) associated with the
     * iCalendar object.value type: URI.
     */
    public final URI url = createProperty("url");

    /**
     * value type: TEXTThis class of URI provides a framework for defining non-standard
     * properties..
     */
    public final URI X_ = createProperty("X-");

    /**
     * value type: TEXTThis URI defines the action to be invoked when an alarm
     * is triggered..
     */
    public final URI action = createProperty("action");

    /**
     * To specify an alternate text representation for the URI value..
     */
    public final URI altrep = createProperty("altrep");

    public final URI byday = createProperty("byday");

    public final URI byhour = createProperty("byhour");

    public final URI byminute = createProperty("byminute");

    public final URI bymonth = createProperty("bymonth");

    public final URI bysecond = createProperty("bysecond");

    public final URI bysetpos = createProperty("bysetpos");

    public final URI byweekno = createProperty("byweekno");

    public final URI byyearday = createProperty("byyearday");

    /**
     * value type: TEXTThis URI defines the calendar scale used for the calendar
     * information specified in the iCalendar object..
     */
    public final URI calscale = createProperty("calscale");

    /**
     * value type: TEXTThis URI defines the categories for a calendar component..
     */
    public final URI categories = createProperty("categories");

    /**
     * value type: TEXTThis URI defines the access classification for a calendar
     * component..
     */
    public final URI class_ = createProperty("class");

    /**
     * To specify the common name to be associated with the calendar user specified
     * by the URI..
     */
    public final URI cn = createProperty("cn");

    /**
     * value type: TEXTThis URI specifies non-processing information intended
     * to provide a comment to the calendar user..
     */
    public final URI comment = createProperty("comment");

    /**
     * value type: DATE-TIMEThis URI defines the date and time that a to-do
     * was actually completed..
     */
    public final URI completed = createProperty("completed");

    /**
     * value type: TEXTThe URI is used to represent contact information or alternately
     * a reference to contact information associated with the calendar component..
     */
    public final URI contact = createProperty("contact");

    public final URI count = createProperty("count");

    /**
     * This URI specifies the date and time that the calendar information was
     * created by the calendar user agent in the calendar store. Note: This is analogous
     * to the creation date and time for a file in the file system.value type: DATE-TIME.
     */
    public final URI created = createProperty("created");

    /**
     * To specify the type of calendar user specified by the URI..
     */
    public final URI cutype = createProperty("cutype");

    /**
     * To specify the calendar users that have delegated their participation to the
     * calendar user specified by the URI..
     */
    public final URI delegatedFrom = createProperty("delegatedFrom");

    /**
     * To specify the calendar users to whom the calendar user specified by the URI
     * has delegated participation..
     */
    public final URI delegatedTo = createProperty("delegatedTo");

    /**
     * value type: TEXTThis URI provides a more complete description of the
     * calendar component, than that provided by the "SUMMARY" URI..
     */
    public final URI description = createProperty("description");

    /**
     * To specify reference to a directory entry associated with the calendar user
     * specified by the URI..
     */
    public final URI dir = createProperty("dir");

    /**
     * This URI specifies the date and time that a calendar component ends.default
     * value type: DATE-TIME.
     */
    public final URI dtend = createProperty("dtend");

    /**
     * value type: DATE-TIMEThe URI indicates the date/time that the instance
     * of the iCalendar object was created..
     */
    public final URI dtstamp = createProperty("dtstamp");

    /**
     * default value type: DATE-TIMEThis URI specifies when the calendar component
     * begins..
     */
    public final URI dtstart = createProperty("dtstart");

    /**
     * default value type: DATE-TIMEThis URI defines the date and time that
     * a to-do is expected to be completed..
     */
    public final URI due = createProperty("due");

    /**
     * To specify an alternate inline encoding for the URI value..
     */
    public final URI encoding = createProperty("encoding");

    /**
     * default value type: DATE-TIMEThis URI defines the list of date/time exceptions
     * for a recurring calendar component..
     */
    public final URI exdate = createProperty("exdate");

    /**
     * To specify the free or busy time type..
     */
    public final URI fbtype = createProperty("fbtype");

    /**
     * To specify the content type of a referenced object..
     */
    public final URI fmttype = createProperty("fmttype");

    public final URI freq = createProperty("freq");

    public final URI interval = createProperty("interval");

    /**
     * To specify the language for text values in a URI or URI parameter..
     */
    public final URI language = createProperty("language");

    /**
     * value type: DATE-TIMEThe URI specifies the date and time that the information
     * associated with the calendar component was last revised in the calendar store.
     * Note: This is analogous to the modification date and time for a file in the
     * file system..
     */
    public final URI lastModified = createProperty("lastModified");

    /**
     * value type: TEXTThe URI defines the intended venue for the activity defined
     * by a calendar component..
     */
    public final URI location = createProperty("location");

    /**
     * To specify the group or list membership of the calendar user specified by
     * the URI..
     */
    public final URI member = createProperty("member");

    /**
     * value type: TEXTThis URI defines the iCalendar object method associated
     * with the calendar object..
     */
    public final URI method = createProperty("method");

    /**
     * To specify the participation status for the calendar user specified by the
     * URI..
     */
    public final URI partstat = createProperty("partstat");

    /**
     * value type: INTEGERThis URI is used by an assignee or delegatee of a
     * to-do to convey the percent completion of a to-do to the Organizer..
     */
    public final URI percentComplete = createProperty("percentComplete");

    /**
     * The URI defines the relative priority for a calendar component.value
     * type: INTEGER.
     */
    public final URI priority = createProperty("priority");

    /**
     * value type: TEXTThis URI specifies the identifier for the product that
     * created the iCalendar object..
     */
    public final URI prodid = createProperty("prodid");

    /**
     * To specify the effective range of recurrence instances from the instance specified
     * by the recurrence identifier specified by the URI..
     */
    public final URI range = createProperty("range");

    /**
     * default value type: DATE-TIMEThis URI defines the list of date/times
     * for a recurrence set..
     */
    public final URI rdate = createProperty("rdate");

    /**
     * default value type: DATE-TIMEThis URI is used in conjunction with the
     * "UID" and "SEQUENCE" URI to identify a specific instance of a recurring
     * "VEVENT", "VTODO" or "VJOURNAL" calendar component. The URI value is
     * the effective value of the "DTSTART" URI of the recurrence instance..
     */
    public final URI recurrenceId = createProperty("recurrenceId");

    /**
     * To specify the relationship of the alarm trigger with respect to the start
     * or end of the calendar component..
     */
    public final URI related = createProperty("related");

    /**
     * The URI is used to represent a relationship or reference between one
     * calendar component and another.value type: TEXT.
     */
    public final URI relatedTo = createProperty("relatedTo");

    /**
     * To specify the type of hierarchical relationship associated with the calendar
     * component specified by the URI..
     */
    public final URI reltype = createProperty("reltype");

    /**
     * This URI defines the number of time the alarm should be repeated, after
     * the initial trigger.value type: INTEGER.
     */
    public final URI repeat = createProperty("repeat");

    /**
     * value type: TEXTThis URI defines the status code returned for a scheduling
     * request..
     */
    public final URI requestStatus = createProperty("requestStatus");

    /**
     * value type: TEXTThis URI defines the equipment or resources anticipated
     * for an activity specified by a calendar entity...
     */
    public final URI resources = createProperty("resources");

    /**
     * To specify the participation role for the calendar user specified by the URI..
     */
    public final URI role = createProperty("role");

    /**
     * To specify whether there is an expectation of a favor of a reply from the
     * calendar user specified by the URI value..
     */
    public final URI rsvp = createProperty("rsvp");

    /**
     * To specify the calendar user that is acting on behalf of the calendar user
     * specified by the URI..
     */
    public final URI sentBy = createProperty("sentBy");

    /**
     * value type: integerThis URI defines the revision sequence number of the
     * calendar component within a sequence of revisions..
     */
    public final URI sequence = createProperty("sequence");

    /**
     * value type: TEXTThis URI defines the overall status or confirmation for
     * the calendar component..
     */
    public final URI status = createProperty("status");

    /**
     * This URI defines a short summary or subject for the calendar component.value
     * type: TEXT.
     */
    public final URI summary = createProperty("summary");

    /**
     * This URI defines whether an event is transparent or not to busy time
     * searches.value type: TEXT.
     */
    public final URI transp = createProperty("transp");

    /**
     * value type: TEXTTo specify the identifier for the time zone definition for
     * a time component in the URI value.This URI specifies the text value
     * that uniquely identifies the "VTIMEZONE" calendar component..
     */
    public final URI tzid = createProperty("tzid");

    /**
     * value type: TEXTThis URI specifies the customary designation for a time
     * zone description..
     */
    public final URI tzname = createProperty("tzname");

    /**
     * value type: UTC-OFFSETThis URI specifies the offset which is in use prior
     * to this time zone observance..
     */
    public final URI tzoffsetfrom = createProperty("tzoffsetfrom");

    /**
     * value type: UTC-OFFSETThis URI specifies the offset which is in use in
     * this time zone observance..
     */
    public final URI tzoffsetto = createProperty("tzoffsetto");

    /**
     * This URI defines the persistent, globally unique identifier for the calendar
     * component.value type: TEXT.
     */
    public final URI uid = createProperty("uid");

    public final URI until = createProperty("until");

    /**
     * value type: TEXTThis URI specifies the identifier corresponding to the
     * highest version number or the minimum and maximum range of the iCalendar specification
     * that is required in order to interpret the iCalendar object..
     */
    public final URI version = createProperty("version");

    private URI createClass(String string) {
        return createClass(NS, string);
    }

    private URI createProperty(String string) {
        return createProperty(NS, string);
    }

    private ICAL(){
        super(NS);
    }

}
