package sg.edu.nus.iss.day3_workshop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// https://www.baeldung.com/java-write-to-file

@SpringBootTest
public class FileIOTest {

    String dirPath = "\\data2";
    String stringLogin = "write.txt";

    String fileName = dirPath + File.separator + stringLogin;

    @Test
    public void whenAppendStringUsingBufferedWritter_thenOldContentShouldExistToo()
            throws IOException {

        String str = "World";
        BufferedWriter writer = new BufferedWriter(new FileWriter(dirPath + File.separator + stringLogin, true));
        writer.append('\n');
        writer.append(str);

        writer.close();
    }

    @Test
    public void givenWritingStringToFile_whenUsingPrintWriter_thenCorrect()
            throws IOException {
        FileWriter fileWriter = new FileWriter(dirPath + File.separator + stringLogin);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("Some String");
        printWriter.printf("Product name is %s and its price is %d $", "iPhone", 1000);
        printWriter.close();
    }

    @Test
    public void givenWritingStringToFile_whenUsingFileOutputStream_thenCorrect()
            throws IOException {
        String str = "Hello";
        FileOutputStream outputStream = new FileOutputStream(dirPath + File.separator + stringLogin);
        byte[] strToBytes = str.getBytes();
        outputStream.write(strToBytes);

        outputStream.close();
    }

    @Test
    public void givenWritingToFile_whenUsingDataOutputStream_thenCorrect()
            throws IOException {
        String value = "Hello";
        FileOutputStream fos = new FileOutputStream(dirPath + File.separator + stringLogin);
        DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
        outStream.writeUTF(value);
        outStream.close();

        // verify the results
        String result;
        FileInputStream fis = new FileInputStream(dirPath + File.separator + stringLogin);
        DataInputStream reader = new DataInputStream(fis);
        result = reader.readUTF();
        reader.close();

        assertEquals(value, result);
    }

    @Test
    public void whenWritingToSpecificPositionInFile_thenCorrect() throws IOException {
        int data1 = 2014;
        int data2 = 1500;
        
        writeToPosition(fileName, data1, 4);
        assertEquals(data1, readFromPosition(fileName, 4));
        
        // writeToPosition(fileName2, data2, 4);
        // assertEquals(data2, readFromPosition(fileName, 4));
    }

    private void writeToPosition(String filename, int data, long position) throws IOException {
        RandomAccessFile writer = new RandomAccessFile(filename, "rw");
        writer.seek(position);
        writer.writeInt(data);
        writer.close();
    }

    private int readFromPosition(String filename, long position) throws IOException {
        int result = 0;
        RandomAccessFile reader = new RandomAccessFile(filename, "r");
        reader.seek(position);
        result = reader.readInt();
        reader.close();
        return result;
    }

    @Test
    public void givenWritingToFile_whenUsingFileChannel_thenCorrect() throws IOException {
        RandomAccessFile stream = new RandomAccessFile(fileName, "rw");
        FileChannel channel = stream.getChannel();
        String value = "Hello";
        byte[] strBytes = value.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(strBytes.length);
        buffer.put(strBytes);
        buffer.flip();
        channel.write(buffer);
        stream.close();
        channel.close();

        // verify
        RandomAccessFile reader = new RandomAccessFile(fileName, "r");
        assertEquals(value, reader.readLine());
        reader.close();
    }

    @Test
    public void givenUsingJava7_whenWritingToFile_thenCorrect() throws IOException {
        String str = "Hello";

        Path path = Paths.get(fileName);
        byte[] strToBytes = str.getBytes();

        Files.write(path, strToBytes);

        String read = Files.readAllLines(path).get(0);
        assertEquals(str, read);
    }

    @Test
    public void whenWriteToTmpFile_thenCorrect() throws IOException {
        String toWrite = "Hello";
        File tmpFile = File.createTempFile("test", ".tmp");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(toWrite);
        writer.close();

        BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
        assertEquals(toWrite, reader.readLine());
        reader.close();
    }

    @Test
    public void whenTryToLockFile_thenItShouldBeLocked() throws IOException {
        RandomAccessFile stream = new RandomAccessFile(fileName, "rw");
        FileChannel channel = stream.getChannel();

        FileLock lock = null;
        try {
            lock = channel.tryLock();
        } catch (final OverlappingFileLockException e) {
            stream.close();
            channel.close();
        }
        stream.writeChars("test lock");
        lock.release();

        stream.close();
        channel.close();
    }
}
