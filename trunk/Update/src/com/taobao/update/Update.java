
package com.taobao.update;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Liupeng
 * @version CreateTime:2010-12-27
 */
public class Update {
    private Map<String, File> files = new HashMap<String, File>();
    private Map<String, Long> fileSize = new HashMap<String, Long>();

    public static String java_path = "";
    public static String win_path = "";

    public static String[] update_path = new String[] {"http://momodemo.zhuzhuxc.com/soft/update/", "http://zzupdate.googlecode.com/svn/trunk/Update/"};
    static {
        win_path = System.getProperty("user.dir");
        java_path = win_path.replaceAll("\\\\", "/");
    }

    public List<String> init(final Display display) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(java_path + "/update.properties"))));
            String line = null;
            int i = 0;
            while ((line = br.readLine()) != null) {
                update_path[i] = line.substring(line.indexOf("=") + 1);
                ++i;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> list = new ArrayList<String>();

        String version = new WebService().getVersion();
        if (version == null) {
            display.asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openInformation(display.getActiveShell(), "更新", "版本比对出错！");
                    System.exit(0);
                }
            });
        } else {
            int versionInt = Integer.parseInt(version.replace(".", ""));
            if (versionInt <= getLocalVersion()) {
                return list;
            }
        }

        for (int i = 0; i < update_path.length; i++) {
            try {
                URL url = new URL(update_path[i] + "update.cfg" + "?t=" + new Date().getTime());
                HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
                urlcon.connect();
                InputStream is = urlcon.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = "";
                while ((line = br.readLine()) != null) {
                    long size = Long.parseLong(line.substring(line.indexOf(",") + 1));
                    line = line.substring(0, line.indexOf(","));
                    list.add(line);
                    String filePath = java_path + "/";
                    StringTokenizer st = new StringTokenizer(line, "/");
                    while (st.hasMoreElements()) {
                        filePath = filePath + st.nextElement() + "/";
                        File cFile = new File(filePath);
                        if (!cFile.exists()) {
                            if (st.hasMoreElements()) {
                                cFile.mkdir();
                            } else {
                                cFile.createNewFile();
                            }
                        }
                    }

                    File file = new File(java_path + "/" + line + ".tmp");
                    files.put(line, file);
                    fileSize.put(line, size);
                }
                break;
            } catch (Exception e) {
                if (i == 1) {
                    return null;
                }
                continue;
            }
        }
        return list;
    }

    public boolean download(String path, Display display, Table table) {

        boolean rtn = false;
        for (int i = 0; i < update_path.length; i++) {
            try {
                URL url = new URL(update_path[i] + path + "?t=" + new Date().getTime());
                HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
                urlcon.connect();
                InputStream is = urlcon.getInputStream();
                OutputStream out = new FileOutputStream(files.get(path));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                double len = 0;
                int flag = 0;
                byte[] buffer = new byte[100];
                while ((flag = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, flag);
                    len = len + flag;
                    String per = ((len / fileSize.get(path)) * 100) + "";
                    int pLen = per.substring(per.indexOf(".")).length();
                    if (pLen > 3) {
                        pLen = 3;
                    }
                    per = per.substring(0, per.indexOf(".") + pLen);
                    showTable(display, table, path, per);
                }

                byte[] con = baos.toByteArray();

                out.write(con);
                out.flush();
                is.close();
                out.close();
                rtn = true;
                break;
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return rtn;
    }

    public void main(final Table table, final List<String> list, final Shell shell, final Label state, final Label msg) {
        final Display display = shell.getDisplay();

        boolean rtn = true;
        for (int i = 0; i < list.size(); i++) {
            if (!(rtn = download(list.get(i), display, table))) {
                break;
            }
        }

        if (!rtn) {
            display.asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openInformation(display.getActiveShell(), "更新", "网络不通，组件下载失败！");
                    System.exit(0);
                }
            });
        } else {
            boolean success = true;
            for (int i = 0; i < list.size(); i++) {
                final String line = list.get(i);
                File file = new File(java_path + "/" + line);
                if (file.delete() || !file.exists()) {
                    files.get(list.get(i)).renameTo(new File(java_path + "/" + line));
                } else {
                    success = false;
                    break;
                }
            }
            if (success) {
                display.asyncExec(new Runnable() {
                    public void run() {
                        TableItem item = new TableItem(table, SWT.NONE);
                        item.setText(new String[] {"全部完成", ""});
                        state.setVisible(false);
                        msg.setText("状态：全部完成");
                        System.out.println("ok");
                        shell.close();
                    }
                });

                try {
                    Runtime.getRuntime().exec(java_path + "/jre/bin/java -jar run.jar");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                display.asyncExec(new Runnable() {
                    public void run() {
                        MessageDialog.openInformation(display.getActiveShell(), "更新", "删除旧组件失败！");
                        System.exit(0);
                    }
                });
            }
        }
    }

    private void showTable(Display display, final Table table, final String line, final String per) {
        display.asyncExec(new Runnable() {
            public void run() {
                TableItem items[] = table.getItems();
                for (int i = 0; i < items.length; i++) {
                    if(items[i].getText(0).equals(line)){
                        items[i].setText(1, per+"%");
                    }
                }
            }
        });
    }

    private int getLocalVersion() {
        int localVersion = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(java_path + "/version"))));
            String line = br.readLine();
            if (line != null && !line.equals("")) {
                localVersion = Integer.parseInt(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return localVersion;
    }
}
