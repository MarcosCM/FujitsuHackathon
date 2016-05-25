#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
#include <unistd.h>

#include "Cuadro.h"

using namespace std;
using namespace cv;

RNG rng(12345);
int morph_size = 1;

/*
 * Actualiza la media y varianza de un descriptor concreto
 */
void updateDescriptor(Cuadro *f, double descriptor, double oldMean, double oldVar,
		double* newMean, double* newVar) {
	*newMean = (((oldMean*f->n)+descriptor))/(f->n+1);
	double alfa = 0.000001;
	double prioriVar = pow(*newMean*alfa,2);
	if(f->n==0){
		*newVar = prioriVar;
	} else {
		double normalVar = (((f->n-1)*oldVar)+((descriptor-oldMean)*(descriptor-(*newMean))))/(f->n);
		*newVar = (prioriVar/(f->n+1))+(((f->n)/(f->n+1))*normalVar);
		//*newVar = normalVar;
	}
}

/*
 * Actualiza los descriptores de la imagen dependiendo del histograma
 */
void updateFeature(Cuadro* f, MatND hist) {
	double newMean, newVar;
	std::vector<uchar> array;
	if (hist.isContinuous()) {
	  array.assign(hist.datastart, hist.dataend);
	} else {
	  for (int i = 0; i < hist.rows; ++i) {
	    array.insert(array.end(), hist.ptr<uchar>(i), hist.ptr<uchar>(i)+hist.cols);
	  }
	}
	for(int i = 0; i<256; i++){
		updateDescriptor(f, array[i], f->hist_medio[i], f->hist_var[i], &newMean, &newVar);
		f->hist_medio[i] = newMean;
		f->hist_var[i] = newVar;
	}
	f->n += 1;
}

void aprender(String nomFich) {
	Mat img, adaptive, otsu, canny, blobs;
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;

	vector<Cuadro> cuadros;

	/* Carga de la imagen */
	img = imread(nomFich, CV_LOAD_IMAGE_GRAYSCALE);

	Size smallSize(20, 20);
	vector<Mat> smallImages;

	int acc = 0;
	for (int y = 0; y < img.rows; y += smallSize.height) {
			for (int x = 0; x < img.cols; x += smallSize.width) {
				if (((y + smallSize.height) < img.rows)
						&& ((x + smallSize.width) < img.cols)) {
					Rect rect = Rect(x, y, smallSize.width, smallSize.height);
					smallImages.push_back(Mat(img, rect));
					ostringstream oss;
					oss << "Img " << x << " " << y << endl;
					imshow(oss.str(), Mat(img, rect));

					// Quantize the hue to 30 levels
					// and the saturation to 32 levels
					int hbins = 30, sbins = 32;
					int histSize = 255;
					// saturation varies from 0 (black-gray-white) to
					// 255 (pure spectrum color)
					float sranges[] = { 0, 256 };
					const float* ranges[] = { sranges };
					MatND hist;
					// we compute the histogram from the 0-th and 1-st channels
					int channels[] = { 0 };

					Mat re = Mat(img, rect);

					calcHist(&re, 1, channels, Mat(), // do not use mask
							hist, 1, &histSize, ranges, true, // the histogram is uniform
							false);

					/* Lectura de los objetos almacenados en el fichero */
					FileStorage fs("objetos.yml", FileStorage::READ);
					FileNode n = fs["strings"];

					ostringstream osc;
					osc << "C" << x << y;
					cout << osc.str() << endl;
					Cuadro current = Cuadro(osc.str());
					current.read(n);

					updateFeature(&current, hist);

					cuadros.push_back(current);
					fs.release();


					acc++;
				}
			}
		}

	/* Escribe los datos actualizados en el fichero */
	FileStorage fs2("objetos.yml", FileStorage::WRITE);
	for(int i = 0; i<acc; i++){
		cuadros[i].write(fs2);
	}

	fs2.release();

	//while (1) {
		//imshow("Original", img);
		//imshow("Adaptive", adaptive);
		//imshow("Contours", otsu);
		//imshow("Blobs", blobs);

		//if (waitKey(1) == 27)
		//  break;
	//}
}
