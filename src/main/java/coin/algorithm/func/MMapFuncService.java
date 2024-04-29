package coin.algorithm.func;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.foreign.Arena;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

public class MMapFuncService implements FuncService {
    Map<String, MappedByteBuffer> instanceMap = new ConcurrentHashMap<>();

    @Override
    public String call(String func, String param, String ident) throws IOException {
        String key = func + ident;
        String keyReq = key + "req";
        String keyRes = key + "res";
        MappedByteBuffer reqBuffer = instanceMap.get(keyReq);
        MappedByteBuffer resBuffer = instanceMap.get(keyRes);
        if (reqBuffer == null) {

            Path reqPath = Path.of(key + ".req");
            RandomAccessFile file = new RandomAccessFile(reqPath.toFile(), "rwd");
            reqBuffer = file.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, 128 * 1000);
            instanceMap.put(keyReq, reqBuffer);

        }
        if (resBuffer == null) {
            Path ResPath = Path.of(key + ".res");
            RandomAccessFile file = new RandomAccessFile(ResPath.toFile(), "r");
            resBuffer = file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, 128 * 1000);
            instanceMap.put(keyRes, resBuffer);
        }
        byte[] reqBytes = new byte[6+param.getBytes().length];
        ByteBuffer buffer = ByteBuffer.wrap(reqBytes);
        buffer.putChar('@');
        buffer.putInt(param.getBytes().length);
        buffer.put(param.getBytes());
        reqBuffer.put(buffer.array());
        long starTime = System.currentTimeMillis();
        while (true) {
            char header = resBuffer.getChar();
            if (header=='@') {
                int length = resBuffer.getInt();
                byte[] rs = new byte[length];
                resBuffer.get(rs);
                return new String(rs, StandardCharsets.UTF_8);
            }
            if (System.currentTimeMillis() - starTime > 5000) {
                return "timeout";
            }
            reqBuffer.position(0);
            resBuffer.position(0);
        }
    }




}
