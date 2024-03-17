#include "database.h"
#include <pthread.h>

MYSQL *conn;
const char *DatabaseName = "ProgettoRetiCuscione";
const char *TablesName = "database/tables.sql";
// init the database

pthread_mutex_t conn_mutex;
// Function to acquire the mutex lock
void acquire_lock()
{
    if (pthread_mutex_lock(&conn_mutex) != 0)
    {
        fprintf(stderr, "Failed to acquire mutex lock\n");
        exit(EXIT_FAILURE);
    }
}

// Function to release the mutex lock
void release_lock()
{
    if (pthread_mutex_unlock(&conn_mutex) != 0)
    {
        fprintf(stderr, "Failed to release mutex lock\n");
        exit(EXIT_FAILURE);
    }
}

// Init the database
int database_init(int argc, char **argv)
{
    // Initialize the mutex
    pthread_mutexattr_t mutex_attr;
    pthread_mutexattr_init(&mutex_attr);
    pthread_mutexattr_settype(&mutex_attr, PTHREAD_MUTEX_RECURSIVE);
    if (pthread_mutex_init(&conn_mutex, &mutex_attr) != 0)
    {
        fprintf(stderr, "Failed to initialize mutex\n");
        exit(EXIT_FAILURE);
    }

    // Init SQL connection for creating the database;
    pthread_mutex_lock(&conn_mutex);
    conn = mysql_init(conn);
    pthread_mutex_unlock(&conn_mutex);

    if (conn == NULL)
    {
        printf("Error %u %s \n", mysql_errno(conn), mysql_error(conn));
        exit(EXIT_FAILURE);
    }

    puts("Connecting");
    if (!(mysql_real_connect(conn, NULL, "root", "root", NULL, 0, NULL, 0)))
    {
        fprintf(stderr, "Failed to connect to MySql: Error: %s\n", mysql_error(conn));
        exit(EXIT_FAILURE);
    }
    puts("Connected");
    char *c_s = "CREATE DATABASE IF NOT EXISTS ";
    char *create_database = malloc(strlen(c_s) + strlen(DatabaseName) + strlen(";") + 1);
    strcpy(create_database, c_s);
    strcat(create_database, DatabaseName);
    strcat(create_database, ";");

    acquire_lock();
    if (mysql_query(conn, create_database) < 0)
    {
        fprintf(stderr, "Failed to create Database: Error: %s\n", mysql_error(conn));
        exit(EXIT_FAILURE);
    }
    mysql_select_db(conn, DatabaseName);
    release_lock();

    FILE *fptr;
    long file_size;
    fptr = fopen(TablesName, "r");
    if (fptr == NULL)
    {
        perror("Error reading file");
        exit(EXIT_FAILURE);
    }
    fseek(fptr, 0, SEEK_END);
    file_size = ftell(fptr);
    rewind(fptr);

    char *str;
    str = (char *)malloc(sizeof(char) * (file_size + 1));
    fread(str, sizeof(char), file_size, fptr);
    str[file_size] = '\0';
    fclose(fptr);

    acquire_lock();
    mysql_set_server_option(conn, CLIENT_MULTI_STATEMENTS);
    if (mysql_query(conn, str) < 0)
    {
        fprintf(stderr, "Failed to create Tables: %s\n", mysql_error(conn));
        free(str);
        exit(EXIT_FAILURE);
    }
    release_lock();

    free(str);
    acquire_lock();
    int status;
    do
    {
        MYSQL_RES *result = mysql_store_result(conn);
        if (result)
        {
            mysql_free_result(result);
        }
        else
        {
            if (mysql_field_count(conn) != 0)
            {
                printf("Could not retrieve result set\n");
                break;
            }
        }
        if ((status = mysql_next_result(conn)) > 0)
            printf("Could not execute statement\n");
    } while (status == 0);
    release_lock();

    return EXIT_SUCCESS;
}

