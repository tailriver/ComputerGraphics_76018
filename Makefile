#this is the makefile for GC class
RM=rm -f
CP=cp
JARLIBHOME=./lib
JAVA=java
JAVAC=javac

JARLIBHOME=~/Desktop/SimpleExample/lib
JAVA=/Library/Java/JavaVirtualMachines/1.7.0u6.jdk/Contents/Home/bin/java
JAVAC=/Library/Java/JavaVirtualMachines/1.7.0u6.jdk/Contents/Home/bin/javac

CLASSPATH=$(JARLIBHOME)/jar/*
JAVAFLAGS=-cp $(CLASSPATH):bin
JAVACFLAGS=-d bin -sourcepath src -cp $(CLASSPATH):. -Xlint:deprecation

CLASSES=$(patsubst src/%.java,bin/%.class,$(wildcard src/*.java))
TARBALL=CG_00M00000

.PHONY: Q1 Q2 Q3 clean tarball
.SUFFIXES : .java .class

#ALL:: bin/Main.class $(CLASSES)
#	$(JAVA) $(JAVAFLAGS) $(<:bin/%.class=%)

ALL:: Q1 Q2 Q3

Q1: bin/Question1.class $(CLASSES)
	$(JAVA) $(JAVAFLAGS) $(<:bin/%.class=%)

Q2: bin/Question2.class $(CLASSES)
	$(JAVA) $(JAVAFLAGS) $(<:bin/%.class=%)

Q3: bin/Question3.class $(CLASSES)
	$(JAVA) $(JAVAFLAGS) $(<:bin/%.class=%)

bin/%.class: src/%.java
	mkdir -p bin
	$(JAVAC) $(JAVACFLAGS) $<

clean:
	$(RM) -R bin $(TARBALL) $(TARBALL).tar.gz

tarball:
	mkdir -p $(TARBALL)/lib/jar
	$(CP) -R src $(TARBALL)
	$(CP) -R resource $(TARBALL)
	sed -e "8,10s/^/#/" Makefile > $(TARBALL)/Makefile
	tar czf $(TARBALL).tar.gz $(TARBALL)
	$(RM) -R $(TARBALL)
