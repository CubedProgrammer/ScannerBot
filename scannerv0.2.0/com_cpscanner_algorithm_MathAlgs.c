#include "triangle_solver.h"
#include "com_cpscanner_algorithm_MathAlgs.h"
JNIEXPORT void JNICALL Java_com_cpscanner_algorithm_MathAlgs_solveTriangle
  (JNIEnv *env, jclass jc, jdoubleArray jarr)
  {
    double *arr = (*env)->GetDoubleArrayElements(env, jarr, NULL);
    solve(arr, arr + 1, arr + 2, arr + 3, arr + 4, arr + 5);
    (*env)->ReleaseDoubleArrayElements(env, jarr, arr, 0);
  }
