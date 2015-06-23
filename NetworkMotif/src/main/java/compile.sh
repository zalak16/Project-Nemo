#!/bin/bash

echo "Compiling"

#javac -cp `hadoop classpath`:. edu/uw/nemo/labeler/*.java edu/uw/nemo/ESUGen.java edu/uw/nemo/motifSignificant/explicitMethod/RandomCanonicalLabelling.java edu/uw/nemo/model/AdjacentVertexWithEdge.java edu/uw/nemo/model/AdjacencyMapping.java edu/uw/nemo/model/Mapping.java edu/uw/nemo/io/Parser.java edu/uw/nemo/motifSignificant/explicitMethod/SwitchingAlgorithm/ConvertDataStructure.java edu/uw/nemo/motifSignificant/explicitMethod/SwitchingAlgorithm/SwitchingAlgoirthmGenerateGraph.java edu/uw/nemo/motifSignificant/mapreduce/IntegerTwoDArrayWritable.java edu/uw/nemo/motifSignificant/mapreduce/SwitchingAlgorithmGenerateGraphMapper.java edu/uw/nemo/motifSignificant/mapreduce/SwitchingAlgorithmGenerateGraphReducer.java edu/uw/nemo/motifSignificant/mapreduce/GraphGeneratorJob.java edu/uw/nemo/motifSignificant/mapreduce/NemoRandomGraphMain.java

javac -cp `hadoop classpath`:. labelg.exe edu/uw/nemo/nauty/*.java edu/uw/nemo/labeler/*.java edu/uw/nemo/esu/ESUGen.java edu/uw/nemo/motifSignificant/explicitMethod/*.java edu/uw/nemo/motifSignificant/explicitMethod/SwitchingAlgorithm/*.java edu/uw/nemo/motifSignificant/mapreduce/*.java  edu/uw/nemo/model/*.java edu/uw/nemo/io/*.java 

echo "Compilation done"

#jar -cvf nemo.jar edu/uw/nemo/labeler/*.class edu/uw/nemo/edu/uw/nemo/model/*.class edu/uw/nemo/io/Parser.class edu/uw/nemo/motifSignificant/explicitMethod/SwitchingAlgorithm/*.class edu/uw/nemo/motifSignificant/mapreduce/*.class

jar -cvf nemo.jar labelg.exe edu/uw/nemo/nauty/*.class edu/uw/nemo/labeler/*.class edu/uw/nemo/esu/ESUGen.class edu/uw/nemo/motifSignificant/explicitMethod/*.class edu/uw/nemo/motifSignificant/explicitMethod/SwitchingAlgorithm/*.class edu/uw/nemo/motifSignificant/mapreduce/*.class  edu/uw/nemo/model/*.class edu/uw/nemo/io/*.class 

echo "copying"
 cp nemo.jar edu/uw/nemo/motifSignificant/mapreduce/nemo.jar

echo "Copying nemo.jar"

 ~/hadoop-2.6.0/bin/hdfs dfs -rm /user/zalak/logdir/*
 ~/hadoop-2.6.0/bin/hdfs dfs -rmdir /user/zalak/logdir
 
  ~/hadoop-2.6.0/bin/hdfs dfs -rmdir /user/zalak/output1/_temporary/0
 ~/hadoop-2.6.0/bin/hdfs dfs -rmdir /user/zalak/output1/_temporary
  ~/hadoop-2.6.0/bin/hdfs dfs -rm /user/zalak/output1/*
 ~/hadoop-2.6.0/bin/hdfs dfs -rmdir /user/zalak/output1
 
  ~/hadoop-2.6.0/bin/hdfs dfs -rm /user/zalak/RandomGraphGenerator/*
 ~/hadoop-2.6.0/bin/hdfs dfs -rmdir /user/zalak/RandomGraphGenerator
 
 ~/hadoop-2.6.0/bin/hdfs dfs -ls /user/zalak/
 
 echo "Copying nemo.jar"
 ~/hadoop-2.6.0/bin/hdfs dfs -rm /user/zalak/nemo.jar

~/hadoop-2.6.0/bin/hdfs dfs -put nemo.jar /user/zalak/




