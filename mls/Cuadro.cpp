#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
#include <unistd.h>

#include "Cuadro.h"

using namespace std;
using namespace cv;

Cuadro::Cuadro(String nom) {
	nombre = nom;
	n = 0;
}

Cuadro::~Cuadro() {
	/*
	delete[] &n;
	delete[] &area_media;
	delete[] &area_sse;
	delete[] &area_varianza;
	delete[] &perimetro_media;
	delete[] &perimetro_sse;
	delete[] &perimetro_varianza;
	delete[] &m1_media;
	delete[] &m1_sse;
	delete[] &m1_varianza;
	delete[] &m2_media;
	delete[] &m2_sse;
	delete[] &m2_varianza;
	*/
}

void Cuadro::write(FileStorage& fs) {
	cout << nombre << endl;
	fs << nombre + "_n" << n;
	for(int i = 0; i < 256; i++){
		ostringstream oss;
		string String = static_cast<ostringstream*>( &(ostringstream() << i) )->str();
		fs << nombre + "_hist_medio" + String  << hist_medio[i];
		String = static_cast<ostringstream*>( &(ostringstream() << i) )->str();
		fs << nombre + "_hist_var" + String << hist_var[i];
	}
}

void Cuadro::read(const FileNode& node) {
	n = (int) node[nombre + "_n"];
	for(int i = 0; i < 256; i++){
		ostringstream oss;
		oss << nombre + "_hist_medio" << i;
		hist_medio[i] = (float) node[oss.str()];
		oss << nombre + "_hist_var" << i;
		hist_var[i] = (float) node[oss.str()];
	}
}
