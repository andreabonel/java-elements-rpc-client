#!/bin/bash

set -e

# Set your classpath according to your instalation. 
# The .jar files needed are: 
#    owner.jar
#    apache-commons-lang3.jar
#    hamcrest.jar
#    junit.jar
CP=""

cd ..
WORKDIR=tmpdir
mkdir -p $WORKDIR

TESTFILE=src/main/java/wf/bitcoin/javabitcoindrpcclient/examples/ElementsTestExample.java
RUNCLASS=""

RUNFILE=$(echo $TESTFILE | sed 's,.*wf,wf,g;s,.java$,,g;s,/,.,g')
echo "compiling"

javac \
    -classpath ${CP} \
    -sourcepath src/main/java:. \
    -d $WORKDIR \
    $TESTFILE
echo "running"
pushd $WORKDIR
java \
    -Dfile.encoding=UTF-8 \
    -classpath ${CP} \
    ${RUNCLASS} \
    $RUNFILE
popd
