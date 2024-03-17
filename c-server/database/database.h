#ifndef DATABASE
#define DATABASE

#include <stdio.h>
#include <stdlib.h>
#include <mysql/mysql.h>
#include <mysql/my_command.h>
#include <string.h>
#include <stdbool.h>

#include "../sc_array.h"

#define USER_LOGIN_USERNAME_ERROR -1
#define USER_LOGIN_PASSWORD_ERROR -2

// database connection
int database_init(int argc, char **argv);
void database_close();
// request handler
int user_login(char *username, char *password);
bool user_create(char *username, char *password);
bool user_update(char *old_username, char *new_username, char *old_password, char *new_password);
MYSQL_RES *select_all_drinks();
bool update_user_cart(char *user_id, char *user_password, char *drinks[], int drinks_size);
bool insert_user_order(char *user_name, char *user_password, char *drinks[], int drinks_size);
MYSQL_RES *select_user_cart(char *user_id, char *user_password);
MYSQL_RES *get_drink_details(char *drink_id);
MYSQL_RES *get_suggested_drinks(char *user_name);
MYSQL_RES *get_user_payment_methods(char *user_name, char *password);
char *insert_payment_method(char *user_name, char *password, char *card_number, char *card_date, char *card_cvc);
bool remove_payment_method(char *user_name, char *password, char *card_id);
// bool insert_user_biometric(char *user_name, char *user_biometric);
// bool remove_user_biometric(char *user_name);
// MYSQL_RES *login_user_biometric(char *user_biometric);

#endif