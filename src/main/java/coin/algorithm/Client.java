package coin.algorithm;

import coin.algorithm.func.FuncService;
import coin.algorithm.func.MMapFuncService;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        String func = "isBuy";
        String ident = "testBot";
        FuncService funcService = new MMapFuncService();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 612040; i++) {
            String params = String.valueOf(i);
            System.out.println(funcService.call(func, params, ident));
        }
        long endTime = System.currentTimeMillis();
        System.out.println("time-milis: " + (endTime - startTime));
    }
}
