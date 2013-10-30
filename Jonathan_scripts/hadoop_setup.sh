#!/bin/sh

# Download Hadoop into directory
cd /usr/local
sudo wget http://apache.mirrors.pair.com/hadoop/common/stable/hadoop-1.2.1.tar.gz
sudo tar xzf hadoop-1.2.1.tar.gz
sudo mv hadoop-1.2.1 hadoop

# Change owner of all the files to ubuntu user
# Check if correct
sudo chown -R ubuntu hadoop

# Add the following lines to" 
# the end of the “$HOME/.bashrc” file of user ubuntu
echo "" >> ~/.bashrc
echo "# Set Hadoop-related environment variables" >> ~/.bashrc
echo "export HADOOP_HOME=/usr/local/hadoop" >> ~/.bashrc
echo "# Set JAVA_HOME (we will also configure JAVA_HOME directly for Hadoop later on)" >> ~/.bashrc
echo "export JAVA_HOME=/usr/lib/jvm/java-7-oracle" >> ~/.bashrc
echo "# Some convenient aliases and functions for running Hadoop-related commands" >> ~/.bashrc
echo "unalias fs &> /dev/null" >> ~/.bashrc
echo 'alias fs="hadoop fs"' >> ~/.bashrc
echo "unalias hls &> /dev/null" >> ~/.bashrc
echo 'alias hls="fs -ls"' >> ~/.bashrc
echo "# If you have LZO compression enabled in your Hadoop cluster and" >> ~/.bashrc
echo "# compress job outputs with LZOP (not covered in this tutorial):" >> ~/.bashrc
echo "# Conveniently inspect an LZOP compressed file from the command" >> ~/.bashrc
echo "# line; run via:" >> ~/.bashrc
echo "#" >> ~/.bashrc
echo "# $ lzohead /hdfs/path/to/lzop/compressed/file.lzo" >> ~/.bashrc
echo "#" >> ~/.bashrc
echo "# Requires installed 'lzop' command." >> ~/.bashrc
echo "#" >> ~/.bashrc
echo "lzohead () {" >> ~/.bashrc
echo "hadoop fs -cat $1 | lzop -dc | head -1000 | less" >> ~/.bashrc
echo "}" >> ~/.bashrc
echo "# Add Hadoop bin/ directory to PATH" >> ~/.bashrc
echo "export PATH=$PATH:$HADOOP_HOME/bin" >> ~/.bashrc

# Reload bash profile
source ~/.bashrc