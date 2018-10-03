#!/bin/bash

pushd $(dirname $0) > /dev/null
basepath=$(pwd)
popd > /dev/null

cat << EndOfMessage


EndOfMessage

java -cp "$basepath"/client.jar:"$basepath"/shared.jar -Djava.security.policy="$basepath"/policy ca.polymtl.inf8480.tp1.client.Client $*
