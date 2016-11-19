#include "com_martin_opencv4android_OpenCVHelper.h"
#include <stdio.h>
#include <stdlib.h>
#include <algorithm>
#include <vector>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <cv.h>
#include <highgui.h>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
#include <android/log.h>
#include <opencv2/opencv.hpp>
#include <android/bitmap.h>
#define APPNAME "OCV4Android"

using namespace cv;
using namespace std;


Mat get_modified_gray(Mat img)
{
Mat gray;
cv::cvtColor(img,gray,CV_BGR2GRAY);
cv::cvtColor(gray,gray,CV_GRAY2BGR);
return gray;
}

Mat get_modified_hist(Mat img)
{
vector<Mat> channels; 
Mat img_hist_equalized;
cvtColor(img, img_hist_equalized, CV_BGR2YCrCb); 
split(img_hist_equalized,channels); 
equalizeHist(channels[0], channels[0]); 
merge(channels,img_hist_equalized); 
cvtColor(img_hist_equalized, img_hist_equalized, CV_YCrCb2BGR); 
return img_hist_equalized;
}

Mat get_modified_bw(Mat img)
{
Mat bw_output,gray;
cv::cvtColor(img,gray,CV_BGR2GRAY);
adaptiveThreshold(gray,bw_output,255,ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY,15,5);
cv::cvtColor(bw_output,bw_output,CV_GRAY2BGR);
return bw_output;
}

Mat get_modified_enhance(Mat img)
{
  Mat lab_image;
  cvtColor(img,lab_image,CV_BGR2Lab);
  std::vector<cv::Mat> lab_planes(3);
  cv::split(lab_image, lab_planes);  
  cv::Ptr<cv::CLAHE> clahe = cv::createCLAHE();
  clahe->setClipLimit(2);
  cv::Mat dst;
  clahe->apply(lab_planes[0], dst);
  dst.copyTo(lab_planes[0]);
  cv::merge(lab_planes, lab_image);
  cv::Mat image_clahe;
  cv::cvtColor(lab_image, image_clahe, CV_Lab2BGR);
return image_clahe;
}

Mat get_modified_lighten(Mat img)
{
cv::Mat brightened_image = img*1.3;
cv::addWeighted(brightened_image,0.9,img,0.1,3,brightened_image);
return brightened_image;
}

int main( int argc, char** argv )
{
    Mat image;
    image = imread(argv[1], CV_LOAD_IMAGE_COLOR);   // Read the file
    Mat out=get_modified_bw(image);
    namedWindow( "Display window", WINDOW_AUTOSIZE );// Create a window for display.
    imshow( "Display window",out);                   // Show our image inside it.
    waitKey(0);                                          // Wait for a keystroke in the window
    return 0;
}