// returns true if the user exists
bool user_exists(char *username)
{
    acquire_lock();

    char *query = malloc(strlen("SELECT user_name FROM users_table WHERE user_name=''") + strlen(username) + 1);
    sprintf(query, "SELECT user_name FROM users_table WHERE user_name='%s'", username);
    MYSQL_RES *res;
    MYSQL_ROW row;

    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        free(query);
        release_lock();
        return 1;
    }

    res = mysql_store_result(conn);
    if (res == NULL)
    {
        printf("Error retrieving result set: %s\n", mysql_error(conn));
        free(query);
        release_lock();

        return 1;
    }

    int num_fields = mysql_num_fields(res);
    // if there are results then it exists
    if ((row = mysql_fetch_row(res)))
    {
        mysql_free_result(res);
        free(query);
        release_lock();

        return true;
    }
    mysql_free_result(res);
    free(query);
    release_lock();

    return false;
}

// returns true (1) if ok
// 0 if sql error
// -1 if username doesn t exist
// -2 if the password is incorrect
int user_login(char *username, char *password)
{
    acquire_lock();

    MYSQL_RES *res;
    MYSQL_ROW row;
    char *query = malloc(strlen("SELECT user_name,user_password FROM users_table WHERE user_name='' AND user_password=''") + strlen(username) + strlen(password) + 1);
    sprintf(query, "SELECT user_name,user_password FROM users_table WHERE user_name='%s' AND user_password='%s'", username, password);

    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        free(query);
        release_lock();

        return false;
    }

    res = mysql_store_result(conn);
    if (res == NULL)
    {
        printf("Error retrieving result set: %s\n", mysql_error(conn));
        free(query);
        release_lock();

        return false;
    }

    int num_fields = mysql_num_fields(res);
    // if there are results then it exists
    if ((row = mysql_fetch_row(res)))
    {
        mysql_free_result(res);
        free(query);
        release_lock();

        return true;
    }

    // try searching only the username
    if (user_exists(username) == true)
    {
        free(query);
        release_lock();

        return USER_LOGIN_PASSWORD_ERROR;
    }

    mysql_free_result(res);
    free(query);
    release_lock();

    return USER_LOGIN_USERNAME_ERROR;
}

// creates a new user with username password
// returns false if a username with this name is already present
bool user_create(char *username, char *password)
{
    acquire_lock();

    char *query = malloc(strlen("INSERT INTO users_table (user_name, user_password) VALUES ('','')") + strlen(username) + strlen(password) + 1);
    sprintf(query, "INSERT INTO users_table (user_name, user_password) VALUES ('%s','%s')", username, password);

    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        free(query);
        release_lock();

        return false;
    }
    mysql_store_result(conn);
    free(query);
    release_lock();

    return true;
}

// updates the user username and/or password
bool user_update(char *old_username, char *new_username, char *old_password, char *new_password)
{
    acquire_lock();

    char *query = malloc(strlen("Update users_table Set user_name ='', user_password='' where user_name = '' and user_password = ''") + strlen(new_username) + strlen(old_username) + strlen(new_password) + strlen(old_password) + 1);
    sprintf(query, "Update users_table Set user_name ='%s', user_password='%s' where user_name = '%s' and user_password = '%s'", new_username, new_password, old_username, old_password);

    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        free(query);
        release_lock();

        return false;
    }
    mysql_store_result(conn);
    free(query);
    release_lock();

    return true;
}

// returns the user id in *user_id from the username
bool get_user_id(char *username, int *user_id)
{
    acquire_lock();

    MYSQL_RES *res;
    MYSQL_ROW row;
    char *query = malloc(strlen("SELECT user_id FROM users_table WHERE user_name=''") + strlen(username) + 1);
    sprintf(query, "SELECT user_id FROM users_table WHERE user_name='%s'", username);
    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        release_lock();

        return false;
    }
    res = mysql_store_result(conn);
    if (res == NULL)
    {
        printf("Error retrieving result set: %s\n", mysql_error(conn));
        release_lock();

        return false;
    }

    int num_fields = mysql_num_fields(res);
    // if there are results then it exists
    if ((row = mysql_fetch_row(res)))
        *user_id = atoi(row[0]);
    else
    {
        printf("No user with username %s\n", username);
        mysql_free_result(res);
        free(query);
        release_lock();

        return false;
    }
    mysql_free_result(res);
    free(query);
    release_lock();

    return true;
}

