#!/bin/bash
# usage: ./publish-participation-fixtures.sh COUNT

SERVICES_HOST=localhost:8080
# SERVICES_HOST=webservices-dev-1.build.internal:8080

USER_ID=2524 # jason

START_ID=50000
COUNT=$1

for (( id = 50000; id < START_ID+COUNT; id++ ))
do
    echo -n "Publishing id $id ..."
    curl -X PUT -H "Content-Type: application/json" http://$SERVICES_HOST/contents/participation/item/$id/publish?userId=$USER_ID
    echo " done."
done
