package com.example.javatest;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CopyString {

    private static int MODE_S2T = 0;
    private static int MODE_S2M = 1;
    private static int MODE_T2M = 2;
    private static int MODE_M2T = 3;
    private static int MODE_T2B = 4;
    private static int MODE_B2T = 5;

    private static String[] mLocalName = new String[]{
            "ar",
            "de",
            "es",
            "fr",
            "in",
            "it",
            "ja",
            "ko",
            "ms",
            "pt-rBR",
            "ru",
            "tr",
            "th",
            "uk",
            "vi",
            "zh-rCN",
            "zh-rTW"
    };
    private static String[] mNewStringsKeys = new String[]{
            "\"speed_exceeding_10x_loss_audio\"",
    };

    //    private static String ADD_HEADER_STRING = "    <!--online music and reward video-->";
    private static String ADD_HEADER_STRING = "";
    private static String ADD_FOOTER_STRING = "</resources>";


    private static int COPY_MODE = MODE_T2M;
//    private int COPY_MODE = MODE_S2T;

    public static void createList() throws IOException {
        File shotStringFolder = new File("D:\\Inshot\\trunk\\InstaShot\\src\\main\\res");
        File trimmerStringFolder = new File("D:\\code\\Android\\Trimmer\\trunk\\YouCut\\src\\main\\res");
        File makerStringFolder = new File("D:\\code\\Android\\marker\\trunk\\Guru\\src\\main\\res");
//        File makerStringFolder = new File("D:\\VideoMaker\\branch\\guru_trunk_branch\\Guru\\src\\main\\res");

        File trunkStringFolder = trimmerStringFolder;
//        File branchStringFolder = new File("D:\\Trimmer\\branch\\1.260.60_record\\YouCut\\src\\main\\res");
        File branchStringFolder = new File("D:\\inshot\\1.584.221\\InstaShot\\src\\main\\res");
        File otherStringFolder = new File("C:\\Users\\bao\\Desktop\\videoglitchRes");
//        File otherStringFolder = new File("D:\\VideoMaker\\branch\\1.224.41\\Guru\\src\\main\\res");
//        File otherStringFolder = new File("D:\\inshot\\1.584.221\\InstaShot\\src\\main\\res");
//        File branchStringFolder = new File("D:\\VideoMaker\\branch\\1.150.25_music\\Guru\\src\\main\\res");
        File srcFolder;
        File desFolder;
        if (COPY_MODE == MODE_S2T) {
            srcFolder = shotStringFolder;
            desFolder = trimmerStringFolder;
        } else if (COPY_MODE == MODE_S2M) {
            srcFolder = shotStringFolder;
            desFolder = makerStringFolder;
        } else if (COPY_MODE == MODE_T2M) {
            srcFolder = trimmerStringFolder;
            desFolder = makerStringFolder;
        } else if (COPY_MODE == MODE_M2T) {
            srcFolder = makerStringFolder;
            desFolder = trimmerStringFolder;
        } else if (COPY_MODE == MODE_T2B) {
            srcFolder = trunkStringFolder;
            desFolder = branchStringFolder;
        } else if (COPY_MODE == MODE_B2T) {
            srcFolder = branchStringFolder;
            desFolder = trunkStringFolder;
        } else {
            srcFolder = makerStringFolder;
            desFolder = trunkStringFolder;
        }

        if (srcFolder.exists() && srcFolder.isDirectory()) {
            File[] files = srcFolder.listFiles();
            for (File file : files) {
                if (file.isDirectory() && file.getName().startsWith("values") && isTargetFile(file)) {
                    File[] files1 = file.listFiles();
                    for (File srcFile : files1) {
                        if (srcFile.getName().startsWith("strings.xml")) {
                            File desFile = new File(new File(desFolder, file.getName()), srcFile.getName());
                            File tempFile = new File(new File(desFolder, file.getName()), ".temp" + srcFile.getName());
                            if (!tempFile.exists()) {
                                tempFile.createNewFile();
                            }
                            BufferedReader reader = null;
                            BufferedWriter writer = null;
                            try {
                                reader = new BufferedReader(new FileReader(desFile));
                                writer = new BufferedWriter(new FileWriter(tempFile, false));
                                String s;
                                while ((s = reader.readLine()) != null) {
                                    if (s.equals(ADD_FOOTER_STRING)) {
                                        continue;
                                    }
                                    writer.write(s);
                                    writer.newLine();
                                    writer.flush();
                                }

                                reader.close();
                                writer.close();

                                reader = new BufferedReader(new FileReader(srcFile));
                                writer = new BufferedWriter(new FileWriter(tempFile, true));
                                if (!ADD_HEADER_STRING.equals("")) {
                                    writer.newLine();
                                    writer.write(ADD_HEADER_STRING);
                                    writer.newLine();
                                    writer.flush();
                                }

                                while ((s = reader.readLine()) != null) {
                                    for (String stringKey : mNewStringsKeys) {
                                        if (s.contains(stringKey)) {
                                            writer.write(s);
                                            writer.newLine();
                                            writer.flush();
                                        }
                                    }
                                }
                                writer.write(ADD_FOOTER_STRING);
                                writer.flush();

                                reader.close();
                                writer.close();

                                desFile.delete();
                                tempFile.renameTo(desFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (reader != null) {
                                    try {
                                        reader.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                if (writer != null) {
                                    try {
                                        writer.close();
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isTargetFile(File file) {
        String name = file.getName();
        for (String s : mLocalName) {
            if (name.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] arg) throws IOException {
        createList();
    }

}