JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
		$(JC) $(JFLAGS) $*.java

SSPCLASSES = \
		ssp.java \

default: classes

classes: $(SSPCLASSES:.java=.class)

clean:
		rm *.class