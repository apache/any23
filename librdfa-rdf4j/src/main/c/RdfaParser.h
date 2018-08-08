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
#ifndef _RDFA_PARSER_H_
#define _RDFA_PARSER_H_

#include <string.h>
#include <cstdio>
#include <iostream>
#include <rdfa.h>
#include <rdfa_utils.h>
#include <stdlib.h>

struct rdfacontext;

class Callback {
public:

    virtual ~Callback() {
    }

    virtual void default_graph(char* subject, char* predicate, char* object, int object_type, char* datatype, char* language) {
    }

    virtual void processor_graph(char* subject, char* predicate, char* object, int object_type, char* datatype, char* language) {
    }

    virtual char* fill_data(size_t buffer_length) {
    }

    virtual size_t fill_len() {
    }
};

/**
 * The RdfaParser class is a wrapper class for Java to provide a
 * simple API for using librdfa in Java.
 */
class RdfaParser {
private:
    Callback *_callback;
public:
    /**
     * The base URI that will be used when resolving relative pathnames
     * in the document.
     */
    std::string mBaseUri;

    /**
     * The base RDFa context to use when setting the triple handler callback,
     * buffer filler callback, and executing the parser call.
     */
    rdfacontext* mBaseContext;

    RdfaParser(const char* baseUri) : _callback(0) {
        mBaseUri = baseUri;
        mBaseContext = rdfa_create_context(baseUri);
    }

    /**
     * Standard destructor.
     */
    ~RdfaParser() {
        rdfa_free_context(mBaseContext);
        delCallback();
    }

    void c_process_default_graph_triple(rdftriple* triple, void* callback_data) {
        _callback->default_graph(triple->subject, triple-> predicate, triple->object, triple->object_type, triple-> datatype, triple-> language);
        rdfa_free_triple(triple);
    }

    void c_process_processor_graph_triple(rdftriple* triple, void* callback_data) {
        _callback->processor_graph(triple->subject, triple-> predicate, triple->object, triple->object_type, triple-> datatype, triple-> language);
        rdfa_free_triple(triple);
    }

    size_t c_fill_buffer(char* buffer, size_t buffer_length, void* callback_data) {
        char* data = _callback->fill_data(buffer_length);
        size_t size = _callback -> fill_len();
        memset(buffer, ' ', buffer_length);
        memcpy(buffer, data, size);

        return size;
    }

    /**
     * Starts the parsing process for librdfa. When more data is
     * required by the XML parser, the buffer filler callback is
     * called. If triples are found, then the triple handler callback
     * is called.
     */
    int parse() {
        return rdfa_parse(mBaseContext);
    }

    void delCallback() {
        delete _callback;
        _callback = 0;
    }

    void setCallback(Callback *cb) {
        delCallback();
        _callback = cb;
    }
};

#endif /* _RDFA_PARSER_H_ */
