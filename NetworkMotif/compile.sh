#!/bin/bash

echo "Compiling"

javac -cp `hadoop classpath`:.  edu/uw/nemo/model/AdjacentVertexWithEdge.java edu/uw/nemo/model/AdjacencyMapping.java edu/uw/nemo/model/Mapping.java edu/uw/nemo/io/Parser.java edu/uw/nemo/motifSignificant/mapreduce/SwitchingAlgorithmGenerateGraphMapper.java edu/uw/nemo/motifSignificant/mapreduce/SwitchingAlgorithmGenerateGraphReducer.java edu/uw/nemo/motifSignificant/mapreduce/GraphGeneratorJob.java edu/uw/nemo/motifSignificant/mapreduce/NemoRandomGraphMain.java

echo "Compilation done"

jar -cvf nemo.jar edu/uw/nemo/model/*.class edu/uw/nemo/io/Parser.class edu/uw/nemo/motifSignificant/mapreduce/*.class

echo "copying"
 cp nemo.jar edu/uw/nemo/motifSignificant/mapreduce/nemo.jar

echo "changing directory"

cd edu/uw/nemo/motifSignificant/mapreduce/

