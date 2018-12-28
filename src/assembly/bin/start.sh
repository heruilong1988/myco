#!/bin/bash

SERVICE_NAME="@project.artifactId@"
VERSION="@project.version@"
SERVICE_FULL_NAME=${SERVICE_NAME}-${VERSION}
SERVICE_LIB_NAME=${SERVICE_FULL_NAME}.jar

PRO_BASE_DIR=$(cd `dirname $0`; pwd)/..
cd ${PRO_BASE_DIR}
PRO_CONF_DIR=conf
PRO_LIB_DIR=lib
PRO_LOGS_DIR=logs
PRO_PID_FILE=bin/running.pid
PRO_NOHUP_FILE=${PRO_LOGS_DIR}/nohup.out

JOLOKIA_FILE=${PRO_LIB_DIR}/jolokia-jvm-1.3.1-agent.jar
JOLOKIA_OPTS="host=0.0.0.0,port=8778"

JAVA_OPTS="-server -Xms256m -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/"

unset OPTIND
while getopts "t" opt;
do
    case ${opt} in
        t)
            JAVA_OPTS="-server -Xms256m -Xmx512m"
            JOLOKIA_OPTS="host=0.0.0.0,port=1278"
            ;;
        \?)
            echo "unknown argument"
            exit 1
            ;;
    esac
done

START_SUCCESS_OUTPUT="The ${SERVICE_NAME} service started"
MAX_START_TIME_S=60

if [ -f "${PRO_PID_FILE}" ]; then
    if [ `ps -ef | grep $(cat "${PRO_PID_FILE}") | grep java | grep ${SERVICE_FULL_NAME} | wc -l` -gt 0 ]; then

        echo "${SERVICE_FULL_NAME} is running, PID:$(cat "${PRO_PID_FILE}")"
        exit 0
    fi
fi

if [ -f "${PRO_NOHUP_FILE}" ]; then
    mv ${PRO_NOHUP_FILE} ${PRO_NOHUP_FILE}.bak
fi

nohup java ${JAVA_OPTS} \
    -javaagent:${JOLOKIA_FILE}=${JOLOKIA_OPTS} \
    -Dbase.dir=${PRO_BASE_DIR} -Dconf.dir=${PRO_CONF_DIR} \
    -Dconfig.define.print.enabled=${CONFIG_DEFINE_PRINT_ENABLED} \
    -jar ${PRO_LIB_DIR}/${SERVICE_LIB_NAME} \
    > ${PRO_NOHUP_FILE} 2>&1 &

if [ $? -eq 0 ]; then
    if /bin/echo -n $! > "${PRO_PID_FILE}"; then
        echo "SLEEP 3 SECONDS THEN CHECK PID" && sleep 3
        if [ `ps -ef | grep $(cat "$PRO_PID_FILE") | grep java | grep ${SERVICE_FULL_NAME} | wc -l` -gt 0 ]; then
            COUNT=0
            while [ `cat "${PRO_NOHUP_FILE}" | grep "${START_SUCCESS_OUTPUT}" | wc -l` -eq 0 ]; do
                sleep 1
                COUNT=`expr ${COUNT} + 1`
                echo "${COUNT} SECOND(S) ELAPSED..."

                if [ ${COUNT} -eq ${MAX_START_TIME_S} ]; then
                    break
                fi
            done

            if [ ${COUNT} -eq ${MAX_START_TIME_S} ]; then
                echo "FAILED TO START ${SERVICE_FULL_NAME} AFTER ${MAX_START_TIME_S} SECONDS"
                kill -9 `cat ${PRO_PID_FILE}`
                rm ${PRO_PID_FILE}
                exit 1
            else
                cat ${PRO_NOHUP_FILE}
                echo ""
                echo "${SERVICE_FULL_NAME} STARTED, PID:$(cat "${PRO_PID_FILE}")"
            fi
        else
            echo "FAILED TO START ${SERVICE_FULL_NAME}"
            exit 1
        fi
    else
        echo "FAILED TO WRITE PID FILE"
        exit 1
    fi
else
    echo "${SERVICE_FULL_NAME} DID NOT START"
    exit 1
fi