jobject mat_to_bitmapp(JNIEnv * env, Mat & src, bool needPremultiplyAlpha, jobject bitmap_config){
    jclass java_bitmap_class = (jclass)env->FindClass("android/graphics/Bitmap");
    jmethodID mid = env->GetStaticMethodID(java_bitmap_class,
                                           "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");

    jobject bitmap = env->CallStaticObjectMethod(java_bitmap_class,
                                                 mid, src.size().width, src.size().height, bitmap_config);
    AndroidBitmapInfo  info;
    void*              pixels = 0;

    try {
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
        CV_Assert(src.type() == CV_8UC1 || src.type() == CV_8UC3 || src.type() == CV_8UC4);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
        CV_Assert(pixels);
        if(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888){
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if(src.type() == CV_8UC1){
                cvtColor(src, tmp, CV_GRAY2RGBA);
            }else if(src.type() == CV_8UC3){
                cvtColor(src, tmp, CV_BGR2RGBA);
            }else if(src.type() == CV_8UC4){
                if(needPremultiplyAlpha){
                    cvtColor(src, tmp, CV_RGBA2mRGBA);
                }else{
                    src.copyTo(tmp);
                }
            }
        }else{
            // info.format == ANDROID_BITMAP_FORMAT_RGB_565
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if(src.type() == CV_8UC1){
                cvtColor(src, tmp, CV_GRAY2BGR565);
            }else if(src.type() == CV_8UC3){
                cvtColor(src, tmp, CV_BGR2BGR565);
            }else if(src.type() == CV_8UC4){
                cvtColor(src, tmp, CV_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return bitmap;
    }catch(cv::Exception e){
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return bitmap;
    }catch (...){
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return bitmap;
    }
}

extern "C" {

//gray color
JNIEXPORT jobject JNICALL Java_com_martin_opencv4android_OpenCVHelper_getGrayBitmapp
(JNIEnv *env, jobject thiz,jobject bitmap)
{
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Scaning getGrayBitmapp");
    int ret;
    AndroidBitmapInfo info;
    void* pixels = 0;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_getInfo() failed ! error=%d", ret);
        return NULL;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 )
    {       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"Bitmap format is not RGBA_8888!");
        return NULL;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    Mat mbgra(info.height, info.width, CV_8UC4, pixels);
    // init our output image
    Mat dst = mbgra.clone();

    cvtColor(mbgra, dst, CV_RGBA2GRAY);
    cvtColor(dst, dst, CV_GRAY2BGR);

    //get source bitmap's config
    jclass java_bitmap_class = (jclass)env->FindClass("android/graphics/Bitmap");
    jmethodID mid = env->GetMethodID(java_bitmap_class, "getConfig", "()Landroid/graphics/Bitmap$Config;");
    jobject bitmap_config = env->CallObjectMethod(bitmap, mid);
    jobject _bitmap = mat_to_bitmapp(env,dst,false,bitmap_config);

    AndroidBitmap_unlockPixels(env, bitmap);
    return _bitmap;
}


//magic color
JNIEXPORT jobject JNICALL Java_com_martin_opencv4android_OpenCVHelper_getMagicBitmapp
(JNIEnv *env, jobject thiz,jobject bitmap)
{
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Scaning getMagicBitmap");
    int ret;
    AndroidBitmapInfo info;
    void* pixels = 0;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_getInfo() failed ! error=%d", ret);
        return NULL;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 )
    {       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"Bitmap format is not RGBA_8888!");
        return NULL;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }



    Mat mbgra(info.height, info.width, CV_8UC4, pixels);
        // init our output image
        Mat dst1 = mbgra.clone();
        cvtColor(mbgra ,dst1,CV_RGBA2BGR);
        Mat lab_image;
        cvtColor(dst1 ,lab_image,CV_BGR2Lab);

        std::vector<cv::Mat> lab_planes(3);
          cv::split(lab_image, lab_planes);
          cv::Ptr<cv::CLAHE> clahe = cv::createCLAHE();
          clahe->setClipLimit(2);
          Mat dst;
          clahe->apply(lab_planes[0], dst);
          dst.copyTo(lab_planes[0]);
          cv::merge(lab_planes, lab_image);
          cv::Mat image_clahe;
          cv::cvtColor(lab_image, image_clahe, CV_Lab2BGR);

    //get source bitmap's config
    jclass java_bitmap_class = (jclass)env->FindClass("android/graphics/Bitmap");
    jmethodID mid = env->GetMethodID(java_bitmap_class, "getConfig", "()Landroid/graphics/Bitmap$Config;");
    jobject bitmap_config = env->CallObjectMethod(bitmap, mid);
    jobject _bitmap = mat_to_bitmapp(env,image_clahe,false,bitmap_config);

    AndroidBitmap_unlockPixels(env, bitmap);
    return _bitmap;
}

//lighten color
JNIEXPORT jobject JNICALL Java_com_martin_opencv4android_OpenCVHelper_getLightedBitmapp
(JNIEnv *env, jobject thiz,jobject bitmap)
{
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Scaning getMagicBitmap");
    int ret;
    AndroidBitmapInfo info;
    void* pixels = 0;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_getInfo() failed ! error=%d", ret);
        return NULL;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 )
    {       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"Bitmap format is not RGBA_8888!");
        return NULL;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

     Mat mbgra(info.height, info.width, CV_8UC4, pixels);
        // init our output image
        Mat dst = mbgra.clone();

        cvtColor(mbgra,dst,CV_RGBA2BGR);
        cv::Mat brightened_image = dst*1.3;
        cv::addWeighted(brightened_image,0.9,dst,0.1,3,dst);

    //cvtColor(mbgra * 1.3,dst,CV_BGR2Lab);//

    //get source bitmap's config
    jclass java_bitmap_class = (jclass)env->FindClass("android/graphics/Bitmap");
    jmethodID mid = env->GetMethodID(java_bitmap_class, "getConfig", "()Landroid/graphics/Bitmap$Config;");
    jobject bitmap_config = env->CallObjectMethod(bitmap, mid);
    jobject _bitmap = mat_to_bitmapp(env,dst,false,bitmap_config);

    AndroidBitmap_unlockPixels(env, bitmap);
    return _bitmap;
}

//black and white image
JNIEXPORT jobject JNICALL Java_com_martin_opencv4android_OpenCVHelper_getBlackWhiteBitmapp
(JNIEnv *env, jobject thiz,jobject bitmap)
{
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Scaning getBWBitmap -->black and white image");
    int ret;
    AndroidBitmapInfo info;
    void* pixels = 0;

    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_getInfo() failed ! error=%d", ret);
        return NULL;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888 )
    {       __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"Bitmap format is not RGBA_8888!");
        return NULL;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME,"AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    Mat mbgra(info.height, info.width, CV_8UC4, pixels);
    // init our output image
    Mat dst = mbgra.clone();

    cvtColor(mbgra, dst, CV_RGBA2GRAY);

    // float alpha = 2.2;
    // float beta = 0;
    // dst.convertTo(dst, -1, alpha, beta);

    //threshold(dst,dst,255,ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY,15,5);
   threshold(dst,dst,0,255,THRESH_BINARY | THRESH_OTSU);
   cvtColor(dst, dst, CV_GRAY2BGR);


    //get source bitmap's config
    jclass java_bitmap_class = (jclass)env->FindClass("android/graphics/Bitmap");
    jmethodID mid = env->GetMethodID(java_bitmap_class, "getConfig", "()Landroid/graphics/Bitmap$Config;");
    jobject bitmap_config = env->CallObjectMethod(bitmap, mid);
    jobject _bitmap = mat_to_bitmapp(env,dst,false,bitmap_config);

    AndroidBitmap_unlockPixels(env, bitmap);
    return _bitmap;

}
}