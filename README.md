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
## Notes
Can't run this with 1millions of lines, this is due to HSQLDB (not sure HSQLDB does not support and any special setting required here!)
