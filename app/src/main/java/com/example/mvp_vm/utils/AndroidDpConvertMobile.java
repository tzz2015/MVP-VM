package com.example.mvp_vm.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ljf++
 * @version AndroidDpConvertMobile.java, v 0.1 2019-03-25 下午1:52 ljf++
 */
public class AndroidDpConvertMobile {
    private static final String VERSION = "1.00.00";
    protected final static String DIMENS_FILE = "dimens.xml";
    protected final static String SIZES_FILE = "txsizes.xml";
    protected final static String STRINGS_FILE = "strings.xml";

    protected static final String DEFAULT_MODULE_DIR = "app";
    protected final boolean debug;
    protected String moduleDir;

    public AndroidDpConvertMobile(String moduleDir) {
        this(false, moduleDir);
        this.moduleDir = moduleDir;
    }

    public AndroidDpConvertMobile() {
        this(false, DEFAULT_MODULE_DIR);
    }

    public AndroidDpConvertMobile(boolean debug, String moduleDir) {
        this.moduleDir = moduleDir;
        this.debug = debug;
    }

    /**
     * 是否转换txsizes.xml文件
     */
    protected boolean isConvertTextSize() {
        Sw300dpXhdpiConverter convert = new Sw300dpXhdpiConverter();
        File file = new File(convert.getDefaultValuesPath(), SIZES_FILE);
        return file.exists();
    }

    /**
     * 是否转换strings.xml以用于调试
     */
    protected boolean isConvertDebugString() {
        return false;
    }

    /**
     * 实现多尺寸分辨率转换
     */
    public final void convert() {
        final String className = getClassTag();
        System.out.printf("start " + className + ": \n");
        String current = System.getProperty("user.dir");
        System.out.printf("working directory: %s \n", current);

        List<DimenConverter> list = new ArrayList<>();
        fetchConverters(list);

        boolean success = true;
        for (int i = 0; i < list.size() && success; i++) {
            success = list.get(i).convert();
        }

        if (success) {
            System.out.printf(className + " complete.\n");
        } else {
            System.out.printf(className + " failed!!!.\n");
        }
        System.out.printf(className + " version: " + VERSION);
    }

    protected String getClassTag() {
        return getClass().getSimpleName();
    }

    protected void fetchConverters(List<DimenConverter> list) {
        list.add(new Sw300dpHdpiConverter());
        list.add(new Sw300dpXhdpiConverter());
        list.add(new Sw300dpXxhdpiConverter());
        list.add(new Sw360dpHdpiConverter());
        list.add(new Sw392dpConverter());
        list.add(new Sw400dpConverter());
        list.add(new Sw400dpHdpiConverter());
        list.add(new Sw400dp400dpiConverter());
        list.add(new Sw400dp420dpiConverter());
        list.add(new Sw480dp480dpiConverter());
        list.add(new Sw400dp560dpiConverter());
        list.add(new Sw560dp400dpiConverter());
        list.add(new Sw500dpXhdpiConverter());
        list.add(new Sw700dpMdpiConverter());
        list.add(new Sw700dpHdpiConverter());
    }

