#!/usr/bin/env bash

set -e
#创建苹果启动图
rm -rf tmp.iconset
mkdir tmp.iconset
# 注意mac-icon.png 是有内边距的，需符合苹果的设计规范

sips -z 16 16     mac-icon.png --out tmp.iconset/icon_16x16.png
sips -z 32 32     mac-icon.png --out tmp.iconset/icon_16x16@2x.png
sips -z 32 32     mac-icon.png --out tmp.iconset/icon_32x32.png
sips -z 64 64     mac-icon.png --out tmp.iconset/icon_32x32@2x.png
sips -z 128 128   mac-icon.png --out tmp.iconset/icon_128x128.png
sips -z 256 256   mac-icon.png --out tmp.iconset/icon_128x128@2x.png
sips -z 256 256   mac-icon.png --out tmp.iconset/icon_256x256.png
sips -z 512 512   mac-icon.png --out tmp.iconset/icon_256x256@2x.png
sips -z 512 512   mac-icon.png --out tmp.iconset/icon_512x512.png
sips -z 1024 1024   mac-icon.png --out tmp.iconset/icon_512x512@2x.png

iconutil -c icns tmp.iconset -o icon.icns
rm -rf tmp.iconset
echo "已生成icon.icns"