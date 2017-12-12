#!/bin/bash

base_path=$(cd `dirname $0`; cd ..; pwd)

cd $base_path

mvn clean install -DskipTests -Pprod

