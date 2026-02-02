rm -rf apk-output
mkdir -p apk-output
./gradlew assembleDebug 
cp app/build/outputs/apk/debug/*.apk apk-output/
#mcli cp --recursive apk-output s-ao/