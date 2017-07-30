/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class jhard_io_JHardNativeInterface */

#ifndef _Included_jhard_io_JHardNativeInterface
#define _Included_jhard_io_JHardNativeInterface
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    openDevice
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_jhard_io_JHardNativeInterface_openDevice
  (JNIEnv *, jclass, jstring);

/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    getError
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_jhard_io_JHardNativeInterface_getError
  (JNIEnv *, jclass, jint);

/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    closeDevice
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_jhard_io_JHardNativeInterface_closeDevice
  (JNIEnv *, jclass, jint);

/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    readFile
 * Signature: (Ljava/lang/String;[B)I
 */
JNIEXPORT jint JNICALL Java_jhard_io_JHardNativeInterface_readFile
  (JNIEnv *, jclass, jstring, jbyteArray);

/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    writeFile
 * Signature: (Ljava/lang/String;[B)I
 */
JNIEXPORT jint JNICALL Java_jhard_io_JHardNativeInterface_writeFile
  (JNIEnv *, jclass, jstring, jbyteArray);

/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    pollDevice
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_jhard_io_JHardNativeInterface_pollDevice
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    transferI2c
 * Signature: (II[B[B)I
 */
JNIEXPORT jint JNICALL Java_jhard_io_JHardNativeInterface_transferI2c
  (JNIEnv *, jclass, jint, jint, jbyteArray, jbyteArray);

/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    servoStartThread
 * Signature: (III)J
 */
JNIEXPORT jlong JNICALL Java_jhard_io_JHardNativeInterface_servoStartThread
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    servoUpdateThread
 * Signature: (JII)I
 */
JNIEXPORT jint JNICALL Java_jhard_io_JHardNativeInterface_servoUpdateThread
  (JNIEnv *, jclass, jlong, jint, jint);

/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    servoStopThread
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_jhard_io_JHardNativeInterface_servoStopThread
  (JNIEnv *, jclass, jlong);

/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    setSpiSettings
 * Signature: (IIII)I
 */
JNIEXPORT jint JNICALL Java_jhard_io_JHardNativeInterface_setSpiSettings
  (JNIEnv *, jclass, jint, jint, jint, jint);

/*
 * Class:     jhard_io_JHardNativeInterface
 * Method:    transferSpi
 * Signature: (I[B[B)I
 */
JNIEXPORT jint JNICALL Java_jhard_io_JHardNativeInterface_transferSpi
  (JNIEnv *, jclass, jint, jbyteArray, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
