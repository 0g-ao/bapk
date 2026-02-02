tar --exclude='.gradle' \
  --exclude='app/build' \
  --exclude='apk-output'  \
  -czf 'GeckoBrowser.tgz' . 

mcli cp GeckoBrowser.tgz s-ao/0gravity/GeckoBrowser.tgz

rm GeckoBrowser.tgz
