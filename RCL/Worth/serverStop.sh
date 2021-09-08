#!/bin/bash
pid=$(<serverPID.txt)
kill $pid
rm serverPID.txt
