#!/bin/bash
 
#$1 is the instance IP
#$2 is the private key

# Script
echo "Starting deploy script"
 
# Scp to instance

scp /home/travis/build/JulienB-Epfl/Coronhackathon/back/build/libs/backend-0.0.1-SNAPSHOT.jar ec2-user@18.234.142.249:/home

exit 0
