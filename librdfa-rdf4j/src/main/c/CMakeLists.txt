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
