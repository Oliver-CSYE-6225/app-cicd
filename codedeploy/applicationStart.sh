
# sudo systemctl stop webapp
# sudo systemctl start webapp

sudo kill -9 `sudo lsof -t -i:8080`
sudo nohup java -jar /home/ubuntu/webapp/webapp-0.0.1-SNAPSHOT.jar >/home/ubuntu/log.txt 2>&1 &