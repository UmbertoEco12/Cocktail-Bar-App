#include "socket_multi.h"

#define PORT 8888
#define CERTIFICATE "socket/server.crt"
#define PRIVATE_KEY "socket/server.key"
#define MAX_CLIENTS 10

typedef struct
{
    int client_fd;
    SSL *ssl;
} ClientConnection;

// Helper function to handle SSL errors
void handle_ssl_error(const char *message)
{
    fprintf(stderr, "%s\n", message);
    ERR_print_errors_fp(stderr);
    exit(EXIT_FAILURE);
}

// Function to configure the SSL context
void configure_context(SSL_CTX *ctx)
{
    if (SSL_CTX_use_certificate_file(ctx, CERTIFICATE, SSL_FILETYPE_PEM) <= 0)
        handle_ssl_error("Error loading certificate file");

    if (SSL_CTX_use_PrivateKey_file(ctx, PRIVATE_KEY, SSL_FILETYPE_PEM) <= 0)
        handle_ssl_error("Error loading private key file");
}

int receive_message_length(SSL *ssl)
{
    uint32_t message_length;
    int bytes_read = SSL_read(ssl, &message_length, sizeof(message_length));
    if (bytes_read != sizeof(message_length))
    {
        if (bytes_read == 0)
        {
            return 0;
        }
        perror("SSL_read() failed length");
        return -1;
    }

    return ntohl(message_length);
}

int receive_message(SSL *ssl, char **buffer)
{
    int message_length = receive_message_length(ssl);
    if (message_length == 0)
        return 0;
    if (message_length < 0)
    {
        return -1;
    }

    *buffer = malloc(message_length + 1); // +1 for null terminator
    if (!(*buffer))
    {
        perror("Failed to allocate memory");
        return -1;
    }
    int num_bytes = SSL_read(ssl, *buffer, message_length);
    if (num_bytes < 0)
    {
        perror("SSL_read() failed buffer");
        free(*buffer);
        return -1;
    }

    (*buffer)[message_length] = '\0'; // Null-terminate the string

    return num_bytes;
}

int receive_request(SSL *ssl)
{
    // UInt32 length = 0;
    char *buffer;
    int num_bytes = receive_message(ssl, &buffer);
    if (num_bytes > 0)
    {
        // Message received
        // process
        handle_request(buffer, 0, ssl);
        free(buffer);
    }
    return num_bytes;
}

// Thread function to handle each client connection
void *client_thread(void *arg)
{
    ClientConnection *connection = (ClientConnection *)arg;
    int r = 0;
    do
    {
        // Receive the request from the client
        r = receive_request(connection->ssl);
    } while (r > 0);

    // Cleanup the SSL connection
    SSL_shutdown(connection->ssl);
    SSL_free(connection->ssl);
    close(connection->client_fd);

    free(connection);
    pthread_exit(NULL);
}

int socket_main(int argc, char **argv)
{
    SSL_CTX *ctx;
    int server_fd;

    // Initialize OpenSSL
    SSL_library_init();
    SSL_load_error_strings();
    OpenSSL_add_all_algorithms();

    // Create the SSL context
    ctx = SSL_CTX_new(TLS_server_method());
    if (!ctx)
        handle_ssl_error("SSL_CTX_new() failed");

    // Configure the SSL context
    configure_context(ctx);

    // Create a TCP socket
    server_fd = socket(AF_INET, SOCK_STREAM, 0);
    if (server_fd < 0)
    {
        perror("socket() failed");
        SSL_CTX_free(ctx);
        exit(EXIT_FAILURE);
    }
    int reuse = 1;
    // Set the socket option to reuse the address
    if (setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse)) == -1)
    {
        perror("setsockopt() failed");
        close(server_fd);
        exit(1);
    }
    // Set up the server address
    struct sockaddr_in server_addr;
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    server_addr.sin_addr.s_addr = htonl(INADDR_ANY);

    // Bind the socket to the server address
    if (bind(server_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0)
    {
        perror("bind() failed");
        close(server_fd);
        SSL_CTX_free(ctx);
        exit(EXIT_FAILURE);
    }

    // Start listening for client connections
    if (listen(server_fd, SOMAXCONN) < 0)
    {
        perror("listen() failed");
        close(server_fd);
        SSL_CTX_free(ctx);
        exit(EXIT_FAILURE);
    }

    printf("Server listening on port %d...\n", PORT);

    while (1)
    {
        // Accept a client connection
        int client_fd = accept(server_fd, NULL, NULL);
        if (client_fd < 0)
        {
            perror("accept() failed");
            close(server_fd);
            SSL_CTX_free(ctx);
            exit(EXIT_FAILURE);
        }

        // Create a new SSL object for the client
        SSL *ssl = SSL_new(ctx);
        if (!ssl)
            handle_ssl_error("SSL_new() failed");

        // Associate the SSL object with the client socket
        if (SSL_set_fd(ssl, client_fd) != 1)
            handle_ssl_error("SSL_set_fd() failed");

        // Perform the SSL handshake
        int ssl_accept_result = SSL_accept(ssl);
        if (ssl_accept_result <= 0)
            handle_ssl_error("SSL_accept() failed");

        printf("SSL handshake successful. New client connected.\n");

        // Create a client connection object
        ClientConnection *connection = (ClientConnection *)malloc(sizeof(ClientConnection));
        connection->client_fd = client_fd;
        connection->ssl = ssl;

        // Create a new thread to handle the client connection
        pthread_t thread;
        if (pthread_create(&thread, NULL, client_thread, (void *)connection) != 0)
        {
            perror("pthread_create() failed");
            close(client_fd);
            SSL_shutdown(ssl);
            SSL_free(ssl);
            free(connection);
        }

        // Detach the thread
        pthread_detach(thread);
    }

    // Cleanup and exit
    close(server_fd);
    SSL_CTX_free(ctx);

    return 0;
}