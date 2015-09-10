package codeOrchestra.colt.updater;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.prefs.Preferences;

/**
 * @author Dima Kruk
 */
class ColtUpdater {

    private static final String NUM_OF_RESTART = "num_of_restart";
    private static Preferences preferences = Preferences.userNodeForPackage(ColtUpdater.class);

    public static void main(String[] args) {
        File baseDir = getApplicationBaseDir();
        File jarDir;
        if (SystemInfo.isMac) {
            jarDir = new File(baseDir, "Contents/Java");
        } else if (SystemInfo.isWindows){
            jarDir = new File(baseDir, "lib");
        } else {
            jarDir = new File(baseDir, "app");
        }
        File updates = new File(baseDir.getPath() + File.separator + "updates");
        if (updates.exists()) {
            File[] files = updates.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith(".jar")) {
                        try {
                            if (checkFileMD5(f)) {
                                copyFile(f, new File(jarDir.getPath(), f.getName()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        try {
            File core = new File(jarDir, "colt-core.jar");
            URLClassLoader loader = new URLClassLoader(new URL[] {core.toURI().toURL()}, ColtUpdater.class.getClassLoader());
            Class app = Class.forName("codeOrchestra.colt.core.ui.ColtApplication", true, loader);
            Method[] methods = app.getMethods();
            for (Method m : methods) {
                if (m.getName().equals("main")) {
                    m.invoke(null, new Object[]{args});
                    break;
                }
            }

            preferences.putInt(NUM_OF_RESTART, 0);
            preferences.sync();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            int restarts = preferences.getInt(NUM_OF_RESTART, 0);
            if (restarts < 5) {
                restarts++;
                preferences.putInt(NUM_OF_RESTART, restarts);
                restart();
            } else {
                //todo: need reinstall
            }
        }
    }

    public static boolean checkFileMD5(File file) {
        File md = new File(file.getParentFile(), file.getName() + ".MD5");
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(md));
                String md_str = bufferedReader.readLine();
                bufferedReader.close();
                md.delete();
                String fileDigestMD5 = getFileDigestMD5(file);
                return md_str.equals(fileDigestMD5);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
    }

    public static String getFileDigestMD5(File file) throws IOException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
        FileInputStream fis = new FileInputStream(file);
        byte[] dataBytes = new byte[1024];

        int nread;

        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }
        fis.close();

        byte[] mdbytes = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (byte mdbyte : mdbytes) {
            sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static void copyFile (File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }
        try {
            FileChannel source = new FileInputStream(sourceFile).getChannel();
            FileChannel destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            sourceFile.delete();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static File getApplicationBaseDir() {
        String coltBaseDirProp = System.getProperty("colt.base.dir");
        coltBaseDirProp = coltBaseDirProp != null ? coltBaseDirProp : "";

        if (coltBaseDirProp.isEmpty()) {
            String path = ColtUpdater.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
            File file = new File(path);
            while (!new File(file, "flex_sdk").exists()) {
                file = file.getParentFile();
            }
            return file;
        } else if (SystemInfo.isMac && "$APP_PACKAGE".equals(coltBaseDirProp)) {
            File file = new File(ColtUpdater.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            while (!file.getName().equals("COLT.app")) {
                file = file.getParentFile();
            }
            return file;
        }

        return new File(coltBaseDirProp);
    }

    public static void restart() {
        if (SystemInfo.isMac) {
            File baseDir = getApplicationBaseDir();
            if (baseDir.getPath().endsWith(".app")) {
                try {
                    Runtime.getRuntime().exec("open -n -a " + baseDir.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (SystemInfo.isWindows || SystemInfo.isLinux) {
            File executable = getApplicationExecutable();
            if (executable != null && executable.exists()) {
                startExecutable(executable.getPath());
            }
        } else {
            throw new IllegalStateException("Unsupported OS: " + System.getProperty("os.name"));
        }
    }

    public static File getApplicationExecutable() {
        if (SystemInfo.isMac) {
            File executable = new File(getApplicationBaseDir(), "Contents/MacOs/JavaAppLauncher");
            return executable.exists() ? executable : null;
        } else if (SystemInfo.isWindows) {
            File executable = new File(getApplicationBaseDir(), "colt.exe");
            return executable.exists() ? executable : null;
        } else if (SystemInfo.isLinux) {
            File executable = new File(getApplicationBaseDir(), "colt");
            return executable.exists() ? executable : null;
        }

        throw new IllegalStateException("Unsupported OS: " + System.getProperty("os.name"));
    }

    private static void startExecutable(String executable, String... args) {
        ProcessBuilder builder = new ProcessBuilder(executable);

        if (args.length > 0) {
            builder = builder.command(args);
        }

        try {
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
