sudo iptables -t nat -L
sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-ports 8080
# sudo mv /home/ubuntu/webapp-0.0.1-SNAPSHOT.jar /home/ubuntu/webapp
sudo mv /home/ubuntu/webapp/webapp.service  /etc/systemd/system
sudo systemctl daemon-reload