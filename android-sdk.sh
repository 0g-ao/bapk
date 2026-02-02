#
# ~/bashrc
if [ "$(grep "ANDROID_HOME" android-sdk.sh)" ]; then
  echo ' export ANDROID_HOME="$HOME/Android/Sdk" ' >> ~/.bashrc
  echo ' export PATH="$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin" ' >> ~/.bashrc
  #
  wget -c https://dl.google.com/android/repository/commandlinetools-linux-14742923_latest.zip
  unzip -o commandlinetools-linux-14742923_latest.zip
  mkdir -p $ANDROID_HOME/cmdline-tools
  mv  cmdline-tools $ANDROID_HOME/cmdline-tools/latest
  rm -rf commandlinetools-linux-14742923_latest.zip
fi
source ~/.bashrc
yes | sdkmanager --licenses

