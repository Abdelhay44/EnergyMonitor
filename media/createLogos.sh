#!/bin/sh

INFILE="logo_hq.png"

mkdir -p mipmap-xxxhdpi mipmap-xxhdpi mipmap-xhdpi mipmap-mdpi mipmap-hdpi

convert $INFILE -resize 192x192 mipmap-xxxhdpi/ic_launcher.png
convert $INFILE -resize 144x144 mipmap-xxhdpi/ic_launcher.png
convert $INFILE -resize 96x96 mipmap-xhdpi/ic_launcher.png
convert $INFILE -resize 48x48 mipmap-mdpi/ic_launcher.png
convert $INFILE -resize 72x72 mipmap-hdpi/ic_launcher.png
convert $INFILE -resize 512x512 logo_hq_512.png