// check if the user password combination is correct
bool check_user_password(char *username, char *password)
{
    acquire_lock();

    MYSQL_RES *res;
    MYSQL_ROW row;
    char *query = malloc(strlen("SELECT user_id FROM users_table WHERE user_name='' and user_password = ''") + strlen(username) + strlen(password) + 1);
    sprintf(query, "SELECT user_id FROM users_table WHERE user_name='%s' and user_password = '%s'", username, password);
    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        release_lock();

        return 1;
    }
    res = mysql_store_result(conn);
    if (res == NULL)
    {
        printf("Error retrieving result set: %s\n", mysql_error(conn));
        release_lock();

        return 1;
    }

    int num_fields = mysql_num_fields(res);
    // if there are no results
    if (!(row = mysql_fetch_row(res)))
    {
        printf("password fail username: %s, password: %s\n", username, password);
        mysql_free_result(res);
        free(query);
        release_lock();

        return false;
    }
    mysql_free_result(res);
    free(query);
    release_lock();

    return true;
}

// return a mysql resource containing all the drinks
MYSQL_RES *select_all_drinks()
{
    acquire_lock();

    MYSQL_RES *res;
    char *query = "SELECT drink_id, drink_name, is_smoothie, drink_icon_path, drink_price FROM drinks_table";

    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        release_lock();
        return NULL;
    }
    res = mysql_store_result(conn);
    if (res == NULL)
    {
        printf("Error retrieving result set: %s\n", mysql_error(conn));
        release_lock();
        return NULL;
    }
    release_lock();
    return res;
}

// updates the user cart
bool update_user_cart(char *user_name, char *user_password, char *drinks[], int drinks_size)
{
    // check password
    if (check_user_password(user_name, user_password) == false)
    {
        printf("Wrong password");

        return false;
    }
    acquire_lock();
    int user_id;
    get_user_id(user_name, &user_id);
    char str[10];
    sprintf(str, "%d", user_id);
    char *query = malloc(strlen("delete from user_cart where user_id = ;") + strlen(str) + 1);
    sprintf(query, "delete from user_cart where user_id = %s;", str);

    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        free(query);
        release_lock();
        return false;
    }
    mysql_store_result(conn);
    free(query);
    // if drinks is not null insert into cart
    if (drinks != NULL)
    {
        for (int i = 0; i < drinks_size; i++)
        {
            query = malloc(strlen("insert into user_cart(user_id,drink_id) values(,);") + strlen(str) + strlen(drinks[i]) + 1);
            sprintf(query, "insert into user_cart(user_id,drink_id) values(%s,%s);", str, drinks[i]);
            if (mysql_query(conn, query))
            {
                printf("Error executing query: %s\n", mysql_error(conn));
                release_lock();
                return false;
            }
            mysql_store_result(conn);
            free(query);
        }
    }
    release_lock();
    return true;
}

// inserts a new order for the user
bool insert_user_order(char *user_name, char *user_password, char *drinks[], int drinks_size)
{
    // check password
    if (check_user_password(user_name, user_password) == false)
    {
        printf("Wrong password");
        return false;
    }
    acquire_lock();
    int user_id;
    get_user_id(user_name, &user_id);
    char str[10];
    sprintf(str, "%d", user_id);
    MYSQL_RES *res;
    char *query;
    // if drinks is not null insert into cart
    if (drinks != NULL)
    {
        for (int i = 0; i < drinks_size; i++)
        {
            query = malloc(strlen("insert into user_order(user_id,drink_id) values(,);") + strlen(str) + strlen(drinks[i]) + 1);
            sprintf(query, "insert into user_order(user_id,drink_id) values(%s,%s);", str, drinks[i]);
            if (mysql_query(conn, query))
            {
                printf("Error executing query: %s\n", mysql_error(conn));
                release_lock();
                return false;
            }
            mysql_store_result(conn);
            free(query);
        }
    }
    release_lock();
    return true;
}

