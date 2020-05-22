How to get it running

- SARL needs jdk-11 or older. SARL does not work with jdk-13.
- Before compiling formalproject, you must install traas from source
  - download SUMO source code
  - go to sumo/tools/contributed/traas/ and
      $ mvn install
- go to formalproject
- compile with maven
      $ mvn compile
- run with maven
      $ mvn exec:java -Dexec.mainClass=Main 
- generate eclipse project from mvn
      $ mvn eclipse:eclipse
- open in eclipse, and run from eclipse
