#!/bin/bash

#eject -t /dev/cdrom
#sleep 10

dir=/data/video/Rip/"$1"
mkdir "$dir"
sometool "$dir"
eject /dev/cdrom
