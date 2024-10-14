#!/usr/bin/env bash

mvn versions:set -DnewVersion=24.09.01-SNAPSHOT
mvn -N versions:update-child-modules
mvn clean package install