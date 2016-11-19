#include "highgui.h"
#include <cv.h>
#include <cvaux.h>
 

void myErode(IplImage* src,IplImage* dst,int a){
	IplConvKernel* element = cvCreateStructuringElementEx( a,1,a/2,0,CV_SHAPE_RECT, NULL );
	cvErode(src,dst,element,1);
}