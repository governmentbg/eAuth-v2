#!/bin/bash



sudo service eauth-profile stop 2> /dev/null

if [ $? -eq 0 ]
then
  echo "Stoping eauth profile service is ok"

else
  sudo systemctl daemon-reload 2> /dev/null
  echo "Stoping eauth profile service failed" >&2
  exit 1
fi


rm eauth-profile.jar 2> /dev/null

if [ $? -eq 0 ]
then
  echo "Deleting eauth profile service jar is ok"

else
  echo "Deleting eauth profile service failed" >&2
  exit 1
fi

    sudo mv /home/bulsiadmin/eauth-profile-backend-0.0.1-SNAPSHOT.jar /var/eauth-profile/eauth-profile.jar 2> /dev/null

if [ $? -eq 0 ]
then
  echo "Renaming eauth profile service jar is ok"
else
  echo "Renaming eauth-profile-backend-0.0.1-SNAPSHOT.jar failed" >&2
  exit 1
fi
sudo chown eauth-profile:eauth-profile /var/eauth-profile/eauth-profile.jar 2> /dev/null

if [ $? -eq 0 ]
then
  echo "Change owner eauth profile service jar is ok"
else
  echo "Change owner eauth profile service failed" >&2
fi

sudo chmod 500 eauth-profile.jar 2> /dev/null
if [ $? -eq 0 ]
then
  echo "Change mode eauth profile service jar is ok"
else
  echo "Change mode eauth profile service failed" >&2
fi

sudo systemctl daemon-reload 2> /dev/null

if [ $? -eq 0 ]
then
  echo "Realoading eauth profile service jar is ok"
else
  echo "Realoading eauth profile service failed" >&2
  exit 1
fi

sudo service eauth-profile start 2> /dev/null
if [ $? -eq 0 ]
then
  echo "Starting eauth profile service is ok"
else
  echo "Starting eauth profile service failed" >&2
  exit 1
fi
