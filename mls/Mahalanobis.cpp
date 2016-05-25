#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
#include <unistd.h>

#include "Cuadro.h"

using namespace std;
using namespace cv;

double mahalanobis(MatND hist, Cuadro f) {
	/* Calcula los descriptores de imagen */
	std::vector<uchar> array;
	if (hist.isContinuous()) {
		array.assign(hist.datastart, hist.dataend);
	} else {
		for (int i = 0; i < hist.rows; ++i) {
			array.insert(array.end(), hist.ptr<uchar>(i),
					hist.ptr<uchar>(i) + hist.cols);
		}
	}
	double d = 0.0;
	for (int i = 0; i < 256; i++) {
		d += pow((array.at(i) - f.hist_medio[i]), 2) / ((double) f.hist_var[i]);
	}
	return d;
}
