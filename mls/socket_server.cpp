#include <iostream>		// cout, cerr
#include <pthread.h>	// POSIX threads
#include <stdio.h>		// fopen, fwrite, fread...

const int SV_PORT = 50001;

// sizes
const int BUF_SIZE = 200;
const int CMD_SIZE = 2;
// messages
const char ACK[4] = "ACK";
const char FE_CMD[3] = "FE";
const char AS_CMD[3] = "AS";

const char IMG_NAME[8] = "img.jpg";

void *connHandler(void *arg);               // Main program of a thread  

int main(int argc, char *argv[]) {
	try{
		TCPServerSocket serverSock(SV_PORT);
		while(1){
			TCPSocket *sock = serverSock.accept();

			pthread_t th;
			if (pthread_create(&th, NULL, connHandler, (void *) sock) != 0){
				cerr << "Error while creating pthread" << endl;
			}
			else{

			}
		}
	}
	catch(SocketException &e){
		cerr << e.what() << endl;
		exit(1);
	}

	return 0;
}

size_t curl_callback(void *ptr, size_t size, size_t nmemb, void* usrData) {
    size_t n;
    FILE* stream = (FILE*) usrData;

    if (!stream) {
        cerr << "Stream err" << endl;
        return 0;
    }

    n = fwrite((FILE*) ptr, size, nmemb, stream);
    return n;
}

int get_img_from_url(char* url) {
    FILE* fp = fopen(IMG_NAME, "wb");
    if (!fp) {
        cerr << "Error while creating file" << endl;
        return 1;
    }

    CURL* curl = curl_easy_init();
    curl_easy_setopt(curl, CURLOPT_URL, url);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, fp);
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, curl_callback);
    curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1);

    CURLcode curl_res = curl_easy_perform(curl);
    if (curl_res) {
        cerr << "Could not download" << endl;
        return 1;
    }

    long res_code = 0;
    curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &res_code);
    if (!((res_code == 200 || res_code == 201) && curl_res != CURLE_ABORTED_BY_CALLBACK)) {
        cerr << "Invalid response code" << endl;
        return 1;
    }

    // free mem
    curl_easy_cleanup(curl);
    fclose(fp);

    return 0;
}

void *connHandler(void *sock) {
	char buf[100], curr_char;
	int i;
	pthread_detach(pthread_self());

	TCPSocket *socket = (TCPSocket *) sock;
	socket.recv(buf, CMD_SIZE);
	// If it is a FEED command
	if(buf.compare(FE_CMD)){
		// Reset buf
		buf[0] = '\0';
		i = 0;
		do{
			socket.recv(&curr_char, 1);
			buf[i] = curr_char;
			i++;
		} while(curr_char != '\n');
		// replace \n for \0 to end string
		buf[i] = '\0';

		get_img_from_url(buf);
	}
	// If it is an ASK command
	else if(buf.compare(AS_CMD)){
		buf[0] = '\0';
	}

    // Echo message back to client
    strcpy(buf, ACK);
    sock->send(buf, 3);

    delete (TCPSocket *) sock;
  	return NULL;
}