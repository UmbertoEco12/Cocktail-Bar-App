#include "response_manager.h"

// search for a request handler, if found runs that handler
void handle_request(char *request, int sd, SSL *ssl)
{
    char *r = malloc(strlen(request) + 1);
    strcpy(r, request);
    char *token = strtok(r, "\n");
    if (token != NULL)
    {
        printf("request: %s\n", token);
        // user info
        if (strcmp(token, "Registration") == 0)
        {
            handle_registration(token, ssl);
        }
        else if (strcmp(token, "Login") == 0)
        {
            handle_login(token, ssl);
        }
        else if (strcmp(token, "Update") == 0)
        {
            handle_update(token, ssl);
        }
        // all drinks
        else if (strcmp(token, "Drink") == 0)
        {
            handle_all_drinks(token, ssl);
        }
        // drink details
        else if (strcmp(token, "DrinkDetails") == 0)
        {
            handle_drink_details(token, ssl);
        }
        else if (strcmp(token, "UpdateCart") == 0)
        {
            handle_update_cart(token, ssl);
        }
        else if (strcmp(token, "GetCart") == 0)
        {
            handle_get_cart(token, ssl);
        }
        else if (strcmp(token, "Order") == 0)
        {
            handle_order(token, ssl);
        }
        else if (strcmp(token, "Suggest") == 0)
        {
            handle_suggestion(token, ssl);
        }
        else if (strcmp(token, "AddPayment") == 0)
        {
            handle_add_payment(token, ssl);
        }
        else if (strcmp(token, "RemovePayment") == 0)
        {
            handle_remove_payment(token, ssl);
        }
        else if (strcmp(token, "GetPayment") == 0)
        {
            handle_get_payment_methods(token, ssl);
        }
    }
    free(r);
}

// simple function that sends a response to the client
// first the message length is sent, than the message
void send_responseSSL(const char *response, SSL *ssl)
{
    uint32_t length = strlen(response);
    uint32_t len_net = htonl(length);
    SSL_write(ssl, (void *)(&len_net), sizeof(uint32_t));
    SSL_write(ssl, response, strlen(response));
}

// handler for the registration request
void handle_registration(char *token, SSL *ssl)
{
    // username
    token = strtok(NULL, "\n");
    char *username = malloc(strlen(token) + 1);
    strcpy(username, token);
    // password
    token = strtok(NULL, "\n");
    char *password = malloc(strlen(token) + 1);
    strcpy(password, token);
    printf("username: %s, password: %s\n", username, password);

    if (user_create(username, password) == true)
    {
        // success
        char *response = "OK";
        send_responseSSL(response, ssl);
    }
    else
    {
        // failed
        char *response = "NO";
        send_responseSSL(response, ssl);
    }
}
// handler for the login request
void handle_login(char *token, SSL *ssl)
{
    // username
    token = strtok(NULL, "\n");
    char *username = malloc(strlen(token) + 1);
    strcpy(username, token);
    // password
    token = strtok(NULL, "\n");
    char *password = malloc(strlen(token) + 1);
    strcpy(password, token);
    printf("username: %s, password: %s\n", username, password);
    int r = user_login(username, password);
    if (r == true)
    {
        // success
        char *response = "OK";
        send_responseSSL(response, ssl);
    }
    else if (r == USER_LOGIN_USERNAME_ERROR)
    {
        // failed
        char *response = "WRONG_USERNAME";
        send_responseSSL(response, ssl);
    }
    else
    {
        // failed
        char *response = "WRONG_PASSWORD";
        send_responseSSL(response, ssl);
    }
}
// handler for the user info update request
void handle_update(char *token, SSL *ssl)
{
    // old username
    token = strtok(NULL, "\n");
    char *old_username = malloc(strlen(token) + 1);
    strcpy(old_username, token);
    // username
    token = strtok(NULL, "\n");
    char *username = malloc(strlen(token) + 1);
    strcpy(username, token);
    // old password
    token = strtok(NULL, "\n");
    char *old_password = malloc(strlen(token) + 1);
    strcpy(old_password, token);
    // password
    token = strtok(NULL, "\n");
    char *password = malloc(strlen(token) + 1);
    strcpy(password, token);
    printf("old username %s,username: %s, old password: %s, password: %s\n", old_username, username, old_password, password);

    if (user_update(old_username, username, old_password, password) == true)
    {
        // success
        char *response = "OK";
        send_responseSSL(response, ssl);
    }
    else
    {
        // failed
        char *response = "NO";
        send_responseSSL(response, ssl);
    }
}

