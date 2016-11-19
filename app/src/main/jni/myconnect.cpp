#include "highgui.h"
#include <cv.h>
#include <cvaux.h>
void myConnect(IplImage* img_src,IplImage* img_Clone){
    CvSeq* contour = NULL;
    double minarea = 400.0;
    double tmparea = 0.0;
    CvMemStorage* storage = cvCreateMemStorage(0);
    uchar *pp;
    IplImage* img_dst = cvCreateImage(cvGetSize(img_src),IPL_DEPTH_8U,1);
    //------------搜索二值图中的轮廓，并从轮廓树中删除面积小于某个阈值minarea的轮廓-------------//
    CvScalar color = cvScalar(255,0,0);//CV_RGB(128,0,0);
    CvContourScanner scanner = NULL;
    scanner = cvStartFindContours(img_src,storage,sizeof(CvContour),CV_RETR_CCOMP,CV_CHAIN_APPROX_NONE,cvPoint(0,0));
    //开始遍历轮廓树
    CvRect rect;
    while (contour=cvFindNextContour(scanner))
    {
        tmparea = fabs(cvContourArea(contour));
        rect = cvBoundingRect(contour,0);
        if (tmparea < minarea/*||tmparea>4900*/)
        {
            //当连通域的中心点为黑色时，而且面积较小则用白色进行填充
            pp=(uchar*)(img_Clone->imageData + img_Clone->widthStep*(rect.y+rect.height/2)+rect.x+rect.width/2);
            if (pp[0]==255)
            {
                for(int y = rect.y;y<rect.y+rect.height;y++)
                {
                    for(int x =rect.x;x<rect.x+rect.width;x++)
                    {
                        pp=(uchar*)(img_Clone->imageData + img_Clone->widthStep*y+x);

                        if (pp[0]==255)
                        {
                            pp[0]=0;
                        }
                    }
                }
            }

        }

    }
}

