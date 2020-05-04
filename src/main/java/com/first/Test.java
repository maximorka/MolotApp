package com.first;

/**
 * Created by pc3 on 17.04.2020.
 */
public class Test {
    public static void main(String[] args) {
//        String ww = "GET /arduino/meteo?key=1&temp=20&hum=83.2 HTTP/1.0\r\nHost: goiteens.club\r\nConnection: close\r\n";
//        System.out.println(ww.length());
//         ww = "GET /arduino/meteo/update.php?key=6&temp=20&hum=83.2 HTTP/1.1\n\nHost: goiteens.club\n" +
//                 "Connection: close\n";
//        System.out.println(ww.length());


        //dt /= _iterationCount;
for(int q = 0; q < 10000; ++q) {
    Test2 tmp = new Test2();
    tmp.temp();
}

//for(int i=0;i<10000;i++){
//
//
//}

//        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
//        System.out.println("Number of currently running threads: " + threads.size());
//        for (Thread thread : threads.keySet()) {
//            System.out.println(thread.getName());
//        }
    }

    public static class Test2{
        double val = 0;
        double dt = 3.14;
        double d = 1.0;
        String te = "qwerty";
        String t1 = "qwertwerfefergfrtgtrgtrbtbry";
        public void temp(){
            long start = System.currentTimeMillis();
           // for (int i = 0; i < 10000; ++i) {
                for (int k = 0; k < 500000; k++) {

                    String r = t1+te;
                    double x = Math.sin(val);
                    val += ((dt*19)/0.00003);
                    val+=d++;
                }
          //  }
            long stop = System.currentTimeMillis();
            System.out.println("time:" + (stop - start));
        }
    }
}
