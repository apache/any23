# Librdfa - RDF4J

RDF4J parser that uses [librdfa](https://github.com/rdfa/librdfa) to parse RDFa to triples.

## Prerequisites

You need to install the [librdfa](https://github.com/rdfa/librdfa) library.

## Install

``` mvn
<dependency>
   <groupId>org.apache.any23</groupId>
   <artifactId>apache-any23-librdfa</artifactId>
   <version>1.0.0</version>
</dependency>

<repositories>
   <repository>
       <id>librdfa-rdf4j</id>
       <url>https://raw.github.com/JulioCCBUcuenca/librdfa-java/repository/</url>
   </repository>
</repositories>
```

## Compile

`mvn clean install`

## Use

Add the library and you can parse an `InputStream` as you would do with [`Rio`](http://docs.rdf4j.org/javadoc/2.1/org/eclipse/rdf4j/rio/Rio.html).

``` java
RDFParser rdfParser = Rio.createParser(RDFFormat.RDFA);
Model model = new LinkedHashModel();
rdfParser.setRDFHandler(new StatementCollector(model));
rdfParser.parse(in, "http://www.example.org./");
```

## Benchmarking

In general librdfa is 2-5 seconds faster than semargl.  

### librdfa-rdf4j
- round: 0.11 [+- 0.00] 
- round.block: 0.00 [+- 0.00]
- round.gc: 0.00 [+- 0.00]
- GC.calls: 0
- GC.time: 0.00
- time.total: 0.11
- time.warmup: 0.00
- time.bench: 0.11

### semargl-rdf4j
- round: 0.15 [+- 0.00]
- round.block: 0.00 [+- 0.00]
- round.gc: 0.00 [+- 0.00]
- GC.calls: 1
- GC.time: 0.00
- time.total: 0.15
- time.warmup: 0.00
- time.bench: 0.15
