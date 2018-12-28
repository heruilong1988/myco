#!/bin/bash

SERVICE_NAME="@project.artifactId@"
VERSION="@project.version@"
SERVICE_FULL_NAME=${SERVICE_NAME}-${VERSION}

PRO_BASE_DIR=$(cd `dirname $0`; pwd)/..
cd ${PRO_BASE_DIR}
PRO_PID_FILE=bin/running.pid
MAX_STOP_TIME_S=20

if [ -f "${PRO_PID_FILE}" ]; then
    if [ `ps -ef | grep $(cat "${PRO_PID_FILE}") | grep java | grep ${SERVICE_FULL_NAME} | wc -l` -gt 0 ]; then
        PID=`cat ${PRO_PID_FILE}`
        kill ${PID}
        COUNT=0
        while [ `ps -ef | grep ${PID} | grep java | grep ${SERVICE_FULL_NAME} | wc -l` -gt 0 ]; do
            sleep 1
            COUNT=`expr ${COUNT} + 1`
            echo "${COUNT} SECOND(S) ELAPSED..."
            if [ ${COUNT} -gt ${MAX_STOP_TIME_S} ]; then
                kill -9 ${PID}
                break
            fi
        done

        rm ${PRO_PID_FILE}
        echo "${SERVICE_FULL_NAME} STOPPED"
        exit 0
    fi
fi

echo "NO INSTANCE OF ${SERVICE_FULL_NAME} IS RUNNING"


