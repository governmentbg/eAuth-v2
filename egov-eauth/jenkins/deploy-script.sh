#!/bin/bash

serviceName=$1
jarName=$2
jarNameService=$1".jar"


sudo service $serviceName stop 2> /dev/null

if [ $? -eq 0 ]
then
  echo "Stoping $serviceName service is ok"

else
  sudo systemctl daemon-reload 2> /dev/null
  echo "Stoping $serviceName service failed" >&2
  exit 1
fi


rm  $jarNameService 2> /dev/null

if [ $? -eq 0 ]
then
  echo "Deleting $serviceName service jar is ok"

else
  echo "Deleting $serviceName service failed" >&2
  exit 1
fi

sudo mv /home/bulsiadmin/$jarName /var/$serviceName/$jarNameService 2> /dev/null

if [ $? -eq 0 ]
then
  echo "Renaming $jarName jar is ok"
else
  echo "Renaming $jarName failed" >&2
  exit 1
fi
sudo chown $serviceName:$serviceName /var/$serviceName/$jarNameService 2> /dev/null

if [ $? -eq 0 ]
then
  echo "Change owner $serviceName jar is ok"
else
  echo "Change owner $serviceName failed" >&2
fi

sudo chmod 500 /var/$serviceName/$jarNameService 2> /dev/null
if [ $? -eq 0 ]
then
  echo "Change mode $serviceName jar is ok"
else
  echo "Change mode $serviceName failed" >&2
fi

sudo systemctl daemon-reload 2> /dev/null

if [ $? -eq 0 ]
then
  echo "Realoading $serviceName jar is ok"
else
  echo "Realoading $serviceName failed" >&2
  exit 1
fi

sudo service $serviceName start 2> /dev/null
if [ $? -eq 0 ]
then
  echo "Starting $serviceName is ok"
else
  echo "Starting $serviceName failed" >&2
  exit 1
fi
