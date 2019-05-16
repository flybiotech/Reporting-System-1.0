#bin/sh
export PATH=/opt/android/android-ndk-r11c:$PATH
ndk-build
cp -f ../libs/armeabi/libthSDK.so ../jniLibs/armeabi/
cp -f lib/libAVAPIs.so ../jniLibs/armeabi/
cp -f lib/libIOTCAPIs.so ../jniLibs/armeabi/
