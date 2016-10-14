#!/bin/bash

TITLE=$1
SEASON=$2
EPISODES=$3
PROTOTYPE=$4
PROTOTYPE_FILE=$(basename "$PROTOTYPE")

if [[ $PROTOTYPE =~ \.bluray\.disc$ ]]; then
	DISCTYPE=bluray
else
	DISCTYPE=dvd
fi

cd /data/video/Discs/Serien

SEASON_DIR="$TITLE.$DISCTYPE/Season $SEASON"
mkdir -p "$SEASON_DIR"

for i in `seq -f "%02g" 1 $EPISODES`;
do
	cp "$PROTOTYPE" "$SEASON_DIR"
	mv "$SEASON_DIR/$PROTOTYPE_FILE" "$SEASON_DIR/$TITLE ${SEASON}x${i}.$DISCTYPE.disc"
done