    /**
     * 480 x 854 hdpi(1.5)
     */
    protected class Sw300dpHdpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value * 4.0f / 9.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw300dp-hdpi";
        }
    }

    /**
     * 720 x 1280 xhdpi(2.0)
     */
    protected class Sw300dpXhdpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value / 2.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw300dp-xhdpi";
        }
    }


    /**
     * 1080 x 1920 xxhdpi(3.0)
     */
    protected class Sw300dpXxhdpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value / 2.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw300dp-xxhdpi";
        }
    }

    /**
     * 540 x 888 hdpi(1.5)
     */
    protected class Sw360dpHdpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value / 2.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw360dp-hdpi";
        }
    }

    /**
     * 1080 * 1920 sw392(2.75)
     */
    protected class Sw392dpConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value * 6.0 / 11.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw392dp";
        }
    }

    /**
     * sw400 default
     */
    protected class Sw400dpConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value * 3.0 / 5.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw400dp";
        }
    }

    /**
     * sw400 400dpi(2.5)
     */
    protected class Sw400dp400dpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value * 3.0 / 5.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw400dp-400dpi";
        }
    }

    /**
     * 720 x 1280 hdpi(1.5)
     */
    protected class Sw400dpHdpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value * 2.0 / 3.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw400dp-hdpi";
        }
    }

    /**
     * 1080 x 1920 420dpi(2.625)
     */
    protected class Sw400dp420dpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value * 4.0 / 7.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw400dp-420dpi";
        }
    }

    /**
     * 2560 x 1440 560hdpi(3.5)
     */
    protected class Sw400dp560dpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value * 4.0 / 7.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw400dp-560dpi";
        }
    }

    /**
     * 1440 x2560  xxxhdpi(3.5)
     */
    protected class Sw560dp400dpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value * 4.0 / 7.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw560dp-xxxhdpi";
        }
    }

    /**
     * 720 x 1280 480dpi(2.0)
     */
    protected class Sw480dp480dpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value / 2f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw480dp-480dpi";
        }
    }

    /**
     * 720 x 1280 xhdpi(2.0)
     */
    protected class Sw500dpXhdpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value * 3.0 / 4.0f;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw500dp-xhdpi";
        }
    }

    /**
     * 720 x 1280 mdpi(1.0)
     */
    protected class Sw700dpMdpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw700dp-mdpi";
        }
    }

    /**
     * 1080 x 1920 hdpi(1.5)
     */
    protected class Sw700dpHdpiConverter extends DpiBaseConverter {

        @Override
        public String noteText() {
            return getClass().getSimpleName();
        }

        @Override
        public double convertDimen(double value) {
            return value;
        }

        @Override
        public double convertTxSizeDimen(double value) {
            return convertDimen(value);
        }

        @Override
        public String getConvertValuesName() {
            return "values-sw700dp-hdpi";
        }
    }

    protected interface IConverter {
        /**
         * 单位转换
         *
         * @param unit，  dimen单位，如: sp, dip, dp, px
         * @param value, dimen数值
         * @return
         */
        double conversion(final String unit, double value);

        /**
         * 获取单位
         *
         * @return String
         */
        String getUnits();
    }

    protected abstract class DpiBaseConverter implements DimenConverter {

        public final String getCurrentDir() {
            return System.getProperty("user.dir");
        }

        public final String split() {
            return System.getProperty("file.separator");
        }

        public final String getResourcesPath() {
            return getStudioResourcesDir() + split() + "res";
        }

        public final String getStudioResourcesDir() {
            return getCurrentDir() + split() + moduleDir + split() + "src" + split() + "main";
        }

        public String getDefaultValuesPath() {
            return getResourcesPath() + split() + "values";
        }

        public String getConvertValuesPath() {
            return getResourcesPath() + split() + getConvertValuesName();
        }

        @Override
        public final boolean convert() {
            if (!performDimenConvert()) {
                System.out.printf(noteText() + " convert(), performDimenConvert failed! \n");
                return false;
            }
            if (isConvertTextSize() && !performTxSizeDimenConvert()) {
                System.out.printf(noteText() + " convert(), performTxSizeDimenConvert failed! \n");
                return false;
            }
            if (isConvertDebugString() && !performStringConvert()) {
                System.out.printf(noteText() + " convert(), performStringConvert failed! \n");
                return false;
            }
            System.out.printf(noteText() + " perform convert complete\n");
            return true;
        }

        /**
         * noteText
         *
         * @return String
         */
        public abstract String noteText();

        /**
         * get the target conversion dimension file name. such as "values-xxx"
         *
         * @return String
         */
        public abstract String getConvertValuesName();

        /**
         * convert dimens of view width, height etc.
         *
         * @param value
         * @return double
         */
        public abstract double convertDimen(double value);

        /**
         * convert dimens of text size.
         *
         * @param value
         * @return double
         */
        public abstract double convertTxSizeDimen(double value);

        /**
         * convert dimen of view width, height, etc.
         *
         * @return boolean
         */
        public boolean performDimenConvert() {
            IConverter c = new IConverter() {
                @Override
                public double conversion(String unit, double value) {
                    return convertDimen(value) / 1.04;
                }

                @Override
                public String getUnits() {
                    return "sp|dip|dp|px";
                }
            };
            return performDimenConvert(getDefaultValuesPath(), getConvertValuesPath(), DIMENS_FILE, c);
        }

        /**
         * convert dimen of text size.
         */
        public boolean performTxSizeDimenConvert() {
            IConverter c = new IConverter() {

                @Override
                public double conversion(String unit, double value) {
                    return convertTxSizeDimen(value) / 1.04;
                }

                @Override
                public String getUnits() {
                    return "sp|dip|dp|px";
                }
            };
            return performDimenConvert(getDefaultValuesPath(), getConvertValuesPath(), SIZES_FILE, c);
        }

        /**
         * @param defDimenDir,     the default dimen xml file's directory. usually
         *                         "<project.dir>/res/values"
         * @param convertDimenDir, the conversion dimen xml file's directory. usually
         *                         "<project.dir>/res/values-xxx"
         * @param filename,        the dimen xml file's name, such as dimens.xml, txsizes.txt
         * @param converter
         * @return return true when perform conversion success, otherwise return
         * false.
         */
        protected final boolean performDimenConvert(String defDimenDir, String convertDimenDir, String filename,
                                                    IConverter converter) {
            final File defFile = new File(defDimenDir, filename);
            if (!defFile.exists()) {
                throw new IllegalStateException(
                        "performDimenConvert. default dimens{" + defFile.getAbsolutePath() + "} file not exists.");
            }

            File dir = new File(convertDimenDir);
            if (!dir.exists()) {
                dir.mkdir();
            }

            File cvFile = new File(convertDimenDir, filename);
            if (!cvFile.exists()) {
                try {
                    cvFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            cvFile.setWritable(true, false);

            OutputStreamWriter osWriter = null;
            BufferedWriter writer = null;

            InputStreamReader isReader = null;
            BufferedReader reader = null;

            try {
                osWriter = new OutputStreamWriter(new FileOutputStream(cvFile), "utf-8");
                writer = new BufferedWriter(osWriter);

                isReader = new InputStreamReader(new FileInputStream(defFile), "utf-8");
                reader = new BufferedReader(isReader);

                int count = 0;
                final String UNITS = converter.getUnits();
                final String REGULAR = String.format("\\>[\\s]*[-]?[0-9]+[.]?[0-9]*(%s)[\\s]*\\<", UNITS);
                Pattern p = Pattern.compile(REGULAR);
                String rLine = null, wLine = null;
                while ((rLine = reader.readLine()) != null) {
                    Matcher m = p.matcher(rLine);
                    if (m.find()) {
                        String g = m.group();
                        String unitName = getUnitName(g, UNITS.split("[|]"));
                        double value = getUnitValue(g);
                        double dimen = converter.conversion(unitName, value);
                        String text = String.format(">%.2f%s<", dimen, unitName);
                        wLine = rLine.replaceAll(REGULAR, text);
                    } else {
                        wLine = rLine;
                    }
                    if (count > 0) {
                        writer.append('\n');
                    }
                    writer.append(wLine);
                    writer.flush();
                    rLine = null;
                    wLine = null;

                    ++count;
                }

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != writer) {
                        writer.close();
                        writer = null;
                    }
                    if (null != osWriter) {
                        osWriter.close();
                        osWriter = null;
                    }

                    if (null != reader) {
                        reader.close();
                        reader = null;
                    }
                    if (null != isReader) {
                        isReader.close();
                        isReader = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cvFile.setReadOnly();
            }

            return false;
        }

        /**
         * 用于调试使用，用来strings.xml转换，主要用于调试
         */
        public boolean performStringConvert() {
            String d = getConvertValuesPath();
            final String dpType = getConvertValuesName().replace("values-", "");
            if (debug) {
                System.out.printf("d: %s, dptype: %s\n", d, dpType);
            }
            File f = new File(getDefaultValuesPath() + split() + STRINGS_FILE);
            if (!f.exists()) {
                System.out.printf("file: {%s} is not exists\n", f.getAbsoluteFile());
                return false;
            }

            File cvFile = new File(getConvertValuesPath(), STRINGS_FILE);
            if (!cvFile.exists()) {
                try {
                    cvFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                System.out.printf("create file: %s\n", f.getAbsoluteFile());
            }
            cvFile.setWritable(true, false);

            InputStreamReader isReader = null;
            BufferedReader reader = null;

            OutputStreamWriter osWriter = null;
            BufferedWriter writer = null;
            try {
                isReader = new InputStreamReader(new FileInputStream(f), "utf-8");
                reader = new BufferedReader(isReader);

                osWriter = new OutputStreamWriter(new FileOutputStream(cvFile, false), "utf-8");
                writer = new BufferedWriter(osWriter);

                int count = 0;
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("<string name=\"dimen_adaptive_title\">DensityDemo[default]</string>")) {
                        line = line.replace("default", dpType);
                    }
                    if (count > 0) {
                        writer.write('\n');
                    }
                    writer.write(line);
                    writer.flush();

                    ++count;
                }

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != reader) {
                        reader.close();
                        reader = null;
                    }
                    if (null != isReader) {
                        isReader.close();
                        isReader = null;
                    }

                    if (null != writer) {
                        writer.close();
                        writer = null;
                    }
                    if (null != osWriter) {
                        osWriter.close();
                        osWriter = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cvFile.setReadOnly();
            }

            return false;
        }

        private String getUnitName(String value, final String[] units) {
            String name = "";
            String regular = "";
            for (int i = 0; i < units.length; i++) {
                if (i > 0) {
                    regular += "|";
                }
                regular += units[i];
            }
            if (units.length > 0) {
                regular = "(" + regular;
                regular = regular + ")";
                Pattern p = Pattern.compile(regular);
                Matcher m = p.matcher(value);
                if (m.find()) {
                    name = m.group();
                }
            }
            if (debug) {
                System.out.printf("getUnitName, value: %s, regx: %s, name: %s\n ", value, regular, name);
            }
            return name;
        }

        public double getUnitValue(String text) {
            final String REG = "[-]?[0-9]+[.]?[0-9]*";
            Pattern p = Pattern.compile(REG);
            Matcher m = p.matcher(text);
            if (!m.find()) {
                throw new IllegalArgumentException("getUnitValue. must content dimen value. text: " + text);
            }
            return Double.parseDouble(m.group());
        }
    }

    interface DimenConverter {
        /**
         * 多尺寸分辨率转换接口
         *
         * @return boolean
         */
        boolean convert();
    }


}