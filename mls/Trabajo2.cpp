#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
#include <unistd.h>

using namespace std;
using namespace cv;

extern void aprender(String nomFich, String nomObj);
extern void reconocer(String nomFich);

int maina(int argc, char *argv[]) {
//    if (string(argv[1])=="aprender") {
//    	aprender("imagenesT2\\circulo1.pgm", "circulo");
//    	aprender("imagenesT2\\circulo2.pgm", "circulo");
//    	aprender("imagenesT2\\circulo3.pgm", "circulo");
//    	aprender("imagenesT2\\circulo4.pgm", "circulo");
//    	aprender("imagenesT2\\circulo5.pgm", "circulo");
//
//    	aprender("imagenesT2\\rueda1.pgm", "rueda");
//    	aprender("imagenesT2\\rueda2.pgm", "rueda");
//    	aprender("imagenesT2\\rueda3.pgm", "rueda");
//    	aprender("imagenesT2\\rueda4.pgm", "rueda");
//    	aprender("imagenesT2\\rueda5.pgm", "rueda");
//    	aprender("imagenesT2\\rectangulo1.pgm", "rectangulo");
//    	aprender("imagenesT2\\rectangulo2.pgm", "rectangulo");
//    	aprender("imagenesT2\\rectangulo3.pgm", "rectangulo");
//    	aprender("imagenesT2\\rectangulo4.pgm", "rectangulo");
//    	aprender("imagenesT2\\rectangulo5.pgm", "rectangulo");
//    	aprender("imagenesT2\\triangulo1.pgm", "triangulo");
//    	aprender("imagenesT2\\triangulo2.pgm", "triangulo");
//    	aprender("imagenesT2\\triangulo3.pgm", "triangulo");
//    	aprender("imagenesT2\\triangulo4.pgm", "triangulo");
//    	aprender("imagenesT2\\triangulo5.pgm", "triangulo");
//    	aprender("imagenesT2\\vagon1.pgm", "vagon");
//    	aprender("imagenesT2\\vagon2.pgm", "vagon");
//    	aprender("imagenesT2\\vagon3.pgm", "vagon");
//    	aprender("imagenesT2\\vagon4.pgm", "vagon");
//    	aprender("imagenesT2\\vagon5.pgm", "vagon");
//    } else if (argc == 3 && string(argv[1])=="reconocer"){
//    	reconocer(string(argv[2]));
//    } else {
//    	cout << "Para aprender: ./aprender nomfich nomobj\n"
//    			<<"Para reconocer: ./reconocer nomfich";
//    }

}
