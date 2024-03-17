#include "sc_array.h"
#include "database/database.h"
#include "socket/socket_multi.h"

int main(int argc, char *argv[])
{
    // init database
    database_init(argc, argv);
    // handle incoming connections
    socket_main(argc, argv);
    // close db and exit
    database_close();
    return EXIT_SUCCESS;
}
