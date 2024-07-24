echo $PWD
# cp $PWD/target/*.jar /home/fuentes/target
cd /home/fuentes/gateway-proxy
echo $PWD
docker build -t gateway-proxy:1.0.1 .