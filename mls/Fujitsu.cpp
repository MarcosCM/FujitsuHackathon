#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
#include <unistd.h>

using namespace std;
using namespace cv;

extern void aprender(String nomFich);
extern void reconocer(String nomFich);

int main(int argc, char *argv[]) {


	aprender("imagenes\\tst.jpg");

	while (1) {
		if (waitKey(1) == 27)
			break;
	}
}

