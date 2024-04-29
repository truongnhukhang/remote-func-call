package coin.algorithm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.*;
import java.lang.invoke.VarHandle;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class Server {
    public static void main(String[] args) throws IOException {
        String func = "isBuy";
        String ident = "testBot";
        String key = func + ident;
        Path resPath = Path.of(key + ".res");
        Path reqPath = Path.of(key + ".req");
        resPath.toFile().delete();
        reqPath.toFile().delete();
        MappedByteBuffer resBuffer = new RandomAccessFile(resPath.toFile(), "rwd").getChannel()
                                                                                  .map(FileChannel.MapMode.READ_WRITE,
                                                                                       0,
                                                                                       128 * 1000);
        MappedByteBuffer reqBuffer = new RandomAccessFile(reqPath.toFile(), "rwd").getChannel()
                                                                                  .map(FileChannel.MapMode.READ_WRITE,
                                                                                       0,
                                                                                       128 * 1000);
        while (true) {
            char header = reqBuffer.getChar();
            if(header=='@') {
                int length = reqBuffer.getInt();
                byte[] params = new byte[length];
                reqBuffer.get(params);
                String callParams = new String(params, StandardCharsets.UTF_8);
                Integer idx = Integer.parseInt(callParams);
                byte[] randomValue = (idx + "-" + idx).getBytes();
                byte[] rs = new byte[6 + randomValue.length];
                ByteBuffer buffer = ByteBuffer.wrap(rs);
                buffer.putChar('@');
                buffer.putInt(randomValue.length);
                buffer.put(randomValue);
                resBuffer.put(buffer.array());
            }
            reqBuffer.position(0);
            resBuffer.position(0);
        }
    }
}