// reads the icon from the local fileSystem (path saved in the db)
// then sends the raw data to the client
void send_icon(const char *icon_path, SSL *ssl)
{
    FILE *fptr;
    unsigned char *buffer;
    uint32_t filelen;
    fptr = fopen(icon_path, "rb");
    if (fptr == NULL)
    {
        // printf("Cannot open file %s\n", icon_path);
        // printf("error: %d\n", ferror(fptr));
        perror("Error:");
        // send no icon info
        return; // "No icon";
    }

    // Get file length
    fseek(fptr, 0, SEEK_END);
    filelen = ftell(fptr);
    rewind(fptr);

    // Allocate memory for buffer
    buffer = (unsigned char *)malloc((filelen + 1) * sizeof(unsigned char));
    // Read contents from file into buffer
    fread(buffer, filelen, 1, fptr);

    // Null-terminate buffer
    buffer[filelen] = '\0';
    uint32_t file_len_net = htonl(filelen);
    // send length
    SSL_write(ssl, (void *)(&file_len_net), sizeof(uint32_t));
    // send byte data // all at once
    SSL_write(ssl, buffer, filelen);
    // send byte data in chunks
    // size_t bytes_sent = 0;
    // while (bytes_sent < filelen)
    // {
    //     size_t chunk_size = (filelen - bytes_sent < CHUNK_SIZE) ? filelen - bytes_sent : CHUNK_SIZE;
    //     SSL_write(ssl, (void *)(&chunk_size), sizeof(UInt32));
    //     SSL_write(ssl, buffer + bytes_sent, chunk_size);
    //     bytes_sent += chunk_size;
    // }
    fclose(fptr);
    free(buffer);
}

// copies string copy in result starting from offset index
void join_str(char *result, UInt32 *offset, const char *copy)
{
    UInt32 length = 0;
    length = strlen(copy);
    memcpy(result + *offset, copy, length);
    *offset += length;
}

// send a single drink to the client
void send_drink(SSL *ssl, const Drink *drink)
{
    // Send the ID
    send_responseSSL(drink->id, ssl);
    // Send the name
    send_responseSSL(drink->name, ssl);
    // Send the is_smoothie flag
    send_responseSSL(drink->is_smoothie, ssl);
    // Send the price
    send_responseSSL(drink->price, ssl);
    // Send the icon data
    send_icon(drink->icon_path, ssl);
}

// handler for the get all drinks request
void handle_all_drinks(char *token, SSL *ssl)
{
    // get query result
    MYSQL_RES *res = select_all_drinks();
    MYSQL_ROW row;
    //  send drinks count
    uint32_t count = mysql_num_rows(res);
    uint32_t count_net = htonl(count);
    SSL_write(ssl, (void *)(&count_net), sizeof(uint32_t));
    int i = 0;
    // loop on results
    while ((row = mysql_fetch_row(res)))
    {
        Drink drink;
        drink.id = row[0];
        drink.name = row[1];
        drink.is_smoothie = row[2];
        drink.icon_path = row[3];
        drink.price = row[4];
        send_drink(ssl, &drink);
        i++;
    }
    mysql_free_result(res);
}

