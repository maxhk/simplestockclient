#!/bin/bash

workdir=$(dirname $(pwd $0))

protoc --java_out=$workdir/src market.proto