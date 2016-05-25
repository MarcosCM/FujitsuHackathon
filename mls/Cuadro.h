#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
#include <unistd.h>

#ifndef CUADRO_H_
#define CUADRO_H_

using namespace std;
using namespace cv;

class Cuadro {
public:
    int n;
    String nombre;
    float hist_medio[255];
    float hist_var[255];

    Cuadro(String nom);

    ~Cuadro();

    void write(FileStorage& fs);

    void read(const FileNode& node);

};

#endif /* CUADRO_H_ */
