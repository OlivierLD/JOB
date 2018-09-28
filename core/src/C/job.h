/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class job_io_JOBNativeInterface */

#ifndef _Included_job_io_JOBNativeInterface
#define _Included_job_io_JOBNativeInterface
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     job_io_JOBNativeInterface
 * Method:    openDevice
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_job_io_JOBNativeInterface_openDevice
  (JNIEnv *, jclass, jstring);

/*
 * Class:     job_io_JOBNativeInterface
 * Method:    getError
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_job_io_JOBNativeInterface_getError
  (JNIEnv *, jclass, jint);

/*
 * Class:     job_io_JOBNativeInterface
 * Method:    closeDevice
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_job_io_JOBNativeInterface_closeDevice
  (JNIEnv *, jclass, jint);

/*
 * Class:     job_io_JOBNativeInterface
 * Method:    readFile
 * Signature: (Ljava/lang/String;[B)I
 */
JNIEXPORT jint JNICALL Java_job_io_JOBNativeInterface_readFile
  (JNIEnv *, jclass, jstring, jbyteArray);

/*
 * Class:     job_io_JOBNativeInterface
 * Method:    writeFile
 * Signature: (Ljava/lang/String;[B)I
 */
JNIEXPORT jint JNICALL Java_job_io_JOBNativeInterface_writeFile
  (JNIEnv *, jclass, jstring, jbyteArray);

/*
 * Class:     job_io_JOBNativeInterface
 * Method:    pollDevice
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_job_io_JOBNativeInterface_pollDevice
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     job_io_JOBNativeInterface
 * Method:    transferI2c
 * Signature: (II[B[B)I
 */
JNIEXPORT jint JNICALL Java_job_io_JOBNativeInterface_transferI2c
  (JNIEnv *, jclass, jint, jint, jbyteArray, jbyteArray);

/*
 * Class:     job_io_JOBNativeInterface
 * Method:    servoStartThread
 * Signature: (III)J
 */
JNIEXPORT jlong JNICALL Java_job_io_JOBNativeInterface_servoStartThread
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     job_io_JOBNativeInterface
 * Method:    servoUpdateThread
 * Signature: (JII)I
 */
JNIEXPORT jint JNICALL Java_job_io_JOBNativeInterface_servoUpdateThread
  (JNIEnv *, jclass, jlong, jint, jint);

/*
 * Class:     job_io_JOBNativeInterface
 * Method:    servoStopThread
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_job_io_JOBNativeInterface_servoStopThread
  (JNIEnv *, jclass, jlong);

/*
 * Class:     job_io_JOBNativeInterface
 * Method:    setSpiSettings
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_job_io_JOBNativeInterface_setSpiSettings
  (JNIEnv *, jclass, jint, jint, jint, jint);

/*
 * Class:     job_io_JOBNativeInterface
 * Method:    transferSpi
 * Signature: (I[B[B)I
 */
JNIEXPORT jint JNICALL Java_job_io_JOBNativeInterface_transferSpi
  (JNIEnv *, jclass, jint, jbyteArray, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif