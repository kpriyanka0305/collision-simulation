How to get it running
=====================

These instructions are for Linux.


Install latest stable SUMO
--------------------------

- Clone sumo source code, and check out the latest stable version
- I compiled it from source code. The following instructions come from the SUMO
  README.

      $ git clone github.com:eclipse/sumo.git
      $ cd sumo
      $ git checkout -b v1_6_0 v1_6_0
      $ cd build/cmake-build
      $ cmake ../..
      $ make -j3

- Make sure that the sumo binary directory is in your $PATH

      $ export PATH="$PATH_TO_SUMO_SOURCE_DIR/bin:$PATH"


Install TRAAS matching your SUMO version
---------------------------------------

- Go to sumo/tools/contributed/traas/

      $ cd sumo/tools/contributed/traas
      $ mvn install


Compile collision-simulation
----------------------------

- Clone this repository

      $ git clone git@github.com:kpriyanka0305/collision-simulation.git
      $ cd collision-simulation

- Compile and run with maven

      $ mvn compile
      $ mvn exec:java -Dexec.mainClass=main.Main

- To use from within eclipse
  - First install the M2Eclipse plugin https://www.eclipse.org/m2e/index.html
  - Then import the project pom.xml
  - Create a maven run configuration with Goals: compile exec:java
  - Parameters exec.mainClass = main.Main
  - To make this the active run configuration, you have to change an option in Eclipse:
  - Window -> Preferences -> Run/Debug -> Launching -> Launch Operations. You
    can choose to always run the previous application.
