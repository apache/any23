// Licensed to the Apache Software Foundation (ASF) under one or more
// contributor license agreements.  See the NOTICE file distributed with
// this work for additional information regarding copyright ownership.
// The ASF licenses this file to You under the Apache License, Version 2.0
// (the "License"); you may not use this file except in compliance with
// the License.  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

%module(directors="1") rdfa
%feature("director") Callback;

%{
  #include "RdfaParser.h"

  RdfaParser* gRdfaParser = NULL;
  void process_default_graph_triple(rdftriple* triple, void* callback_data);
  void process_processor_graph_triple(rdftriple* triple, void* callback_data);
  size_t fill_buffer(char* buffer, size_t buffer_length, void* callback_data);
%}

%constant int RDF_TYPE_NAMESPACE_PREFIX = RDF_TYPE_NAMESPACE_PREFIX;
%constant int RDF_TYPE_IRI = RDF_TYPE_IRI;
%constant int RDF_TYPE_PLAIN_LITERAL = RDF_TYPE_PLAIN_LITERAL;
%constant int RDF_TYPE_XML_LITERAL = RDF_TYPE_XML_LITERAL;
%constant int RDF_TYPE_TYPED_LITERAL = RDF_TYPE_TYPED_LITERAL;

%{
  void process_default_graph_triple(rdftriple* triple, void* callback_data){
    gRdfaParser->c_process_default_graph_triple(triple,  callback_data);
  }
  void process_processor_graph_triple(rdftriple* triple, void* callback_data){
    gRdfaParser->c_process_processor_graph_triple( triple,  callback_data);
  }
  size_t fill_buffer(char* buffer, size_t buffer_length, void* callback_data){
    return gRdfaParser->c_fill_buffer(buffer, buffer_length, callback_data);
  }
%}

%include RdfaParser.h

%extend RdfaParser {
    void init (){
        gRdfaParser = self;
        rdfa_set_default_graph_triple_handler(gRdfaParser->mBaseContext, &process_default_graph_triple);
        rdfa_set_processor_graph_triple_handler(gRdfaParser->mBaseContext, &process_processor_graph_triple);
        rdfa_set_buffer_filler(gRdfaParser->mBaseContext, &fill_buffer);
    }
}
