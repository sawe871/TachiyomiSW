export ANDROID_HOME="/home/server/Android/Sdk"
./gradlew assembleStandard
rm app-aligned.apk
rm tachiyomiAZ.apk
/bin/bash /home/server/super_sekrit_build_skript.sh
cp tachiyomiAZ.apk /var/www/giganig.ga/html
