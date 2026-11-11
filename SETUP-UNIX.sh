#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
jar="gradle/wrapper/gradle-wrapper.jar"
expected="498495120a03b9a6ab5d155f5de3c8f0d986a449153702fb80fc80e134484f17"
if [ ! -f "$jar" ]; then
  curl --fail --location --silent --show-error "https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar" --output "$jar"
fi
echo "$expected  $jar" | sha256sum -c -
