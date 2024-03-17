#ifndef RESPONSE_MANAGER
#define RESPONSE_MANAGER

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/socket.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "../database/database.h"

typedef unsigned int UInt32;

typedef struct
{
    const char *id;
    const char *name;
    const char *is_smoothie;
    const char *icon_path;
    const char *price;
} Drink;

// response
void send_response(char *response, int sd);
void send_responseSSL(const char *response, SSL *ssl);
void handle_request(char *request, int sd, SSL *ssl);
// handlers
void handle_registration(char *token, SSL *ssl);
void handle_login(char *token, SSL *ssl);
void handle_update(char *token, SSL *ssl);
void handle_get_cart(char *token, SSL *ssl);
void handle_update_cart(char *token, SSL *ssl);
void handle_drink_details(char *token, SSL *ssl);
void handle_order(char *token, SSL *ssl);
void handle_suggestion(char *token, SSL *ssl);
void handle_add_payment(char *token, SSL *ssl);
void handle_remove_payment(char *token, SSL *ssl);
void handle_get_payment_methods(char *token, SSL *ssl);
void handle_all_drinks(char *token, SSL *ssl);
// utility functions for drinks
void send_icon(const char *icon_path, SSL *ssl);
void join_str(char *result, UInt32 *offset, const char *copy);
// void send_drink(SSL *ssl, const char *id, const char *name, const char *desc, const char *icon_path, const char *price);
void send_drink(SSL *ssl, const Drink *drink);
// void handle_insert_biometric(char *token, SSL *ssl);
// void handle_remove_biometric(char *token, SSL *ssl);
// void handle_login_biometric(char *token, SSL *ssl);

#endif