package com.jereksel.libresubstratum.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Root {

    private static Shell shell;

    public static String runCommand(String command) {
        return getShell().runCommand(command);
    }

    private static Shell getShell() {
        if (shell == null) shell = new Shell();
        return shell;
    }

    public static void closeShell() {
        if (shell != null) shell.close();
    }

    private static class Shell {
        private Process process;
        private BufferedWriter bufferedWriter;
        private BufferedReader bufferedReader;

        private Shell() {
            try {
                process = Runtime.getRuntime().exec("su");
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public String runCommand(String command) {
            try {
                StringBuilder sb = new StringBuilder();
                String callback = "/shellCallback/";
                bufferedWriter.write(command + "\necho " + callback + '\n');
                bufferedWriter.flush();

                char[] buffer = new char[256];
                while (true) {
                    sb.append(buffer, 0, bufferedReader.read(buffer));
                    int i;
                    if ((i = sb.indexOf(callback)) > -1) {
                        sb.delete(i, i + callback.length());
                        break;
                    }
                }
                return sb.toString().trim();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        public void close() {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.write("exit\n");
                    bufferedWriter.flush();

                    bufferedWriter.close();
                }

                if (bufferedReader != null)
                    bufferedReader.close();

                if (process != null) {
                    process.waitFor();
                    process.destroy();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}