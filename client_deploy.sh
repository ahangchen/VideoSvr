rm -rf powerful
mkdir powerful
cp lib/libnvr.so powerful/libnvr.so
cp lib/nvrmng powerful/nvrmng
cp out/artifacts/VideoSvr_war_exploded/. -r powerful/VideoSvr
tar cvf powerful.tar.gz powerful
scp -r powerful.tar.gz cwh@222.201.145.237:/home/cwh
rm -rf powerful
ssh cwh@222.201.145.237