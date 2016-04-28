rm -rf powerful
rm -rf software/tomcat/webapps/VideoSvr/WEB-INF.zip
rm -rf software/tomcat/webapps/VideoSvr/WEB-INF
tar xvf powerful.tar.gz
cp powerful/VideoSvr/. -r software/tomcat/webapps/VideoSvr
cp powerful/libnvr.so -r software/tomcat/webapps/VideoSvr/lib/libnvr.so
cp powerful/nvrmng -r software/tomcat/webapps/VideoSvr/lib/nvrmng
rm -rf powerful
./software/tomcat/bin/shutdown.sh
./software/tomcat/bin/startup.sh
