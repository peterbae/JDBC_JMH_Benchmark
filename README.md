# JDBC_JMH_Benchmark

Run using "mvn clean install" into "java -jar target/benchmarks.jar".

This project uses Maven to download the latest version of the MSSQL JDBC driver.

In the mssql-jdbc dependency in the pom file, modify the version information (along with deploying the custom mssql jdbc driver jar as a repo) to use the custom jar during JMH runs.

I've attached a couple of run results to this project, named:

Calendar to ZonedDateTime performance results - before implementation.txt (running the JMH benchmark with 8.1.0.jre13-preview driver jar)

Calendar to ZonedDateTime performance results - after implementation.txt (running the JMH benchmark with my latest custom jar from CalToZDT branch my mssql-jdbc fork)