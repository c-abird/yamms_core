#include <jni.h>
#include <fftw3.h>

#include <iostream>
using namespace std;

#include "settings.h"
#include "uni_hamburg_yamms_math_fft_FFT_fftw3Impl.h"

static int get_iodim_array(JNIEnv *env, jobjectArray j_dims, fftw_iodim *dims);
static int get_iodim(JNIEnv *env, jobject j_dim, fftw_iodim *dim);

static void enter_critical(JNIEnv *env);
static void leave_critical(JNIEnv *env);

/*
 * Class:     uni_hamburg_yamms_math_fft_FFT_fftw3Impl
 * Method:    fftw_plan_guru_dft
 * Signature: ([Luni/hamburg/yamms/math/fft/Dimension;[Luni/hamburg/yamms/math/fft/Dimension;[D[DII)J
 */
JNIEXPORT jlong JNICALL Java_uni_hamburg_yamms_math_fft_FFT_1fftw3Impl_fftw_1plan_1guru_1dft
  (JNIEnv *env, jobject obj, jobjectArray j_transform_dims, jobjectArray j_loop_dims, jdoubleArray j_in, jdoubleArray j_out, jint j_mode, jint j_flags)
{
	// Set up arguments
	int mode = j_mode;
	int flags = j_flags;

	fftw_iodim transform_dims[MAX_RANK], loop_dims[MAX_RANK];
	int transform_rank = get_iodim_array(env, j_transform_dims, transform_dims);
	int loop_rank      = get_iodim_array(env, j_loop_dims,           loop_dims);

	jdouble *in, *out;
	jboolean in_copy, out_copy;

	// lock arrays
#ifdef USE_GET_PRIMITIVE_ARRAY_CRITIAL
	in  = (jdouble*)env->GetPrimitiveArrayCritical(j_in,   &in_copy);
	out = (jdouble*)env->GetPrimitiveArrayCritical(j_out, &out_copy);
#else
	in  = env->GetDoubleArrayElements(j_in,   &in_copy);
	out = env->GetDoubleArrayElements(j_out, &out_copy);
#endif

	//if ( in_copy) cout << "IN COPY!" << endl;
	//if (out_copy) cout << "OUT COPY!" << endl;

	/*cout << "transform_rank=" << transform_rank << endl;
	cout << "loop_rank=" << loop_rank << endl;
	cout << "mode=" << mode << endl;
	cout << "flags=" << flags << endl;
	cout << "in=" << in << endl;
	cout << "out=" << out << endl;
	cout << "transform_dims: " << endl;
	for (int i=0; i<transform_rank; ++i) {
		cout << i << ": n=" << transform_dims[i].n << " is=" << transform_dims[i].is << " os=" << transform_dims[i].os << endl;
	}
	cout << "loop_dims: " << endl;
	for (int i=0; i<loop_rank; ++i) {
		cout << i << ": n=" << loop_dims[i].n << " is=" << loop_dims[i].is << " os=" << loop_dims[i].os << endl;
	}*/


	fftw_plan plan = 0;
	switch (mode) {
		case uni_hamburg_yamms_math_fft_FFT_fftw3Impl_MODE_C2C_FORW:
			plan = fftw_plan_guru_dft(
					transform_rank, transform_dims, loop_rank, loop_dims,
					(fftw_complex*)in, (fftw_complex*)out, FFTW_FORWARD, flags);
			break;
		case uni_hamburg_yamms_math_fft_FFT_fftw3Impl_MODE_C2C_BACKW:
			plan = fftw_plan_guru_dft(
					transform_rank, transform_dims, loop_rank, loop_dims,
					(fftw_complex*)in, (fftw_complex*)out, FFTW_BACKWARD, flags);
			break;
		case uni_hamburg_yamms_math_fft_FFT_fftw3Impl_MODE_R2C:
			break;
		case uni_hamburg_yamms_math_fft_FFT_fftw3Impl_MODE_C2R:
			break;
	}

	//fftw_print_plan(plan);

	// Unlock arrays
#ifdef USE_GET_PRIMITIVE_ARRAY_CRITIAL
	env->ReleasePrimitiveArrayCritical(j_in, in, 0);
	env->ReleasePrimitiveArrayCritical(j_out, out, 0);
#else
	env->ReleaseDoubleArrayElements(j_in, in, 0);
	env->ReleaseDoubleArrayElements(j_out, out, 0);
#endif

	if (plan != 0) {
		return (long)plan;
	} else {
		return 0;
	}
	return 0;
}

/*
 * Class:     uni_hamburg_yamms_math_fft_FFT_fftw3Impl
 * Method:    fftw_execute_dft
 * Signature: (J[D[DI)V
 */
