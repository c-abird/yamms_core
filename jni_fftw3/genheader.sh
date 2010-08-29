
CLAASPATH=../src

javac -cp $CLAASPATH ../src/uni/hamburg/yamms/math/fft/FFT_fftw3Impl.java
javah -classpath $CLAASPATH uni.hamburg.yamms.math.fft.FFT_fftw3Impl

