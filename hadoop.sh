
# download hadoop deb and src
# install hadoop deb and jdk-6
#wget http://mirror.olnevhost.net/pub/apache/hadoop/core/stable/hadoop-1.2.1.tar.gz
#wget http://mirror.olnevhost.net/pub/apache/hadoop/core/stable/hadoop_1.2.1-1_x86_64.deb
#sudo apt-get install -y openjdk-6-jdk
#sudo dpkg -i hadoop_1.2.1-1_x86_64.deb

# setup some variables
# location of jvm
# memory for jvm
#sudo sed -i 's|JAVA_HOME=/usr/lib/jvm/java-6-sun|JAVA_HOME=/usr/lib/jvm/java-6-openjdk-amd64/|' /etc/hadoop/hadoop-env.sh
#sudo sed -i 's|-Xmx128m|-Xmx1024m|' /etc/hadoop/hadoop-env.sh


# get examples from tar
#tar -xf hadoop-1.2.1.tar.gz
#cp hadoop-1.2.1/hadoop-examples-1.2.1.jar .


#mkdir input

echo "don't use this, use instead whats under hadoop/"
