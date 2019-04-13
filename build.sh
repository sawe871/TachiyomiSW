export ANDROID_HOME="/home/server/Android/Sdk"
./gradlew assembleStandard $@ 2> errors.log
rm app-aligned.apk
rm tachiyomiAZ.apk
/bin/bash /home/server/super_sekrit_build_skript.sh
cp tachiyomiAZ.apk /var/www/giganig.ga/html
