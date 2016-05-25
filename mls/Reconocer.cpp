#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
#include <unistd.h>

#include "Cuadro.h"

using namespace std;
using namespace cv;

extern double mahalanobis(MatND x, Cuadro f);

int chi_4 = 9.488;

void reconocer(String nomFich) {
	Mat img, otsu, morph, blobs;
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;

	/* Carga de la imagen de reconocimiento */
	img = imread(nomFich, CV_LOAD_IMAGE_GRAYSCALE);

	vector<Cuadro> cuadros;
	vector<MatND> histogramas;

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

				int histSize = 255;

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

				Cuadro current = Cuadro("C" + x + y);
				current.read(n);
				cuadros.push_back(current);
				fs.release();

				histogramas.push_back(hist);

				acc++;
			}
		}
	}

	cout << contours.size() << " contornos detectados" << endl;

	/* Compara la figura con cada clase (circulo, rueda, etc) */
	for (int j = 0; j < acc; j++) {
		Cuadro f = cuadros[j];

		/* Si pasa el test de mahalanobis, pertenece a esa clase */
		double m = mahalanobis(histogramas.at(j), f);
		cout << m << endl;
	}

	while (1) {
		imshow("Original", img);
		imshow("Otsu", otsu);
		imshow("Contours", morph);
		imshow("Blobs", blobs);

		if (waitKey(1) == 27)
			break;
	}

}
