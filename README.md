# logprocessor
Event Log Processor

#Requirements to run
1. Java SDK 1.8 or above
2. Maven 3.x
3. HSQLDB
4. Clone logprocessor repo https://github.com/marcoakeller/logprocessor.git
5. Use a logfile.txt as the format described bellow

#Steps to RUN Log Processor App:

1. Get the sample log file with each line representing one log entry and following one of these JSON format:
{"id":"scsmbstgrc", "state":"FINISHED", "timestamp":1491377495218}
{"id":"scsmbstgra", "state":"FINISHED", "type":"APPLICATION_LOG", "host":"12345","timestamp":1491377495217}

2. Download the HSQLDB at
https://sourceforge.net/projects/hsqldb/files/

3. Unzip the file and go to bin directory.

4. Startup the DB server using the hsqldb.jar file by running:
java -cp hsqldb.jar org.hsqldb.server.Server --database.0 file:eventsdb --dbname.0 EVENTSDB

5. Go to the main folder of the logprocessor repo and build with maven:
mvn clean install

6. On the target folder, run the jar:
java -jar logprocessor-jar-with-dependencies.jar