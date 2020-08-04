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


Install SARL matching your SUMO version
---------------------------------------

- SARL needs jdk-11 or older. SARL does not work with jdk-13.
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
      $ mvn exec:java -Dexec.mainClass=Main

- Alternatively, generate eclipse project from mvn

      $ mvn eclipse:eclipse

- Open in eclipse, and run from eclipse