// returns a mysql resources containing the drinks ids of the user cart
MYSQL_RES *select_user_cart(char *user_name, char *user_password)
{

    // check password
    if (check_user_password(user_name, user_password) == false)
    {
        printf("Wrong password");
        return NULL;
    }
    acquire_lock();
    int user_id;
    get_user_id(user_name, &user_id);
    char str[10];
    sprintf(str, "%d", user_id);
    MYSQL_RES *res;
    char *query = malloc(strlen("select drink_id from user_cart where user_id = ;") + strlen(str) + 1);
    sprintf(query, "select drink_id from user_cart where user_id = %s;", str);

    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        free(query);
        release_lock();
        return NULL;
    }
    res = mysql_store_result(conn);
    if (res == NULL)
    {
        printf("Error retrieving result set: %s\n", mysql_error(conn));
        free(query);
        release_lock();
        return NULL;
    }
    free(query);
    release_lock();
    return res;
}

// returns a mysql resource containing the details of the drink
MYSQL_RES *get_drink_details(char *drink_id)
{
    acquire_lock();

    MYSQL_RES *res;
    char *query = malloc(strlen("SELECT drink_description, tags.tag_name, ingredients.ingredient_name \
                  FROM drinks_table \
                  JOIN drink_tags ON drinks_table.drink_id = drink_tags.drink_id \
                  JOIN tags ON drink_tags.tag_id = tags.tag_id \
                  JOIN drink_ingredients ON drink_ingredients.drink_id = drinks_table.drink_id \
                  JOIN ingredients ON drink_ingredients.ingredient_id = ingredients.ingredient_id \
                  WHERE drinks_table.drink_id = ;") +
                         strlen(drink_id) + 1);
    sprintf(query, "SELECT drink_description, tags.tag_name, ingredients.ingredient_name \
                  FROM drinks_table \
                  JOIN drink_tags ON drinks_table.drink_id = drink_tags.drink_id \
                  JOIN tags ON drink_tags.tag_id = tags.tag_id \
                  JOIN drink_ingredients ON drink_ingredients.drink_id = drinks_table.drink_id \
                  JOIN ingredients ON drink_ingredients.ingredient_id = ingredients.ingredient_id \
                  WHERE drinks_table.drink_id = %s;",
            drink_id);

    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        free(query);
        release_lock();
        return NULL;
    }
    res = mysql_store_result(conn);
    if (res == NULL)
    {
        printf("Error retrieving result set: %s\n", mysql_error(conn));
        free(query);
        release_lock();
        return NULL;
    }
    free(query);
    release_lock();
    return res;
}

// returns the (at most 3) suggested drinks for the user, if any
MYSQL_RES *get_suggested_drinks(char *user_name)
{
    acquire_lock();

    int user_id;
    get_user_id(user_name, &user_id);
    char str[10];
    sprintf(str, "%d", user_id);
    MYSQL_RES *res;
    char *query = malloc(strlen("select d.drink_id, s.occurences \
                                from drinks_table d join drink_tags dt on\
                                d.drink_id = dt.drink_id\
                                join \
                                (select tags.tag_id, tag_name, count(*) as occurences \
                                from (user_order inner join drinks_table on user_order.drink_id = drinks_table.drink_id) \
                                join drink_tags on drinks_table.drink_id = drink_tags.drink_id \
                                join tags on drink_tags.tag_id = tags.tag_id\
                                where user_id = \
                                group by(tag_name)\
                                ) s on dt.tag_id = s.tag_id\
                                group by d.drink_id, s.occurences\
                                order by occurences desc\
                                limit 3;") +
                         strlen(str) + 1);
    sprintf(query, "select d.drink_id, s.occurences \
                    from drinks_table d join drink_tags dt on\
                    d.drink_id = dt.drink_id\
                    join \
                    (select tags.tag_id, tag_name, count(*) as occurences \
                    from (user_order inner join drinks_table on user_order.drink_id = drinks_table.drink_id) \
                    join drink_tags on drinks_table.drink_id = drink_tags.drink_id \
                    join tags on drink_tags.tag_id = tags.tag_id\
                    where user_id = %s\
                    group by(tag_name)\
                    ) s on dt.tag_id = s.tag_id\
                    group by d.drink_id, s.occurences\
                    order by occurences desc\
                    limit 3;",
            str);

    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        free(query);
        release_lock();
        return NULL;
    }
    res = mysql_store_result(conn);
    if (res == NULL)
    {
        printf("Error retrieving result set: %s\n", mysql_error(conn));
        free(query);
        release_lock();
        return NULL;
    }
    free(query);
    release_lock();
    return res;
}