// handler for the get drink details request
// sends only the details of the requested drink
void handle_drink_details(char *token, SSL *ssl)
{
    // drink id
    token = strtok(NULL, "\n");
    // execute query
    MYSQL_RES *res;
    MYSQL_ROW row;
    res = get_drink_details(token);
    int i = 0;
    //  send drinks count
    uint32_t count = mysql_num_rows(res);
    uint32_t count_net = htonl(count);
    SSL_write(ssl, (void *)(&count_net), sizeof(uint32_t));
    // loop on results
    while ((row = mysql_fetch_row(res)))
    {
        if (i == 0)
        {
            // send description
            send_responseSSL(row[0], ssl);
        }
        // send tag
        send_responseSSL(row[1], ssl);
        // send ingredient
        send_responseSSL(row[2], ssl);
        i++;
    }
    mysql_free_result(res);
}

// handler for the update user cart request
void handle_update_cart(char *token, SSL *ssl)
{
    // user name
    token = strtok(NULL, "\n");
    char *user_id = malloc(strlen(token) + 1);
    strcpy(user_id, token);
    // password
    token = strtok(NULL, "\n");
    char *psw = malloc(strlen(token) + 1);
    strcpy(psw, token);
    // drinks count
    token = strtok(NULL, "\n");
    int drinks_count = atoi(token);
    if (drinks_count == 0)
    {
        update_user_cart(user_id, psw, NULL, drinks_count);
    }
    else
    {
        // init char* []
        char **drinks = malloc(sizeof(char *) * drinks_count);
        // drinks
        for (int i = 0; i < drinks_count; i++)
        {
            token = strtok(NULL, "\n");
            drinks[i] = malloc(strlen(token) + 1);
            strcpy(drinks[i], token);
        }
        // call query
        update_user_cart(user_id, psw, drinks, drinks_count);
        free(drinks);
    }
    free(user_id);
    free(psw);
}

// handler for the get user cart request
void handle_get_cart(char *token, SSL *ssl)
{
    // user name
    token = strtok(NULL, "\n");
    char *user_id = malloc(strlen(token) + 1);
    strcpy(user_id, token);
    // password
    token = strtok(NULL, "\n");
    char *psw = malloc(strlen(token) + 1);
    strcpy(psw, token);
    // get drinks from db
    MYSQL_RES *res;
    res = select_user_cart(user_id, psw);

    if (res == NULL)
    {
        send_responseSSL("NO", ssl);
        return;
    }
    else
    {
        send_responseSSL("OK", ssl);
    }

    // send drinks
    MYSQL_ROW row;
    //  send drinks count
    uint32_t count = mysql_num_rows(res);
    uint32_t count_net = htonl(count);
    SSL_write(ssl, (void *)(&count_net), sizeof(uint32_t));

    // loop on results
    while ((row = mysql_fetch_row(res)))
    {
        send_responseSSL(row[0], ssl);
    }
    mysql_free_result(res);
}

// handler for the order request
// adds an order to the user
void handle_order(char *token, SSL *ssl)
{
    // user name
    token = strtok(NULL, "\n");
    char *user_id = malloc(strlen(token) + 1);
    strcpy(user_id, token);
    // password
    token = strtok(NULL, "\n");
    char *psw = malloc(strlen(token) + 1);
    strcpy(psw, token);

    // check payment option
    token = strtok(NULL, "\n");
    char *card_method = malloc(strlen(token) + 1);
    strcpy(card_method, token);
    if (strcmp(card_method, "S") == 0)
    {
        token = strtok(NULL, "\n");
        char *card_id = malloc(strlen(token) + 1);
        strcpy(card_id, token);
        printf("User is paying with a saved card with id %s", card_id);
    }
    else
    {
        token = strtok(NULL, "\n");
        char *card_number = malloc(strlen(token) + 1);
        strcpy(card_number, token);
        token = strtok(NULL, "\n");
        char *card_date = malloc(strlen(token) + 1);
        strcpy(card_date, token);
        token = strtok(NULL, "\n");
        char *card_cvc = malloc(strlen(token) + 1);
        strcpy(card_cvc, token);
        printf("User is paying with a new card n: %s d: %s cvc: %s", card_number, card_date, card_cvc);
    }

    // drinks count
    token = strtok(NULL, "\n");
    int drinks_count = atoi(token);
    bool failed = false;
    if (drinks_count > 0)
    {
        // init char* []
        char **drinks = malloc(sizeof(char *) * drinks_count);
        // drinks
        for (int i = 0; i < drinks_count; i++)
        {
            token = strtok(NULL, "\n");
            drinks[i] = malloc(strlen(token) + 1);
            strcpy(drinks[i], token);
        }
        // call query
        if (insert_user_order(user_id, psw, drinks, drinks_count) == false && failed == false)
        {
            failed = true;
        }
        free(drinks);
    }
    if (failed == true)
    {
        send_responseSSL("NO", ssl);
    }
    else
    {
        send_responseSSL("OK", ssl);
    }
    free(user_id);
}

