.PHONY: run compile

compile:
	mvn -o compile

run:
	mvn -o exec:java -Dexec.mainClass=Main

data/cross.net.xml: data/cross.nod.xml data/cross.edg.xml
	netconvert --node-files=data/cross.nod.xml --edge-files=data/cross.edg.xml --output-file=data/cross.net.xml
