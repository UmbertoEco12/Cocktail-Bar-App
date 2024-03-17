#!/bin/bash
gcc -o main response_manager/response_manager.c socket/socket_multi.c database/database.c main.c -lmysqlclient -lssl -lcrypto
