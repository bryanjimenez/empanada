#!/bin/sh

# Install Oracle Java 1.7
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update && sudo apt-get install -y oracle-jdk7-installer

# SSH Configuration
sudo apt-get install -y openssh-server
ssh-keygen -t rsa -P "" -f "/home/ubuntu/.ssh/id_rsa"
cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys

# Test the SSH setup and to save your local machine’s host key fingerprint 
ssh localhost

# Agree to terms
#yes

# Disabling IPv6
sudo vim /etc/sysctl.conf
echo "" >> /etc/sysctl.conf
echo "# disable ipv6" >> /etc/sysctl.conf
echo "net.ipv6.conf.all.disable_ipv6 = 1" >> /etc/sysctl.conf
echo "net.ipv6.conf.default.disable_ipv6 = 1" >> /etc/sysctl.conf
echo "net.ipv6.conf.lo.disable_ipv6 = 1" >> /etc/sysctl.conf

# reboot
sudo shutdown -r now