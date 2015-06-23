How to run graph labeler with labelg.exe
1. Build labelg.exe: If you are on windows machine, would require cygwin with
gcc.
	1.1 Make sure gcc is installed using 'echo; gcc --version'.
	1.2 Go to src\main\nauty\nauty25r9 and run '.\configure' (Linux machine) './configure' (windows machine)
		1.2.1 If this error '-bash: ./configure: Permission denied' occurs then give executable permission to ./configure using
		      command 'chmod +x ./configure' (Windows machine).
	    1.2.2 Then again run ./configure (Windows machine).
	1.3 then 'make all'. On success, labelg.exe will be present under the same folder.

2. Make sure network-motif java project is build successfully. To ensure this,
check the class files are present under target\classes and
target\test-classes.

3. Copy labelg.exe (created in step 1) to a working directory where 'src' folder is present (say current
folder) and junit-4.10.jar (maven project should have pulled it on disk) as well to the working directory.

4. There are two tests for graph labelling. They can be run (both should pass) as follows:
	java -cp '.\target\classes;.\target\test-classes;.\junit-4.10.jar' org.junit.runner.JUnitCore edu.uw.nemo.labeler.GraphFormatTest
	java -cp '.\target\classes;.\target\test-classes;.\junit-4.10.jar' org.junit.runner.JUnitCore edu.uw.nemo.labeler.GraphLabelTest


For MapReduce code to run on Linux machine i.e. UWb nodes:
Again follow steps 1 to 4 in linux machine.
Directly copying labelg.exe from windows machine to Linux won't work.
make labelg.exe in linux machine as well.


Overall build:
1. Ensure that java 7 (jdk 1.7) or higher is installed on target machine.
2. Install apache maven from http://maven.apache.org/
3. Go to project root directory. on most systems this should be 'network-motif' folder pulled from google code.
4. Run 'mvn -U clean install' (without the single quotes).
5. This will pull in dependencies as configured in pom, compile, run tests and genrated deployable jar.
6. On success, nemo-1.0-SNAPSHOT.jar will be available under target folder.
7. Test output is also available surefire folder under target.

Running end to end functionality:
1. Fire up your favorite IDE (eclispe / netbeans / idea / ...)
2. Load nemo project.
3. Load and run NemoControllerTest.

This test does following:
1. Loads a sample data file included in nemo.
2. Pulls out the mappings.
3. Generates motifs using ESU algorithm.
4. Generates canonical labelling for motifs (pending integration).
5. Generates standard concentrations per canonical label (pending integration).

Converting PPI files to standard input format:
1. Nemo uses a simple csv file input. The file should contain two strings (from vertex, to vertex) separated by a tab per line.
2. DIPConverterTest can be used for converting a PPI file to this format. A working example is included in the test.

How to debug
1. Because of dependency upon labelg.exe, the tests and program need to be run
from command line.
2. To attach a debugger to the running program, it should be started with
certain flags so that debugger could attach to it.
3. Flags required: -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8888,suspend=y
4. More information about debugging and the flags above can be found here: http://www.informit.com/articles/article.aspx?p=406342&seqNum=2
5. For e.g., to debug NemoControllerTest, start it from command line by issuing following command:
    java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8888,suspend=y -cp '.\target\classes;.\target\test-classes;.\junit-4.10.jar' org.junit.runner.JUnitCore edu.uw.nemo.NemoControllerTest
6. Above command will setup the environment for debugging, and the execution
will remain suspend untill debugger in attached to it.
7. Now start your IDE (NetBeans, Eclipse, etc.), put breakpoints, and attach debugger using
socket connection. You need to specify the same port addrees as mentioned
in the command line while starting the program (8888 in this case).
8. Happy Debugging!!

Google Cloud MapReduce
https://console.developers.google.com/
1. SSH to google cloud compute instance

	Find instance name from Google Developer Console > Compute > Compute Engine > VM Instances
	or
	Use command - gcloud compute instances list (from cygwin with gcloud tool package installed)

	gcloud compute ssh <instance name> --zone asia-east1-a

2. Copy the jar file to run on hadoop using following command

	gcloud compute copy-files <local-file> <instance-name>:<path-to-folder> --zone asia-east1-a

3. Use hadoop user to run mapreduce jobs

	sudo su -l hadoop

4. Run map-reduce job using sample command below

	hadoop jar nemo.jar /nemo/input/full_scere_20140427.csv /nemo/full4/intermediate /nemo/full4/output /nemo/binaries/labelg 4 nemo.jar -randomize -growReducers -setTimeout <timeout value in ms> -setMaxSplitSize <max split size in bytes> -startStage <stage index (0:ESU, 1:Labeler, 2:Combiner)>

5. Monitoring map-reduce jobs

	Use the following webapp to monitor jobs
	http://130.211.240.79:8088/
