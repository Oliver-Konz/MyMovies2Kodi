#!/bin/sh

# Encode a video file to h264 with the specified parameters.
# Two command line argumets are required: 1) The title of the video and 2) the input file.

outputPath=/data/video/Encoded

# The process priority [-20-19] (nice value - lower number means higher priority)
priority=19

# The output profile (device compatibility) [main|high|high10|high422|high444]
# The 10 stands for 10 bit encoding, 422 and 444 stand for YUV chroma subsampling.
# Only high444 supports lossless (crf=0).
profile=high

# The quality [0-51] - lower number is better -> 0 is lossless
crf=20

# The encoding preset [ultrafast|veryfast|faster|fast|medium|slow|slower|veryslow]
preset=slower

# Situation dependent tuning (comma separated list) [film*|animation*|grain*|stillimage*|fastdecode|zerolatency]
# The tune settings marked with * are psy tunings - only one may be present.
tune=film

nice -n $priority ffmpeg -i "$2" -metadata title="$1"  -map 0 -c copy -c:v libx264 -crf $crf -preset $preset -tune $tune -profile:v $profile "$outputPath/$1.mkv" 
date