// adds a payment method for the user
char *insert_payment_method(char *user_name, char *password, char *card_number, char *card_date, char *card_cvc)
{

    // check password
    if (check_user_password(user_name, password) == false)
    {
        printf("Wrong password");
        return NULL;
    }
    acquire_lock();
    MYSQL_RES *res;
    MYSQL_ROW row;
    int user_id;
    get_user_id(user_name, &user_id);
    char str[10];
    sprintf(str, "%d", user_id);
    char *query = malloc(strlen("select insert_payment_method(,'','','')") +
                         strlen(str) + strlen(card_number) + strlen(card_date) + strlen(card_cvc) + 1);
    sprintf(query, "select insert_payment_method(%s,'%s','%s','%s')",
            str, card_number, card_date, card_cvc);
    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        release_lock();
        return NULL;
    }
    res = mysql_store_result(conn);
    if (res == NULL)
    {
        printf("Error retrieving result set: %s\n", mysql_error(conn));
        free(query);
        release_lock();
        return NULL;
    }
    row = mysql_fetch_row(res);
    if (row != NULL)
    {
        free(query);
        mysql_free_result(res);
        release_lock();
        // if is null than this card is already saved
        if (row[0] == NULL)
        {
            return "NULL";
        }
        return row[0];
    }
    free(query);
    mysql_free_result(res);
    release_lock();
    return NULL;
}

// removes a payment method for the user
bool remove_payment_method(char *user_name, char *password, char *card_id)
{

    // check password
    if (check_user_password(user_name, password) == false)
    {
        printf("Wrong password");
        return NULL;
    }
    acquire_lock();
    int user_id;
    get_user_id(user_name, &user_id);
    char str[10];
    sprintf(str, "%d", user_id);
    char *query = malloc(strlen("delete from user_payment_method where user_id =  and card_id = ;") +
                         strlen(str) + strlen(card_id) + 1);
    sprintf(query, "delete from user_payment_method where user_id = %s and card_id = %s;", str, card_id);
    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        release_lock();
        return false;
    }
    mysql_store_result(conn);
    free(query);
    release_lock();
    return true;
}

// returns a mysql resource containing the user payment methods
MYSQL_RES *get_user_payment_methods(char *user_name, char *password)
{

    // check password
    if (check_user_password(user_name, password) == false)
    {
        printf("Wrong password");
        return NULL;
    }
    acquire_lock();
    int user_id;
    get_user_id(user_name, &user_id);
    char str[10];
    sprintf(str, "%d", user_id);
    MYSQL_RES *res;
    // char *query = malloc(strlen("select card_number, card_date, card_cvc from user_payment_method where user_id = ") + strlen(str) + 1);
    // sprintf(query, "select card_number, card_date, card_cvc from user_payment_method where user_id = %s", str);

    char *query = malloc(strlen("SELECT upm.card_id, CONCAT('**** **** **** ', RIGHT(card_number, 4)) AS card_number,\
                                card_date\
                                FROM payment_methods pm\
                                JOIN user_payment_method upm ON pm.card_id = upm.card_id\
                                WHERE upm.user_id = ;") +
                         strlen(str) + 1);
    sprintf(query, "SELECT upm.card_id, CONCAT('**** **** **** ', RIGHT(card_number, 4)) AS card_number,\
                                card_date\
                                FROM payment_methods pm\
                                JOIN user_payment_method upm ON pm.card_id = upm.card_id\
                                WHERE upm.user_id = %s;",
            str);
    if (mysql_query(conn, query))
    {
        printf("Error executing query: %s\n", mysql_error(conn));
        free(query);
        release_lock();
        return NULL;
    }
    res = mysql_store_result(conn);
    if (res == NULL)
    {
        printf("Error retrieving result set: %s\n", mysql_error(conn));
        free(query);
        release_lock();
        return NULL;
    }
    free(query);
    release_lock();
    return res;
}

// closes the database
// Closes the database
void database_close()
{
    acquire_lock();

    mysql_close(conn);

    release_lock();

    // Destroy the mutex
    pthread_mutex_destroy(&conn_mutex);
}