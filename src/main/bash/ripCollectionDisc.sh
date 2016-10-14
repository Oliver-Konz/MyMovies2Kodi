#!/bin/bash

DRIVE=disc:0
MINLENGTH=4200
RIP_PARENT_DIR=/data/video/Rip

EMAIL_NOTIFICATION=true
NOTIFICATION_EMAIL=rip-status@oliverkonz.de

DISCSTUB=$(basename "$1")
COLLECTION_DIR=$(dirname "$1")
TITLE="${DISCSTUB%.*}"
RIP_DIR="$RIP_PARENT_DIR"/"$TITLE"

function status {
	echo "$1"
	if $EMAIL_NOTIFICATION; then
		echo "Status Email." | mail -r "neo <neo@oliverkonz.de>" -s "$TITLE: $1" "$NOTIFICATION_EMAIL"
	fi
	exit $2
}

echo "Importing $TITLE ..."

if ! mkdir -p "$RIP_DIR"; then
	echo "Error creating rip dir $RIP_DIR."
	exit 2
fi

if sometool $MINLENGTH $DRIVE "$RIP_DIR"; then
	eject /dev/cdrom

	RIP_FILES=("$RIP_DIR"/*)
	NUM_RIP_FILES=${#RIP_FILES[@]}
	
	if (( $NUM_RIP_FILES == 1 )); then
		if mv "${RIP_FILES[0]}" "$COLLECTION_DIR/$TITLE.mkv"; then
			rm "$1"
			rmdir "$RIP_DIR"
		else
			status "Error moving file to collection dir." 4
		fi
		status "Import successful." 0
	else
		status "More than one title ripped to $RIP_DIR." 1
	fi
else
	status "Import failed." 3
fi
