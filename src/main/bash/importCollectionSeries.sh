#!/bin/bash

VIDEO_DIRS=("$1"*)
STUB_DIR=$2

VIDEO_FILES=()
for DIR in "${VIDEO_DIRS[@]}"
do
	FILES_IN_DIR=("$DIR"/*.mkv)
	VIDEO_FILES=("${VIDEO_FILES[@]}" "${FILES_IN_DIR[@]}")
done
NUM_VIDEOS=${#VIDEO_FILES[@]}

STUB_FILES=("$STUB_DIR"/*.disc)
NUM_STUBS=${#STUB_FILES[@]}

if (( NUM_VIDEOS != NUM_STUBS )); then
	echo "There are $NUM_VIDEOS video files but $NUM_STUBS stub files!"
	exit 1
fi

for i in `seq 0 $(( NUM_VIDEOS - 1 ))` ;
do
	TITLE="${STUB_FILES[i]%.*}"
	if mv "${VIDEO_FILES[i]}" "$TITLE.mkv"; then
		rm "${STUB_FILES[i]}"
	else
		echo "Error moving ${VIDEO_FILES[i]}."
		exit 2
	fi
done

for DIR in "${VIDEO_DIRS[@]}"
do
        rmdir "$DIR"
done

echo "Done."
