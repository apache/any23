# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

cmake_minimum_required(VERSION 2.8)

# Check if required packages are installed
find_package(SWIG REQUIRED)
find_package(Java REQUIRED)
find_package(JNI REQUIRED)
find_package(LibXml2 REQUIRED)

# Add modules
include(UseJava)
include(UseSWIG)

# Add directories to the build process
set( CMAKE_SWIG_OUTDIR ${CMAKE_CURRENT_BINARY_DIR} )

include_directories(${LIBXML2_INCLUDE_DIR})
include_directories(${JNI_INCLUDE_DIRS})
include_directories(${CMAKE_CURRENT_BINARY_DIR}/..)

# Link library. TODO: replace because of deprecation
link_libraries(rdfa)

# Build the C++ code into a dynamic library: rdfaJava.dll (on Windows) or librdfaJava.so (on Linux)
set(CMAKE_SWIG_FLAGS -package org.apache.any23.rdf.librdfa)
set(CMAKE_SWIG_OUTDIR "${CMAKE_CURRENT_BINARY_DIR}/org/apache/any23/rdf/librdfa")
set_property(SOURCE rdfa.i PROPERTY CPLUSPLUS ON)
swig_add_module(
    rdfaJava
    java
    rdfa.i
    RdfaParser.cpp
)

# For convenience we copy the dynamic library to the current build folder
add_custom_command(
    TARGET rdfaJava
    POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy_if_different $<TARGET_FILE:rdfaJava> ${CMAKE_CURRENT_BINARY_DIR}
)
