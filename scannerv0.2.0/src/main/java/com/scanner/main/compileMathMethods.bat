gcc -I"C:\Program Files\Java\jdk-12.0.1\include" -I"C:\Program Files\Java\jdk-12.0.1\include\win32" -c -o com_scanner_main_MathMethods.o com_scanner_main_MathMethods.c
gcc -shared -o com_scanner_main_MathMethods.dll com_scanner_main_MathMethods.o