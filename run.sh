#!/bin/bash

mvn package

echo -e  "\n=================================\n"

java -cp target/*.jar cz.topolik.hashcodecollisions.StringHashCodeCollisionsPostfixator

echo -e  "\n=================================\n"

java -cp target/*.jar cz.topolik.hashcodecollisions.StringHashCodeCollisionsMutator

echo -e  "\n=================================\n"

