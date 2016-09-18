#include "highgui.h"
#include <cv.h>
#include <cvaux.h>
void mySmooth(IplImage* img1,IplImage* img2){//中值滤波
    cvSmooth(img1,img2,CV_MEDIAN ,3,0,0,0 );
}
