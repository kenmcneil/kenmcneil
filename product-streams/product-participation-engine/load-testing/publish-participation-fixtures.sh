#!/bin/bash
# usage: ./publish-participation-fixtures.sh START_PARTICIPATION_ID COUNT

SERVICES_HOST=localhost:8080
# SERVICES_HOST=webservices-dev-1.build.internal:8080

USER_ID=2524 # jason

START_ID=$1
COUNT=$2

for (( id = START_ID; id < START_ID+COUNT; id++ ))
do
    echo -n "Publishing id $id ..."
    curl -X PUT -H "Content-Type: application/json" http://$SERVICES_HOST/contents/participation/item/$id/publish?userId=$USER_ID
    echo " done."
done