// handler for the get user suggestions request
void handle_suggestion(char *token, SSL *ssl)
{
    // user name
    token = strtok(NULL, "\n");

    MYSQL_RES *res;
    res = get_suggested_drinks(token);
    // send drinks
    MYSQL_ROW row;
    //  send drinks count
    uint32_t count = mysql_num_rows(res);
    uint32_t count_net = htonl(count);
    SSL_write(ssl, (void *)(&count_net), sizeof(uint32_t));
    // loop on results
    while ((row = mysql_fetch_row(res)))
    {
        puts(row[0]);
        send_responseSSL(row[0], ssl);
    }
    mysql_free_result(res);
}

// handler for the add payment method request
void handle_add_payment(char *token, SSL *ssl)
{
    // user name
    token = strtok(NULL, "\n");
    char *user_id = malloc(strlen(token) + 1);
    strcpy(user_id, token);
    // password
    token = strtok(NULL, "\n");
    char *psw = malloc(strlen(token) + 1);
    strcpy(psw, token);
    // card number
    token = strtok(NULL, "\n");
    char *card_number = malloc(strlen(token) + 1);
    strcpy(card_number, token);
    // card date
    token = strtok(NULL, "\n");
    char *card_date = malloc(strlen(token) + 1);
    strcpy(card_date, token);
    // card cvc
    token = strtok(NULL, "\n");
    // save to db
    char *card_id = insert_payment_method(user_id, psw, card_number, card_date, token);
    printf("card_id: %s\n", card_id);
    if (card_id != NULL)
    {
        send_responseSSL(card_id, ssl);
    }
    else
        send_responseSSL("NULL", ssl);
}

// handler for the remove payment method request
void handle_remove_payment(char *token, SSL *ssl)
{
    // user name
    token = strtok(NULL, "\n");
    char *user_id = malloc(strlen(token) + 1);
    strcpy(user_id, token);
    // password
    token = strtok(NULL, "\n");
    char *psw = malloc(strlen(token) + 1);
    strcpy(psw, token);
    // card number
    token = strtok(NULL, "\n");

    // remove from db
    remove_payment_method(user_id, psw, token);
}

// handler for the get user saved payment methods request
void handle_get_payment_methods(char *token, SSL *ssl)
{
    // user name
    token = strtok(NULL, "\n");
    char *user_id = malloc(strlen(token) + 1);
    strcpy(user_id, token);
    // password
    token = strtok(NULL, "\n");
    char *psw = malloc(strlen(token) + 1);
    strcpy(psw, token);
    // handle res
    MYSQL_RES *res;
    MYSQL_ROW row;
    res = get_user_payment_methods(user_id, psw);
    //  send card count
    uint32_t count = mysql_num_rows(res);
    uint32_t count_net = htonl(count);
    SSL_write(ssl, (void *)(&count_net), sizeof(uint32_t));
    // loop on results
    while ((row = mysql_fetch_row(res)))
    {
        // send card id
        send_responseSSL(row[0], ssl);
        // send card number
        send_responseSSL(row[1], ssl);
        // send card date
        send_responseSSL(row[2], ssl);
    }
    mysql_free_result(res);
}
