package edu.uw.nemo.proto.converter;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joglekaa on 4/15/14.
 */
public class DIPConverter {

    public int convert(String srcFile, String destFile) throws IOException, URISyntaxException {
        int count = 0;
        BufferedReader input = openInputFile(srcFile);

        BufferedWriter ouput = openOutputFile(destFile);
        String header = input.readLine();
        Map<String, Integer> nodeToId = new HashMap<String, Integer>();
        Map<Integer, String> idToNode = new HashMap<Integer, String>();
        if (header != null && !header.isEmpty()) {
//            prnt("header", header.split("\t"));
            String line = null;
            while ((line = input.readLine()) != null) {
                // extract node id 1, node id 2
                String[] split = line.trim().split("\t");
                int id1 = mapNode(nodeToId, idToNode, count, split[0]);
                if (id1 == count) {
                    count++;
                }
                int id2 = mapNode(nodeToId, idToNode, count, split[1]);
                if (id2 == count) {
                    count++;
                }
//                prnt("record", split);
                // validate
                ouput.write(String.valueOf(id1));
                ouput.write("\t");
                ouput.write(String.valueOf(id2));
                ouput.newLine();
            }
        }
        ouput.flush();
        ouput.close();
        input.close();
        return count;
    }
    
    private int mapNode(Map<String, Integer> nodeToId, Map<Integer, String> idToNode, int count, String node) {
        Integer id = nodeToId.get(node);
        if (id == null) {
            id = count;
            nodeToId.put(node, id);
            idToNode.put(id, node);
        }
        return id;
    }

    private void prnt(String type, String[] split) {
        System.out.println(type + ":");
        for (int i = 0; i < split.length; i++) {
            System.out.println(i + ": [" + split[i] + "]");
        }
        System.out.println("finished");
    }

    private BufferedReader openInputFile(String srcFile) throws IOException, URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(srcFile);
        Path path = Paths.get(url.toURI());
        Charset charset = Charset.forName("US-ASCII");
        return Files.newBufferedReader(path, charset);
    }

    private BufferedWriter openOutputFile(String destFile) throws IOException, URISyntaxException {
        URL url =Thread.currentThread().getContextClassLoader().getResource(destFile);
        Path path = Paths.get(url.toURI());
        Charset charset = Charset.forName("US-ASCII");
        return Files.newBufferedWriter(path, charset);
    }

}
