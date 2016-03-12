rm -rf powerful 
rm -rf software/tomcat/webapps/VideoSvr/WEB-INF.zip
rm -rf software/tomcat/webapps/VideoSvr/WEB-INF
tar xvf powerful.tar.gz
cp powerful/VideoSvr/. -r software/tomcat/webapps/VideoSvr
cp powerful/libnvr.so -r software/tomcat/webapps/VideoSvr/lib/libnvr.so
rm -rf powerful
