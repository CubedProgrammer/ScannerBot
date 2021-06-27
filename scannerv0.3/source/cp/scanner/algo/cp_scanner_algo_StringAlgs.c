#include"cp_scanner_algo_StringAlgs.h"

int pythagorean_arithmancy(const char *name);

JNIEXPORT jint JNICALL Java_cp_scanner_algo_StringAlgs_arithmancy
  (JNIEnv *env, jclass cls, jstring name)
{
    const jchar *namearr = (*env)->GetStringChars(env, name, NULL);
    char namestr[2001];
    for(unsigned i = 0; i < (*env)->GetStringLength(env, name); i++)
        namestr[i] = namearr[i];
    (*env)->ReleaseStringChars(env, name, namearr);
    return pythagorean_arithmancy(namestr);
}
