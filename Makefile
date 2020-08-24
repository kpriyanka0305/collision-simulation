.PHONY: run compile

compile:
	mvn --offline compile

run:
	mvn --offline exec:java -Dexec.mainClass=main.Main -Dexec.args=$(sumocfg)

data/cross.net.xml: data/cross.nod.xml data/cross.edg.xml
	netconvert --node-files=data/cross.nod.xml --edge-files=data/cross.edg.xml --output-file=data/cross.net.xml
