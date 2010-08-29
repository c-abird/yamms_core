#g++ -shared -fPIC *.cpp -I/opt/java/include -I/opt/java/include/linux -I/home/gselke/fftw/include -L/home/gselke/fftw/lib -l fftw3 -l fftw3_threads -o libwrapfftw3.so
g++ -shared -fPIC *.cpp -I/opt/java/include -I/opt/java/include/linux -I/home/gselke/fftw/include -L/home/gselke/fftw/lib -l fftw3 -o libwrapfftw3.so
# cp libwrapfftw3.so /home/gselke/workspace


