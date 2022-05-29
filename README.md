## Usage
* Compile and Run (this will process the filelog.txt in current folder)
```
mvn clean compile exec:java -Dexec.mainClass="com.cs1.App" -Dexec.cleanupDaemonThreads=false 
``` 
* Run all test
```
mvn clean test
```
* Run performance test
```
mvn clean test-compile exec:java -Dexec.mainClass="com.cs1.PerfTest" -Dexec.cleanupDaemonThreads=false -Dexec.classpathScope=test
```
* Performance result for log file of 20000 lines: 
```
INFO  com.cs1.PerfTest - Time taken in ms (gen, process, fetch) with n=10000: (363, 4616, 241)
```
## Notes
Can't run this with 1millions of lines, this is due to HSQLDB (not sure HSQLDB does not support and any special setting required here!)