JNIEXPORT void JNICALL Java_uni_hamburg_yamms_math_fft_FFT_1fftw3Impl_fftw_1execute_1dft
  (JNIEnv *env, jobject obj, jlong j_plan, jdoubleArray j_in, jdoubleArray j_out, jint mode)
{
	// arguments
	jdouble *in, *out;
	jboolean in_copy, out_copy;

	// lock arrays
#ifdef USE_GET_PRIMITIVE_ARRAY_CRITIAL
	in  = (jdouble*)env->GetPrimitiveArrayCritical(j_in, &in_copy);
	out = (jdouble*)env->GetPrimitiveArrayCritical(j_out, &out_copy);
#else
	in  = env->GetDoubleArrayElements(j_in, &in_copy);
	out = env->GetDoubleArrayElements(j_out, &out_copy);
#endif
	//if ( in_copy) cout << "fftw_execute:  IN COPY!" << endl;
	//if (out_copy) cout << "fftw_execute: OUT COPY!" << endl;

	// do the transform
	fftw_plan plan = (fftw_plan)j_plan;
	//fftw_print_plan(plan);
	switch (mode) {
		case uni_hamburg_yamms_math_fft_FFT_fftw3Impl_MODE_C2C_FORW:
		case uni_hamburg_yamms_math_fft_FFT_fftw3Impl_MODE_C2C_BACKW:
			fftw_execute_dft(plan, (fftw_complex*)in, (fftw_complex*)out);
			break;
		case uni_hamburg_yamms_math_fft_FFT_fftw3Impl_MODE_R2C:
			fftw_execute_dft_r2c(plan, (double*)in, (fftw_complex*)out);
			break;
		case uni_hamburg_yamms_math_fft_FFT_fftw3Impl_MODE_C2R:
			fftw_execute_dft_c2r(plan, (fftw_complex*)in, (double*)out);
			break;
	}
	
	// unlock arrays
#ifdef USE_GET_PRIMITIVE_ARRAY_CRITIAL
	env->ReleasePrimitiveArrayCritical(j_in, in, 0);
	env->ReleasePrimitiveArrayCritical(j_out, out, 0);
#else
	env->ReleaseDoubleArrayElements(j_in, in, 0);
	env->ReleaseDoubleArrayElements(j_out, out, 0);
#endif
}

/*
 * Class:     uni_hamburg_yamms_math_fft_FFT_fftw3Impl
 * Method:    fftw_destroy_plan
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_uni_hamburg_yamms_math_fft_FFT_1fftw3Impl_fftw_1destroy_1plan
  (JNIEnv *env, jobject obj, jlong j_plan)
{
	if (j_plan != 0) {
		fftw_plan plan = (fftw_plan)j_plan;
		fftw_destroy_plan(plan);
	}
}

/*
 * Class:     uni_hamburg_m3sc_math_fft_fftw3_FFT_fftw3Impl
 * Method:    fftw_print_plan
 * Signature: (J)V
 */
/*
JNIEXPORT void JNICALL Java_uni_hamburg_m3sc_math_fft_fftw3_FFT_1fftw3Impl_fftw_1print_1plan
  (JNIEnv *env, jobject obj, jlong j_plan)
{
	fftw_plan plan = (fftw_plan)j_plan;
	fftw_print_plan(plan);
}
*/

/*
 * Class:     uni_hamburg_yamms_math_fft_FFT_fftw3Impl
 * Method:    fftw_plan_with_nthreads
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_uni_hamburg_yamms_math_fft_FFT_1fftw3Impl_fftw_1plan_1with_1nthreads
  (JNIEnv *env, jobject obj, jint j_num_threads)
{
#ifdef USE_FFTW_THREADS
	int num_threads = j_num_threads;
	fftw_plan_with_nthreads(num_threads);
#endif
}

/*
 * Class:     uni_hamburg_yamms_math_fft_FFT_fftw3Impl
 * Method:    fftw_initialize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uni_hamburg_yamms_math_fft_FFT_1fftw3Impl_fftw_1initialize
  (JNIEnv *env, jclass klass)
{
#ifdef USE_FFTW_THREADS
	int ok = fftw_init_threads();
	if (!ok) return -1;
#endif
	return 0;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
// TOOLS                                                                                                //
//////////////////////////////////////////////////////////////////////////////////////////////////////////

static int get_iodim_array(JNIEnv *env, jobjectArray j_dims, fftw_iodim *dims)
{
	int size = env->GetArrayLength(j_dims);
	for (int i=0; i<size; ++i) {
		jobject j_dim = env->GetObjectArrayElement(j_dims, i);
		get_iodim(env, j_dim, &dims[i]);
	}
	return size;
}

static int get_iodim(JNIEnv *env, jobject j_dim, fftw_iodim *dim)
{
	jclass cls = env->GetObjectClass(j_dim);
	dim->n  = env->GetIntField(j_dim, env->GetFieldID(cls, "n", "I"));
	dim->is = env->GetIntField(j_dim, env->GetFieldID(cls, "is", "I"));
	dim->os = env->GetIntField(j_dim, env->GetFieldID(cls, "os", "I"));
}

static void enter_critical(JNIEnv *env)
{
	env->MonitorEnter(env->FindClass("jfftw3/Plan"));
}

static void leave_critical(JNIEnv *env)
{
	env->MonitorExit(env->FindClass("jfftw3/Plan"));
}




