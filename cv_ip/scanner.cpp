#include <iostream>
#include <algorithm>
#include <vector>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
using namespace cv;
using namespace std;

Mat resize_(Mat inputImage)
{
	Mat outputImage;
	int w = inputImage.size().width;
	int h = inputImage.size().height;
	Size rsiz = (w > h) ? Size(1000,500) : Size(500,1000);
	resize(inputImage, outputImage, rsiz);
	return outputImage;
}

int getDistance(Point a, Point b)
{
	int c = sqrt( pow(b.x - a.x , 2) + pow(b.y - a.y, 2));
	cout << c << endl;
	return c;
}

vector<Point2f> pushPoints(Point p1, Point p2, Point p3, Point p4)
{
	vector<Point2f> dst;
	dst.push_back(p1);
	dst.push_back(p2);
	dst.push_back(p3);
	dst.push_back(p4);
	return dst;
}

vector<Point2f> orderPoints(vector<Point2f> sort)
{ 
	Point2f tl, tr, bl, br;
	int diff[4], sum[4], max_min[4];
	for(int i = 0; i < sort.size(); i++)
	{
		sum[i] = sort[i].x + sort[i].y;
	}
	max_min[0] = min(sum[0],min(sum[1],min(sum[2],sum[3])));
	max_min[1] = max(sum[0],max(sum[1],max(sum[2],sum[3])));
	for(int i = 0; i < sort.size(); i++)
	{
		diff[i] = abs(sort[i].x - sort[i].y);
	}
	max_min[2] = min(diff[0],min(diff[1],min(diff[2],diff[3])));
	max_min[3] = max(diff[0],max(diff[1],max(diff[2],diff[3])));
	for(int i = 0; i < sort.size(); i++)
	{
		if(sum[i]==max_min[0]){ tl = sort[i];}
		if(sum[i]==max_min[1]){ br = sort[i];}
		//if(diff[i]==max_min[2]){ tr = sort[i];}
		if(diff[i]==max_min[3]){ bl = sort[i];}
	}	
	for(int i = 0; i < sort.size(); i++)
	{
		if(sort[i]!=tl && sort[i]!=bl && sort[i]!=br)
		{
			tr = sort[i];
		}
	}
	sort = pushPoints(tl, tr, bl, br);
	//cout << sort << endl;
	return sort;
}

vector<Point2f> getPoints(Mat image)
{
	int width = image.size().width;
	int height = image.size().height;
	int intensity, img_intensity, larea = 0, lindex = 0;
	Mat bgdModel, fgdModel, mask;
	vector<vector<Point> > contours;
	vector<Point2f> approxCurve, rectPts;
	double a, epsilon;
	Rect rect, bounding_rect;
	Size rsiz = (width > height) ? Size(1000,500) : Size(500,1000);
	resize(image, image, rsiz);
	mask = Mat::zeros(rsiz, CV_8UC1);
	bgdModel = Mat::zeros(1, 65, CV_64F);
	fgdModel = Mat::zeros(1, 65, CV_64F);
	rect = Rect(50, 50, width-100, height-100);
	grabCut(image, mask, rect, bgdModel, fgdModel, 5, GC_INIT_WITH_RECT);
	for(int i = 0; i < image.rows; i++)
	{
		for(int j = 0; j < image.cols; j++)
		{
			intensity = mask.at<uchar>(i, j);
			if( (intensity == 0) | (intensity == 2) ) { mask.at<uchar>(i, j) = 0 ; } else { mask.at<uchar>(i, j) = 255 ; }
		}
	}
	//imshow("Mask", mask);
	//waitKey(0);
	findContours(mask, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
	for(int i = 0; i < contours.size(); i++)
	{
		a = contourArea(contours[i]);
		if(a > larea)
		{
			larea = a;
			lindex = i;
			bounding_rect = boundingRect(contours[i]);
		}
	}
	//drawContours(image, contours, lindex, Scalar(0,0,255), 2, 8);
	//imshow("Image",image);
	//waitKey(0);
	epsilon = 0.1*arcLength(Mat(contours[lindex]), true);
	approxPolyDP(Mat(contours[lindex]), approxCurve, epsilon,true);
	//cout << approxCurve << endl;
	approxCurve = orderPoints(approxCurve);
	return approxCurve;
}

Mat doPerspective(Mat inputImage)
{
	inputImage = resize_(inputImage);
	vector<Point2f> points = getPoints(inputImage);
	cout << points << endl;
	int w1 = getDistance(points[0], points[1]);
	Point p1, p2;
	int w = inputImage.size().width;
	int h = inputImage.size().height;
	if(w>h){p1 = Point(0,h); p2 = Point(w,0);}
	else{p1 = Point(w,0); p2 = Point(0,h);}
	vector<Point2f> dst = pushPoints(Point(0,0), p1, p2, Point(w,h));
	//cout << dst << endl;
	Mat transmtx = getPerspectiveTransform(points, dst);
	warpPerspective(inputImage, inputImage, transmtx, inputImage.size());
	//imwrite("perspective_output.jpg", inputImage);
	return inputImage;
}

Mat binarize(Mat image)
{
	cvtColor( image, image, CV_BGR2GRAY);
	adaptiveThreshold(image, image, 255.0, CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY, 11, 2);
	return image;
}

int main(int argc, char** argv)
{
	Mat img = imread(argv[1]);
	img = doPerspective(img);
	img = binarize(img);
	imshow("Binary", img);
	waitKey(0);
	return 0;
}
