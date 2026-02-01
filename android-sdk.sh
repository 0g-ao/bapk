#
# android-sdk.sh
source ~/.bashrc
if [ ! "$ANDROID_HOME" ]; then
# ~/bashrc
# export ANDROID_HOME="$HOME/Android/sdk"
# export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin"
##
wget -c https://dl.google.com/android/repository/commandlinetools-linux-14742923_latest.zip
unzip commandlinetools-linux-14742923_latest.zip
##
mkdir -p $ANDROID_HOME/cmdline-tools
mv  cmdline-tools $ANDROID_HOME/cmdline-tools/latest
fi
sudo apt install -y default-jdk
yes | sdkmanager --licenses
