/*
 * Copyright (c) 2008 Digital Bazaar, Inc.
 *
 * This file is part of librdfa.
 *
 * librdfa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * librdfa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with librdfa. If not, see <http://www.gnu.org/licenses/>.
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
        //std::cout << "Callback::~Callback()" << std::endl;
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
