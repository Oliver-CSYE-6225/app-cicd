# sudo iptables -t nat -L
# sudo iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-ports 8080
# sudo mv /home/ubuntu/webapp-0.0.1-SNAPSHOT.jar /home/ubuntu/webapp
sudo mv /home/ubuntu/webapp/webapp.service  /etc/systemd/system
sudo systemctl daemon-reload
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/ubuntu/webapp/webapp_cloudwatch_config.json -s
sudo systemctl start amazon-cloudwatch-agent